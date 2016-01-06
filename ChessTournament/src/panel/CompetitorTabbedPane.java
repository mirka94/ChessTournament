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
import res.Strings;
import model.Database;
import model.Tournament;
import tools.Simulator;
import tools.Tools;

public class CompetitorTabbedPane extends JPanel {
	final Tournament turniej;
	private Database DB;
	private JMenuBar menuBar;
	private JMenu comp, sort, group;
	private JMenuItem addC, rndC, sortDefault, sortRandom, autoGroup;
	private LinkedHashMap<JMenuItem, Competitor.SortOption> sortOptions;
	private ShowEditCompetitorPanel showPanel;
	private TournamentPanel tournamentPanel;
	private GroupsPanel groupsPanel;
	private AbstractGamesPanel gamesPanel;
	private GroupsChoosePanel groupsChoosePanel;
	private FinaleGamesPanel finaleGamesPanel;
	private FinaleScorePanel finaleScorePanel;
	private JTabbedPane tabbedPane = new JTabbedPane();
	private JFrame jFrame;
	
	public CompetitorTabbedPane(Tournament turniej, JFrame frame){
		jFrame = frame;
		this.turniej = turniej;
		this.DB = new Database();
		showPanel = new ShowEditCompetitorPanel(turniej, DB);
		tournamentPanel = new TournamentPanel(turniej, DB);
		FinaleGamesPanel.onFinalesEndListener finEndListener = ()->{
			turniej.setRoundsCompleted(3);
			DB.insertOrUpdateTournament(turniej);
			finaleScorePanel = new FinaleScorePanel(turniej, DB);
			tabbedPane.add(Strings.tournamentResults, finaleScorePanel);
			tabbedPane.setSelectedIndex(6);
		};
		GroupsChoosePanel.onFinaleStartListener finStartListener = ()->{ // po rozpoczęciu finałów
			turniej.setRoundsCompleted(2);
			DB.insertOrUpdateTournament(turniej);
			finaleGamesPanel = new FinaleGamesPanel(turniej, DB, finEndListener);
			tabbedPane.add(Strings.finaleGames, finaleGamesPanel);
			tabbedPane.setSelectedIndex(5); 
		}; 
		GamesPanel.onEliminationsEndListener elEndListener = ()->{  // po zakończeniu eliminacji
			turniej.setRoundsCompleted(1);
			DB.insertOrUpdateTournament(turniej);
			groupsChoosePanel = new GroupsChoosePanel(turniej, DB, finStartListener);
			tabbedPane.add(Strings.chooseForFinales, groupsChoosePanel);
			tabbedPane.setSelectedIndex(4);
		};
		SwissGamesPanel.onFailListener onFailListener = ()->restart();
		GroupsPanel.onTournamentStartListener tStartlistener = ()->{ // po rozpoczęciu turnieju
			turniej.setRoundsCompleted(0);
			DB.insertOrUpdateTournament(turniej);
			group.setVisible(false);
			gamesPanel = turniej.isSwiss() ? new SwissGamesPanel(turniej, DB, onFailListener) : new GamesPanel(turniej, DB, elEndListener);
			tabbedPane.add(turniej.isSwiss()? Strings.swissGames : Strings.elGames, gamesPanel);
			finaleScorePanel = new FinaleScorePanel(turniej, DB);
			tabbedPane.add(Strings.tournamentResults, finaleScorePanel);
			tabbedPane.setSelectedIndex(3);
		};
		groupsPanel = new GroupsPanel(turniej, DB, tStartlistener);
		setMenu(frame);
	    tabbedPane.add(Strings.showOrEditComp, showPanel);
	    tabbedPane.add(Strings.tournament, tournamentPanel);
	    tabbedPane.add(groupsPanel);
	    tabbedPane.addChangeListener((e) -> {
			int i = tabbedPane.getSelectedIndex();
			if(i==0) showPanel.setData();
			if(i==1) tournamentPanel.setSBBounds();
			if(i==2) groupsPanel.initComponents();
			if(i==3) gamesPanel.initComponents();
			if(i==5) finaleGamesPanel.initComponents();
			if(i==4 && turniej.isSwiss()) finaleScorePanel.initComponents(); 
			comp.setVisible( turniej.isPlayersEditAllowed() && i==0);
			group.setVisible(turniej.isPlayersEditAllowed() && i==2 && !turniej.isSwiss());
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
	    	tabbedPane.setTitleAt(2, (type==Tournament.Type.SWISS)?Strings.prep1stRound:Strings.prepGroups);
	    });
	    turniej.setType(turniej.getType()); // wygląda bezsensownie, ale odpala powyższy listener
	    int roundsCompleted = turniej.getRoundsCompleted();
	    if(roundsCompleted>=0) tStartlistener.onTournamentStart();
	    if(turniej.isSwiss()) {
	    	if(turniej.getRoundsCompleted()>=turniej.getRounds()); // jeśli będzie jakiś listener na koniec turnieju szwajcarskiego, to go tu odpalić
	    }
	    else {
		    if(roundsCompleted>0) elEndListener.onEliminationsEnd();
		    if(roundsCompleted>1) finStartListener.onFinaleStart();
		    if(roundsCompleted>2) finEndListener.onFinalesEnd();
	    }
	}
	
