package panel;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import model.Competitor;
import model.Database;
import model.Tournament;
import tools.Simulator;

public class CompetitorTabbedPane extends JFrame {
	private static final long serialVersionUID = -1732368493930988952L;
	final Tournament turniej;
	private Database DB;
	private JMenuBar menuBar;
	private JMenu comp, about, sort, group;
	private JMenuItem addC, rndC, authors, manual, sortDefault, sortRandom, autoGroup;
	private LinkedHashMap<JMenuItem, Competitor.SortOption> sortOptions;
	private ShowEditCompetitorPanel showPanel;
	private TournamentPanel tournamentPanel;
	private GroupsPanel groupsPanel;
	private JTabbedPane tabbedPane = new JTabbedPane();
	//private RoundPanel roundPanel;
	
	public CompetitorTabbedPane(Tournament turniej){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.turniej = turniej;
		this.DB = new Database();
		showPanel = new ShowEditCompetitorPanel(turniej, DB);
		tournamentPanel = new TournamentPanel(turniej, DB);
		groupsPanel = new GroupsPanel(turniej, DB, ()->{
			group.setVisible(false);
			//getContentPane().removeAll();
			tabbedPane.add("Rozgrywki w eliminacjach", new RoundPanel());
			tabbedPane.setSelectedIndex(3);
		});
		//roundPanel = new RoundPanel(turniej);
		setMinimumSize(new Dimension(400,300));
		setSize(700,500);
		//setResizable(false);
		setTitle("ChessTournament alpha v0.2");
		setMenu();
	    tabbedPane.add("Pokaż lub edytuj dodanych uczestników", showPanel);
	    tabbedPane.add("Turniej", tournamentPanel);
	    tabbedPane.add(groupsPanel);
	    tabbedPane.add("Rozgrywki", groupsPanel);
	    //tabbedPane.add("Rundy", roundPanel);
	    tabbedPane.addChangeListener((e) -> {
			int i = tabbedPane.getSelectedIndex();
			if(i==0) showPanel.setData();
			if(i==1) tournamentPanel.setSBBounds();
			if(i==2) groupsPanel.initComponents();
			comp.setVisible( isEditAllowed() && i==0);
			group.setVisible(isEditAllowed() && i==2 && !turniej.isSwiss());
			sort.setVisible(i==2);
		});
	    	    	    
	    add(tabbedPane);
	    setVisible(true);
	    addWindowListener(new WindowAdapter() {
	    	@Override
	    	public void windowClosing(WindowEvent e) {
	    		DB.close();
	    	}
		});
	    turniej.addTypeChangeListener((type) -> {
	    	if(type== Tournament.Type.SWISS) 
	    		tabbedPane.setTitleAt(2, "Rozpocznij przygotowanie rundy 1.");
	    	else 
	    		tabbedPane.setTitleAt(2, "Podział na grupy");
	    });
	    turniej.setType(turniej.getType()); // wygląda bezsensownie, ale odpala powyższy listener
	}
	
	/**
	 * Tworzy i dodaje elementy menu, akcje po ich wywołaniu i skróty
	 */
	private void setMenu() {
		sortOptions = new LinkedHashMap<>();
		menuBar	= new JMenuBar();
		comp 	= new JMenu("Uczestnicy");
		about 	= new JMenu("O programie");
		sort	= new JMenu("Sortowanie graczy");
		group 	= new JMenu("Automatyczne grupowanie graczy");
		addC 	= new JMenuItem("Dodaj");
		rndC 	= new JMenuItem("Dodaj losowego gracza");
		autoGroup = new JMenuItem("Grupuj");
		addC.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_N, Event.CTRL_MASK));
		rndC.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_L, Event.CTRL_MASK));
		sortDefault = new JMenuItem("Sortowanie domyślne");
		sortRandom = new JMenuItem("Kolejność losowa");
		sortOptions.put(new JMenuItem("Wiek - rosnąco")			, Competitor.SortOption.AGE_ASC);
		sortOptions.put(new JMenuItem("Wiek - malejąco")		, Competitor.SortOption.AGE_DESC);
		sortOptions.put(new JMenuItem("Kategoria - rosnąco")	, Competitor.SortOption.CHESSCATEGORY_ASC);
		sortOptions.put(new JMenuItem("Kategoria - malejąco")	, Competitor.SortOption.CHESSCATEGORY_DESC);
		sortOptions.put(new JMenuItem("Imię - rosnąco")			, Competitor.SortOption.NAME_ASC);
		sortOptions.put(new JMenuItem("Imię - malejąco")		, Competitor.SortOption.NAME_DESC);
		sortOptions.put(new JMenuItem("Nazwisko - rosnąco")		, Competitor.SortOption.SURNAME_ASC);
		sortOptions.put(new JMenuItem("Nazwisko - malejąco")	, Competitor.SortOption.SURNAME_DESC);
		authors	= new JMenuItem("Autorzy");
		manual	= new JMenuItem("Pomoc");
		comp.add(addC);
		comp.add(rndC);
		about.add(authors);
		about.add(manual);
		sort.add(sortDefault);
		sort.add(sortRandom);
		sortOptions.keySet().forEach((jmi) -> sort.add(jmi));
		group.add(autoGroup);
		menuBar.add(comp);
		menuBar.add(sort);
		menuBar.add(group);
		menuBar.add(about);
		setJMenuBar(menuBar);
		addC.addActionListener((e) -> {
			 if(!showPanel.isEditAllowed()) return;
			 Competitor c = new Competitor(null, "Imie", "Nazwisko", 0, 0, false, null); //dodać 0
			 DB.insertOrUpdateCompetitor(c, turniej.getId());
			 showPanel.setData();
		});
		rndC.addActionListener((e) -> {
			 if(!showPanel.isEditAllowed()) return;
			 Competitor c = null;
			try {
				c = Simulator.RandomPlayer();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			 DB.insertOrUpdateCompetitor(c, turniej.getId());
			 showPanel.setData();
		});
		sortDefault.addActionListener((e) -> {
			groupsPanel.sortDefault();
		});
		autoGroup.addActionListener((e) -> groupsPanel.autoGroup());
		sortRandom.addActionListener((e)->groupsPanel.shuffle());
		sortOptions.keySet().forEach((jmi) -> 
			jmi.addActionListener((e) -> {
				groupsPanel.stableSort(sortOptions.get(jmi));
			})
		);
		sort.setVisible(false);
		group.setVisible(false);
	}
	
	public boolean isEditAllowed() {
		return turniej.getRoundsCompleted()<0;
	}
}