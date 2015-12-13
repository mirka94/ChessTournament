package panel;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import model.Competitor;
import model.Competitor.SortOption;
import model.Database;
import model.Tournament;
import tools.Simulator;

public class CompetitorTabbedPane extends JPanel {
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
	private GamesPanel gamesPanel;
	private GroupsChoosePanel groupsChoosePanel;
	private FinaleGamesPanel finaleGamesPanel;
	private JTabbedPane tabbedPane = new JTabbedPane();
	
	public CompetitorTabbedPane(Tournament turniej, JFrame frame){
		this.turniej = turniej;
		this.DB = new Database();
		showPanel = new ShowEditCompetitorPanel(turniej, DB);
		tournamentPanel = new TournamentPanel(turniej, DB);
		FinaleGamesPanel.onFinalesEndListener finEndListener = ()->{
			turniej.setRoundsCompleted(3);
			DB.insertOrUpdateTournament(turniej);
			tabbedPane.add("Wyniki turnieju", new FinaleScorePanel(turniej, DB));
			tabbedPane.setSelectedIndex(6);
		};
		GroupsChoosePanel.onFinaleStartListener finStartListener = ()->{ // po rozpoczęciu finałów
			turniej.setRoundsCompleted(2);
			DB.insertOrUpdateTournament(turniej);
			finaleGamesPanel = new FinaleGamesPanel(turniej, DB, finEndListener);
			tabbedPane.add("Rozgrywki finałowe", finaleGamesPanel);
			tabbedPane.setSelectedIndex(5); 
		}; 
		GamesPanel.onEliminationsEndListener elEndListener = ()->{  // po zakończeniu eliminacji
			turniej.setRoundsCompleted(1);
			DB.insertOrUpdateTournament(turniej);
			groupsChoosePanel = new GroupsChoosePanel(turniej, DB, finStartListener);
			tabbedPane.add("Wybór graczy do finałów", groupsChoosePanel);
			tabbedPane.setSelectedIndex(4);
		};
		GroupsPanel.onTournamentStartListener tStartlistener = ()->{ // po rozpoczęciu turnieju
			turniej.setRoundsCompleted(0);
			DB.insertOrUpdateTournament(turniej);
			group.setVisible(false);
			gamesPanel = new GamesPanel(turniej, DB, elEndListener);
			tabbedPane.add("Rozgrywki w eliminacjach", gamesPanel);
			tabbedPane.setSelectedIndex(3);
		};
		groupsPanel = new GroupsPanel(turniej, DB, tStartlistener);
		setMenu(frame);
	    tabbedPane.add("Pokaż lub edytuj dodanych uczestników", showPanel);
	    tabbedPane.add("Turniej", tournamentPanel);
	    tabbedPane.add(groupsPanel);
	    tabbedPane.add("Rozgrywki", groupsPanel);
	    tabbedPane.addChangeListener((e) -> {
			int i = tabbedPane.getSelectedIndex();
			if(i==0) showPanel.setData();
			if(i==1) tournamentPanel.setSBBounds();
			if(i==2) groupsPanel.initComponents();
			if(i==4) groupsChoosePanel.initComponents();
			comp.setVisible( isEditAllowed() && i==0);
			group.setVisible(isEditAllowed() && i==2 && !turniej.isSwiss());
			sort.setVisible(i==2);
		});
	    	    	    
	    frame.add(tabbedPane);
	    setVisible(true);
	    frame.addWindowListener(new WindowAdapter() {
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
	    int roundsCompleted = turniej.getRoundsCompleted();
	    if(!isEditAllowed()) tStartlistener.onTournamentStart();
	    if(roundsCompleted>0) elEndListener.onEliminationsEnd();
	    if(roundsCompleted>1) finStartListener.onFinaleStart();
	    if(roundsCompleted>2) finEndListener.onFinalesEnd();
	}
	
	/**
	 * Tworzy i dodaje elementy menu, akcje po ich wywołaniu i skróty
	 */
	private void setMenu(JFrame frame) {
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
		sortOptions.put(new JMenuItem("Wiek - rosnąco")			, SortOption.AGE_ASC);
		sortOptions.put(new JMenuItem("Wiek - malejąco")		, SortOption.AGE_DESC);
		sortOptions.put(new JMenuItem("Kategoria - rosnąco")	, SortOption.CHESSCATEGORY_ASC);
		sortOptions.put(new JMenuItem("Kategoria - malejąco")	, SortOption.CHESSCATEGORY_DESC);
		sortOptions.put(new JMenuItem("Imię - rosnąco")			, SortOption.NAME_ASC);
		sortOptions.put(new JMenuItem("Imię - malejąco")		, SortOption.NAME_DESC);
		sortOptions.put(new JMenuItem("Nazwisko - rosnąco")		, SortOption.SURNAME_ASC);
		sortOptions.put(new JMenuItem("Nazwisko - malejąco")	, SortOption.SURNAME_DESC);
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
		frame.setJMenuBar(menuBar);
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