	/**
	 * Tworzy i dodaje elementy menu, akcje po ich wywołaniu i skróty
	 */
	private void setMenu(JFrame frame) {
		sortOptions = new LinkedHashMap<>();
		menuBar	= new JMenuBar();
		comp 	= new JMenu(Strings.players);
		sort	= new JMenu(Strings.plSort);
		group 	= new JMenu(Strings.autoGrouping);
		addC 	= new JMenuItem(Strings.addComp);
		rndC 	= new JMenuItem(Strings.addRandomComp);
		autoGroup = new JMenuItem(Strings.defGroup);
		addC.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_N, Event.CTRL_MASK));
		rndC.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_L, Event.CTRL_MASK));
		sortDefault = new JMenuItem(Strings.defSort);
		sortRandom = new JMenuItem(Strings.randomSort);
		sortOptions.put(new JMenuItem(Strings.AGE_ASC) 			, SortOption.AGE_ASC);
		sortOptions.put(new JMenuItem(Strings.AGE_DESC) 		, SortOption.AGE_DESC);
		sortOptions.put(new JMenuItem(Strings.CHESSCATEGORY_ASC) , SortOption.CHESSCATEGORY_ASC);
		sortOptions.put(new JMenuItem(Strings.CHESSCATEGORY_DESC), SortOption.CHESSCATEGORY_DESC);
		sortOptions.put(new JMenuItem(Strings.NAME_ASC) 		, SortOption.NAME_ASC);
		sortOptions.put(new JMenuItem(Strings.NAME_DESC) 		, SortOption.NAME_DESC);
		sortOptions.put(new JMenuItem(Strings.SURNAME_ASC) 		, SortOption.SURNAME_ASC);
		sortOptions.put(new JMenuItem(Strings.SURNAME_DESC) 	, SortOption.SURNAME_DESC);
		comp.add(addC);
		comp.add(rndC);
		sort.add(sortDefault);
		sort.add(sortRandom);
		sortOptions.keySet().forEach((jmi) -> sort.add(jmi));
		group.add(autoGroup);
		menuBar.add(comp);
		menuBar.add(sort);
		menuBar.add(group);
		frame.setJMenuBar(menuBar);
		Tools.aboutMenu(menuBar, frame);
		addC.addActionListener((e) -> {
			 if(!turniej.isPlayersEditAllowed()) return;
			 Competitor c = new Competitor(null, "", "", 0, 0, false, null);
			 DB.insertOrUpdateCompetitor(c, turniej.getId());
			 showPanel.setData();
		});
		rndC.addActionListener((e) -> {
			 if(!turniej.isPlayersEditAllowed()) return;
			 Competitor c = null;
			try {
				 c = Simulator.RandomPlayer();
				 DB.insertOrUpdateCompetitor(c, turniej.getId());
			} catch (Exception e1) {
				 e1.printStackTrace();
			}
			 showPanel.setData();
		});
		sortDefault.addActionListener(	e->groupsPanel.sortDefault());
		autoGroup.addActionListener(	e->groupsPanel.autoGroup());
		sortRandom.addActionListener(	e->groupsPanel.shuffle());
		sortOptions.keySet().forEach((jmi) -> 
			jmi.addActionListener(e->groupsPanel.stableSort(sortOptions.get(jmi)))
		);
		sort.setVisible(false);
		group.setVisible(false);
	}
	
	private void restart() {
		jFrame.getContentPane().removeAll();
    	new CompetitorTabbedPane(turniej,jFrame);
	}
}