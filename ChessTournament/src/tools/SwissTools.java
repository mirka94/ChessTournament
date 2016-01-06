package tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import model.Competitor;
import model.SingleGame;
import model.Tournament;

public class SwissTools {
	public static String genJaVaFoData(Tournament tournament, List<Competitor> competitors, List<SingleGame> games) {
		String result="XXR "+tournament.getRounds();
		Map<Integer,Competitor> compBySwissId = competitors.stream().collect(Collectors.toMap(c->c.getGroup(), c->c));
		int n = competitors.size();
		if(compBySwissId.size()<n || 
			Collections.min(compBySwissId.keySet())!=0 || 
			Collections.max(compBySwissId.keySet())!=n-1
			) return null;
		
		Map<Competitor, Map<Integer, SingleGame>> gamesByComp = gamesByComp(tournament, competitors,games);
		if(gamesByComp==null) System.err.println("Null gamesByCompetitor");
		Map<Competitor,String> JaVaFoCodedCompetitorGames = gamesByComp==null ? null :
				JaVaFoCodedCompetitorGames(gamesByComp, tournament.getRoundsCompleted(), tournament.getRounds());
		
		for(int i=0; i<n; ++i) {
			Competitor c = compBySwissId.get(i);
			String  name = c.toString().replaceAll("[^a-zA-Z ]", "");
			if(name.length()>32) name = name.substring(0, 32);
			result+="\n001"+String.format(Locale.US, "%1$5s      %2$-32s%3$38.1f     ", i+1, name, 0.0f);
			if(gamesByComp!=null) result+=JaVaFoCodedCompetitorGames.get(c);
		}
		
		return result;
	}
	
	private static Map<Competitor,Map<Integer,SingleGame>> gamesByComp(Tournament tournament, List<Competitor> competitors, List<SingleGame> games) {
		Map<Integer,Competitor> compById = competitors.stream().collect(Collectors.toMap(c->c.getId(), c->c));
		Map<Competitor,Map<Integer,SingleGame>> result = new TreeMap<>();
		for(Competitor c : competitors) result.put(c, new TreeMap<>());
		for(SingleGame sg : games) {
			result.get(compById.get(sg.getCompetitorB())).put(sg.getRound(), sg);
			result.get(compById.get(sg.getCompetitorW())).put(sg.getRound(), sg);
		}
		/* jeszcze sprawdzenie poprawności - ilości rozgrywek 
		 * (z włączeniem wolnego losu i przegranej z dyskwalifikacji) 
		 * rozgranych przez danego gracza - ma być dla wszystkich graczy
		 * jednakowa i równa ilości rund rozegranych w turnieju		
		*/
		List<Integer> sizeCheckTemp = result.values().stream().map(m->m.size()).collect(Collectors.toList());
		int expected = tournament.getRoundsCompleted();
		if(expected>0) {
			int min = Collections.min(sizeCheckTemp);
			int max = Collections.max(sizeCheckTemp);
			if(min!=max || min!=expected) {
				System.err.println("Sprawdzenie poprawności ilości rozgrywek - min: "
						+min+", max = "+max+", spodziewano się "+expected);
				return null;
			}
		}
		System.out.println(result);
		return result;
	}
	
