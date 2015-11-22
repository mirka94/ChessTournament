package panel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Competitor;
import model.Database;
import model.Tournament;

public class CompetitorTabbedPane extends JFrame implements WindowListener {
	private static final long serialVersionUID = -1732368493930988952L;
	final Tournament turniej;
	Database DB;
	JMenuBar menuBar;
	JMenu comp, about;
	JMenuItem addC, rndC;
	//, authors, manual;
	ShowEditCompetitorPanel showPanel;
	TournamentPanel tournamentPanel;
	GroupsPanel groupsPanel;
	JTabbedPane tabbedPane = new JTabbedPane();
	
	public CompetitorTabbedPane(Tournament turniej){
		this.turniej = turniej;
		this.DB = new Database();
		showPanel = new ShowEditCompetitorPanel(turniej.getId(), DB);
		tournamentPanel = new TournamentPanel(turniej, DB);
		groupsPanel = new GroupsPanel(turniej, DB);
		//setMinimumSize(new Dimension(400,300));
		setSize(700,500);
		setResizable(false);
		setTitle("ChessTournament alpha v0.01");
		setMenu();
	    
	    addWindowListener(this);
	    
	    tabbedPane.add("Pokaż lub edytuj dodanych uczestników", showPanel);
	    tabbedPane.add("Turniej", tournamentPanel);
	    tabbedPane.add("Podział na grupy", groupsPanel);
	    tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int i = tabbedPane.getSelectedIndex();
				if(i==0) {
					showPanel.setData();
					comp.setVisible(true);
				} else {
					comp.setVisible(false);
				}
				if(i==1) tournamentPanel.setSBBounds();
			}
		});
	    	    	    
	    add(tabbedPane);
	    setVisible(true);
	}
	
	/**
	 * Tworzy i dodaje elementy menu, akcje po ich wywołaniu i skróty
	 */
	private void setMenu() {
		menuBar	= new JMenuBar();
		comp 	= new JMenu("Uczestnicy");
		//about 	= new JMenu("O programie");
		addC 	= new JMenuItem("Dodaj");
		rndC 	= new JMenuItem("Dodaj losowego gracza");
		addC.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_N, 
		        java.awt.Event.CTRL_MASK));
		rndC.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_L, 
		        java.awt.Event.CTRL_MASK));
		//authors	= new JMenuItem("Autorzy");
		//manual	= new JMenuItem("Pomoc");
		comp.add(addC);
		comp.add(rndC);
		//about.add(authors);
		//about.add(manual);
		menuBar.add(comp);
		//menuBar.add(about);
		setJMenuBar(menuBar);
		addC.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				 Competitor c = new Competitor(null, "Imie", "Nazwisko", 0, 0, false);
				 DB.insertOrUpdateCompetitor(c, turniej.getId());
				 showPanel.setData();
			}
		});
		rndC.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				 Competitor c = Simulator.RandomPlayer();
				 DB.insertOrUpdateCompetitor(c, turniej.getId());
				 showPanel.setData();
			}
		});
	}

	@Override
	public void windowClosing(WindowEvent e) {
		DB.close();
	}
	@Override public void windowOpened(WindowEvent e)		{}
	@Override public void windowActivated(WindowEvent e)	{}
	@Override public void windowClosed(WindowEvent e)		{}
	@Override public void windowDeactivated(WindowEvent e)	{}
	@Override public void windowDeiconified(WindowEvent e)	{}
	@Override public void windowIconified(WindowEvent e)	{}
}
