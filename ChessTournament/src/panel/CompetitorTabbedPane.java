package panel;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import model.Competitor;
import model.Database;
import model.Tournament;

public class CompetitorTabbedPane extends JFrame {
	private static final long serialVersionUID = -1732368493930988952L;
	final Tournament turniej;
	Database DB;
	JMenuBar menuBar;
	JMenu comp, about;
	JMenuItem addC, rndC, authors, manual;
	ShowEditCompetitorPanel showPanel;
	TournamentPanel tournamentPanel;
	GroupsPanel groupsPanel;
	JTabbedPane tabbedPane = new JTabbedPane();
	
	public CompetitorTabbedPane(Tournament turniej){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.turniej = turniej;
		this.DB = new Database();
		this.turniej.setRounds(5);
		this.turniej.setRoundsCompleted(-1);
		showPanel = new ShowEditCompetitorPanel(turniej, DB);
		tournamentPanel = new TournamentPanel(turniej, DB);
		groupsPanel = new GroupsPanel(turniej, DB);
		setMinimumSize(new Dimension(400,300));
		setSize(700,500);
		//setResizable(false);
		setTitle("ChessTournament alpha v0.01");
		setMenu();
	    
	    tabbedPane.add("Pokaż lub edytuj dodanych uczestników", showPanel);
	    tabbedPane.add("Turniej", tournamentPanel);
	    tabbedPane.add("Podział na grupy", groupsPanel);
	    tabbedPane.addChangeListener((e) -> {
			int i = tabbedPane.getSelectedIndex();
			if(i==0) {
				showPanel.setData();
				comp.setVisible(true);
			} else {
				comp.setVisible(false);
			}
			if(i==1) tournamentPanel.setSBBounds();
		});
	    	    	    
	    add(tabbedPane);
	    setVisible(true);
	    addWindowListener(new WindowAdapter() {
	    	@Override
	    	public void windowClosing(WindowEvent e) {
	    		DB.close();
	    	}
		});
	}
	
	/**
	 * Tworzy i dodaje elementy menu, akcje po ich wywołaniu i skróty
	 */
	private void setMenu() {
		menuBar	= new JMenuBar();
		comp 	= new JMenu("Uczestnicy");
		about 	= new JMenu("O programie");
		addC 	= new JMenuItem("Dodaj");
		rndC 	= new JMenuItem("Dodaj losowego gracza");
		addC.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_N, 
		        java.awt.Event.CTRL_MASK));
		rndC.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_L, 
		        java.awt.Event.CTRL_MASK));
		authors	= new JMenuItem("Autorzy");
		manual	= new JMenuItem("Pomoc");
		comp.add(addC);
		comp.add(rndC);
		about.add(authors);
		about.add(manual);
		menuBar.add(comp);
		menuBar.add(about);
		setJMenuBar(menuBar);
		addC.addActionListener((e) -> {
			 if(!showPanel.isEditAllowed()) return;
			 Competitor c = new Competitor(null, "Imie", "Nazwisko", 0, 0, false, null);
			 DB.insertOrUpdateCompetitor(c, turniej.getId());
			 showPanel.setData();
		});
		rndC.addActionListener((e) -> {
			 if(!showPanel.isEditAllowed()) return;
			 Competitor c = Simulator.RandomPlayer();
			 DB.insertOrUpdateCompetitor(c, turniej.getId());
			 showPanel.setData();
		});
	}
}