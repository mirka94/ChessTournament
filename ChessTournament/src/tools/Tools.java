package tools;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import model.Competitor;
import model.SingleGame;
import window.AddTPanel;
import window.ShowTPanel;

public class Tools {
	public static void checkGroups(int groups, List<Competitor> competitors) {
		competitors.forEach(c -> { 
			if(c.getGroup()!=null && c.getGroup()+1>groups) c.setGroup(null); 
		});
	}
	
	
	/**
	 * @param competitors lista uczestników
	 * @return lista grup, każda zawierająca listę uczestników
	 */
	public static TreeMap<Integer, List<Competitor>> groupsList(List<Competitor> competitors) {
		TreeMap<Integer, List<Competitor>> groupsList = new TreeMap<>();
		for(Competitor c : competitors) {
			int g = c.getGroup();
			if(!groupsList.containsKey(g)) 
				groupsList.put(g, new ArrayList<>());
			groupsList.get(g).add(c);
		}
		return groupsList;
	}
	
	public static List<SingleGame> generateSingleGames(TreeMap<Integer, List<Competitor>> groupsList, int boards) {
		List<SingleGame> rawResult = new ArrayList<>();
		
		for(Integer i : groupsList.keySet()) {
			List<Competitor> l = groupsList.get(i);
			Collections.shuffle(l);
			for(SGInfo sgi : roundRobinGamesInfo(l.size())) {
				rawResult.add(
					new SingleGame(l.get(sgi.c1-1), l.get(sgi.c2-1), sgi.r-1, -1)
				);
			}
		}
		rawResult.sort((c1,c2) -> c1.getRound()-c2.getRound());
		return rawResult;
		//return determineBoards(rawResult, boards);
	}
	
	public static List<SingleGame> generateFinaleSingleGames(List<Competitor> finaleCompetitors, List<SingleGame> played, int boards) {
		List<SingleGame> rawResult = new ArrayList<>();
		Collections.shuffle(finaleCompetitors);
		for(SGInfo sgi : roundRobinGamesInfo(finaleCompetitors.size())) {
			Competitor c1 = finaleCompetitors.get(sgi.c1-1), c2 = finaleCompetitors.get(sgi.c2-1);
			SingleGame nSG = new SingleGame(c1,c2, -1, -1);
			if(!played.contains(nSG))rawResult.add(nSG);
		}
		return rawResult;
		//return determineBoards(rawResult, boards);
	}
	/*
	private static List<SingleGame> determineBoards(List<SingleGame> rawResult, int boards) {
		List<SingleGame> result = new ArrayList<>();
		int board=0;
		for(SingleGame sg : rawResult) 
			result.add(new SingleGame(
				null,
				sg.getCompetitorW(), 
				sg.getCompetitorB(), 
				sg.getScore(),
				sg.getRound(), 
				board++%boards)
			);
		return result;
	}
	*/
	
	public static List<SGInfo> roundRobinGamesInfo(int g) {
		LinkedList<SGInfo> result = new LinkedList<>();
		int gp = (g%2==1) ? g+1 : g;
		for(int i=1; i<gp; ++i) {
			if(i%2==1) {
				if(g==gp) result.add(new SGInfo(1+i/2,gp,i));
				for(int j=2; j<=gp/2; ++j)
					result.add(new SGInfo((j+i/2),(gp-j+i/2)%(gp-1)+1,i));
			}
			else {
				if(g==gp) result.add(new SGInfo(gp,(gp/2+i/2),i));
				for(int j=2; j<=gp/2; ++j)
					result.add(new SGInfo((gp/2+j+i/2-2)%(gp-1)+1,(gp/2-j+i/2)%(gp-1)+1,i));
			}
		}
		return result;
	}
	
	static class SGInfo {
		int c1, c2, r;
		public SGInfo(int c1, int c2, int r) {
			this.c1 = c1;
			this.c2 = c2;
			this.r = r;
		}
	}
	
	
	public static JMenuBar aboutMenu(final JMenuBar menuBar, JFrame frame) {
		JMenu 	mnTurniej 	 = new JMenu("Turniej"),
				mnOProgramie = new JMenu("O programie");
		menuBar.add(mnTurniej);
		menuBar.add(mnOProgramie);

		JMenuItem 	mntmPomoc 		= new JMenuItem("Pomoc"),
					dodajTurniej 	= new JMenuItem("Dodaj turniej"),
					wybierzTurniej	= new JMenuItem("Wybierz turniej"),
					mntmAutorzy 	= new JMenuItem("Autorzy"),
					mntmOpis 		= new JMenuItem("Opis");
		
		mntmPomoc.setAlignmentY(Component.TOP_ALIGNMENT);
		
		mnTurniej.add(dodajTurniej);
		mnTurniej.add(wybierzTurniej);
		mnOProgramie.add(mntmPomoc);
		mnOProgramie.add(mntmAutorzy);
		mnOProgramie.add(mntmOpis);
		
		dodajTurniej.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_F2, 0));
		wybierzTurniej.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_F3, 0));
		
		dodajTurniej.addActionListener(e -> {
				frame.getContentPane().removeAll();
				frame.add(new AddTPanel(frame), BorderLayout.CENTER);
				frame.pack();
		});
		wybierzTurniej.addActionListener(e -> {
				frame.getContentPane().removeAll();
				frame.add(new ShowTPanel(frame), BorderLayout.CENTER);
				frame.pack();
		});
		// otwieranie pdf z instrukcją po wybraniu pomocy
		mntmPomoc.addActionListener(e->{
			if(Desktop.isDesktopSupported()) {
			    try {
			        File myFile = new File("turniej.pdf");
			        Desktop.getDesktop().open(myFile);
			    } catch (IOException ex) {
			        System.out.println(e);
			    }
			}
		});
		
		mntmAutorzy.addActionListener(e->Dialogs.autorzy());
		mntmOpis.addActionListener(e->Dialogs.opis());
		
		return menuBar;
	}
}