	private static Map<Competitor,String> JaVaFoCodedCompetitorGames(
			Map<Competitor,Map<Integer,SingleGame>> gamesByComp, 
			int roundsCompleted, int roundsTotal) {
		Map<Competitor,String> result = new TreeMap<>();
		Map<Integer, Competitor> competitorMap = gamesByComp.keySet().stream()
				.collect(Collectors.toMap(c->c.getId(), c->c));
		for(Competitor c : gamesByComp.keySet()) {
			String code = "";
			Map<Integer,SingleGame> gamesByN = gamesByComp.get(c);
			int size = gamesByN.size();
			
			for(int i=0; i<size; ++i) {
				SingleGame sg = gamesByN.get(i);
				Competitor cW = competitorMap.get(sg.getCompetitorW()); // gra białymi
				Competitor cB = competitorMap.get(sg.getCompetitorB()); // gra czarnymi
				int score = sg.getScore(); // 1 - wygrały białe, 2 - czarne, 3 - remis;
				boolean wasPlayed = sg.getWasPlayed();
				if(cW.equals(cB)/*equals c*/) { // otrzymany wolny los lub gracz zdyskwalifikowany przed przygotowaniem parowań
					if(score==0) 		code+="  0000 - -";
					else if(score==1) 	code+="  0000 - +";
				}
				else if(wasPlayed) { // gra rozegrana w całości (mat / pat)
					if(c.equals(cW)) {
						if(score==1)		code+=String.format("  %1$4s w 1", cB.getGroup()+1);
						else if(score==2)	code+=String.format("  %1$4s w 0", cB.getGroup()+1);
						else if(score==3)   code+=String.format("  %1$4s w =", cB.getGroup()+1);
					}
					else if(c.equals(cB)) {
						if(score==1)		code+=String.format("  %1$4s b 0", cB.getGroup()+1);
						else if(score==2)	code+=String.format("  %1$4s b 1", cB.getGroup()+1);
						else if(score==3)   code+=String.format("  %1$4s b =", cB.getGroup()+1);
					}
				}
				else { // gra nie została rozegrana w całości, a np. z powodu dyskwalifikacji jednego lub obu zawodników
					if(c.equals(cW)) {
						if(score==1)   		code+=String.format("  %1$4s - +", cB.getGroup()+1);
						else if(score==2)   code+=String.format("  %1$4s - -", cB.getGroup()+1);
						else if(score==3)   code+="  0000 - =";
					}
					else if(c.equals(cB)) {
						if(score==1)		code+=String.format("  %1$4s - -", cB.getGroup()+1);
						else if(score==2)	code+=String.format("  %1$4s - +", cB.getGroup()+1);
						else if(score==3)   code+="  0000 - =";
					}
				}
				if(c.getIsDisqualified()) {
					for(int l=0; l+roundsCompleted<roundsTotal; ++l) code+="  0000 - -";
				}
			}
			result.put(c, code);
		}
		return result;
	}
	
	public static boolean saveJaVaFoData(String data) {
		try {
			PrintWriter out = new PrintWriter("JaVaFoData.txt");
			out.print(data);
			out.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean runJaVaFo() {
		try {
			File out = new File("JaVaFoPairings.txt");
			if(out.exists()) out.delete();
			Runtime.getRuntime().exec("java -jar javafo.jar JaVaFoData.txt -p JaVaFoPairings.txt").waitFor();
			
			return new File("JaVaFoPairings.txt").exists();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static List<SingleGame> readPairings(List<Competitor> competitors, final int round) {
		Map<Integer,Competitor> compBySwissId = competitors.stream().collect(Collectors.toMap(c->c.getGroup(), c->c));
		List<Competitor> playingCompetitors = new ArrayList<>(competitors.size());
		List<SingleGame> result = null;
		try {
			File in = new File("JaVaFoPairings.txt");
			List<String> data = Files.lines(in.toPath()).collect(Collectors.toList());
			int n = Integer.parseInt(data.get(0));
			if(n==data.size()-1) {
				result = new ArrayList<>(n-1);
				for(int i=1; i<=n; ++i) {
					String sgInfo = data.get(i);
					int cutPos = sgInfo.indexOf(" ");
					if(cutPos<1 || cutPos>sgInfo.length()-1) {
						System.err.println("JaVaFoPairings.txt file - pairing error");
						return null;
					}
					Integer cWid = Integer.parseInt(sgInfo.substring(0, cutPos))-1;
					Integer cBid = Integer.parseInt(sgInfo.substring(cutPos+1))-1;
					Competitor cW = compBySwissId.get(cWid);
					playingCompetitors.add(cW);
					if(cBid!=-1) { 
						Competitor cB = compBySwissId.get(cBid);
						playingCompetitors.add(cB);
						result.add(new SingleGame(cW, cB, round, 0));
					}
					else result.add(new SingleGame(cW,round,cW.getIsDisqualified()?0:1)); // bye | dyskwalifikacja
				}
				playingCompetitors.forEach(c->competitors.remove(c));
				for(Competitor c : competitors) 
					result.add(new SingleGame(c,round,c.getIsDisqualified()?0:1)); // bye | dyskwalifikacja
			} else System.err.println("JaVaFoPairings.txt file - size error");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
