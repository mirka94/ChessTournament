package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import tools.Dialogs;

/**
 * @author PiotrJ
 * Klasa odpowiadająca za obsługę bazy danych
 */
@SuppressWarnings (value="unchecked")
public class Database {
	Map<Integer,Tournament> tournaments = new TreeMap<>();
	Map<Integer,Competitor> competitors = new TreeMap<>();
	Map<Integer,SingleGame> singleGames = new TreeMap<>();
	public Database() {
		File theDir = new File("data");
		// if the directory does not exist, create it
		if (!theDir.exists()) {
		    System.out.println("Creating data/ directory");
		    try{
		        theDir.mkdir();
		    } 
		    catch(SecurityException se){
		        System.err.println("Could not create data/ directory");
		    }
		}
	}
	public void close(){}
	private static Object readObject(String filename) {
		Object result = null;
		try {
		    File file = new File("data/"+filename);
		    FileInputStream f = new FileInputStream(file);
		    ObjectInputStream s = new ObjectInputStream(f);
		    result = s.readObject();
		    s.close();
		} catch(FileNotFoundException e) {
			System.err.println("Warning: file "+filename+" can not be found");
		} catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
			Dialogs.bladBazy();
		}
		return result;
	}
	public static <T> void writeObject(String filename, Map<Integer,T> o) {
		try {
	        File file = new File("data/"+filename);
	        FileOutputStream f = new FileOutputStream(file);
	        ObjectOutputStream s = new ObjectOutputStream(f);
	        s.writeObject(new ArrayList<T>(o.values()));
	        s.close();
		} catch(IOException e) {
			e.printStackTrace();
			Dialogs.bladBazy();
		}
	}
	public void readTournaments() {
		ArrayList<Tournament> rawTournaments = (ArrayList<Tournament>) 
				readObject("tournaments.data");
		tournaments = rawTournaments==null ? new TreeMap<>() : rawTournaments.stream()
				.collect(Collectors.toMap(o->o.getId(),o->o));
	}
	public void writeTournaments() {
		writeObject("tournaments.data", tournaments);
	}
	public void readCompetitors(int t) {
		ArrayList<Competitor> rawCompetitors = (ArrayList<Competitor>) 
				readObject("competitors"+t+".data");
		competitors = rawCompetitors==null ? new TreeMap<>() : rawCompetitors.stream()
				.collect(Collectors.toMap(o->o.getId(),o->o));
	}
	public void writeCompetitors(int t) {
		writeObject("competitors"+t+".data", competitors);
	}
	public void readSingleGames(int t) {
		ArrayList<SingleGame> rawGames = (ArrayList<SingleGame>) 
				readObject("games"+t+".data");
		singleGames = rawGames==null ? new TreeMap<>() : rawGames.stream()
				.collect(Collectors.toMap(o->o.getId(),o->o));
	}
	public void writeSingleGames(int t) {
		writeObject("games"+t+".data", singleGames);
	}
	public void insertOrUpdateCompetitor(Competitor c, Integer turniej) {
		if(c.getId()==null) 
			if(competitors.isEmpty()) c.setId(0);
			else c.setId(Collections.max(competitors.keySet())+1);
		competitors.put(c.getId(), c);
		writeCompetitors(turniej);
	}
	public void insertOrUpdateTournament(Tournament t) {
		if(tournaments.isEmpty()) readTournaments();
		if(t.getId()==null)
			if(tournaments.isEmpty()) t.setId(0);
			else t.setId(Collections.max(tournaments.keySet())+1);
		tournaments.put(t.getId(), t.copy());
		writeTournaments();
	}
	public void insertOrUpdateSingleGame(SingleGame g, Integer turniej) {
		if(g.getId()==null) 
			if(singleGames.isEmpty()) g.setId(0);
			else g.setId(Collections.max(singleGames.keySet())+1);
		singleGames.put(g.getId(), g);
		writeSingleGames(turniej);
	}
	public void insertOrUpdateSingleGame(List<SingleGame> insSingleGames, Integer turniej) {
		for(SingleGame g : insSingleGames) {
			if(g.getId()==null) 
				if(singleGames.isEmpty()) g.setId(0);
				else g.setId(Collections.max(singleGames.keySet())+1);
			singleGames.put(g.getId(), g);
		}
		writeSingleGames(turniej);
	}
	public List<Competitor> getCompetitors(int turniej) {
		readCompetitors(turniej);
		return competitors.values().stream().collect(Collectors.toList());
	}
	public List<Tournament> getTournaments() {
		readTournaments();
		return tournaments.values().stream().collect(Collectors.toList());
	}
	public List<SingleGame> getSingleGames(int turniej, boolean finaly) {
		readSingleGames(turniej);
		if(!finaly) {
			return singleGames.values().stream()
				.filter(sg->sg.getRound()!=-1)
				.collect(Collectors.toList());
		}
		else {
			List<Integer> finaleCompetitorsIds = competitors.values().stream()
				.filter(c->c.getGoesFinal())
				.map(c->c.getId())
				.collect(Collectors.toList());
			return singleGames.values().stream()
				.filter(sg->
					finaleCompetitorsIds.contains(sg.getCompetitorB()) && 
					finaleCompetitorsIds.contains(sg.getCompetitorW())
				).collect(Collectors.toList());
		}
	}
	public void removeCompetitor(int id, int turniej) {
		competitors.remove(id);
		writeCompetitors(turniej);
	}
}