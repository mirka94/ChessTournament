package panel;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import model.Competitor;
import model.Database;
import model.SingleGame;
import model.Tournament;
import res.Strings;

/**
 * Zakładka wyników
 */
public class FinaleScorePanel extends JPanel{
	private static final long serialVersionUID = 2251707153580595861L;
	private final Tournament turniej;
	private final Database DB;
	private JTable table;
	private List<Competitor> competitors;
	Map<Integer, Competitor> competitorMap;
	private List<SingleGame> singleGames;
	private Map<Competitor, List<SingleGame>> competitorGames = new HashMap<>();
	private Map<Competitor, Integer> competitorWon 		= new HashMap<>();
	private Map<Competitor, Integer> competitorLost 	= new HashMap<>();
	private Map<Competitor, Integer> competitorTie 		= new HashMap<>();
	private Map<Competitor, Float> 	 competitorPoints 	= new HashMap<>();
	private Map<Competitor, Float>   competitorSBPoints = new HashMap<>();
	
	/**
	 * @param t - id turnieju
	 * @param db - baza danych
	 */
	public FinaleScorePanel(Tournament t, Database db){
		this.turniej = t;
		this.DB = db;
		this.setLayout(new BorderLayout());
		initComponents();
	}
	
	public void initComponents() {
		competitors = DB.getCompetitors(turniej.getId()).stream()
				.filter(c->c.getGoesFinal()).collect(Collectors.toList());
		competitorMap = competitors.stream()
				.collect(Collectors.toMap(c->c.getId(), c->c));
		singleGames = DB.getSingleGames(turniej.getId(), true).stream()
				.filter(sg->competitorMap.containsKey(sg.getCompetitorW())&&
							competitorMap.containsKey(sg.getCompetitorB()))
				.collect(Collectors.toList());
		// filtrowanie powyżej, bo baza zwraca również gry, 
		// gdzie grali (dostał się do finałów) vs (nie dostał się)
		// można to naprawić w bazie
		for(Competitor c : competitors) {
			competitorGames.put(c, new LinkedList<>());
		}
		for(SingleGame sg : singleGames) {
			competitorGames.get(competitorMap.get(sg.getCompetitorW())).add(sg);
			competitorGames.get(competitorMap.get(sg.getCompetitorB())).add(sg);
		}
		removeAll();
		table = new JTable(new MyTableModel());
		add(new JScrollPane(table));
		updateTables();
	}	
	
	void updateTables() {
		for(Competitor c : competitors) {
			competitorWon.put(c, 0);
			competitorLost.put(c, 0);
			competitorTie.put(c, 0);
			for(SingleGame sg : competitorGames.get(c)) {
				Competitor c1 = competitorMap.get(sg.getCompetitorW()); // gra białymi
				Competitor c2 = competitorMap.get(sg.getCompetitorB()); // gra czarnymi
				int score = sg.getScore(); // 1 - wygrały białe, 2 - czarne, 3 - remis;
				if(score==1 && c.equals(c1)) competitorWon.put(c, competitorWon.get(c)+1);
				if(score==2 && c.equals(c2)) competitorWon.put(c, competitorWon.get(c)+1);
				
				if(score==1 && c.equals(c2)) competitorLost.put(c, competitorLost.get(c)+1);
				if(score==2 && c.equals(c1)) competitorLost.put(c, competitorLost.get(c)+1);
				
				if(score==3) competitorTie.put(c, competitorTie.get(c)+1);
			}
			float points = 1.0f*competitorWon.get(c)+0.5f*competitorTie.get(c);
			competitorPoints.put(c,points);
		}
		for(Competitor c : competitors) {
			float SBPoints = 0.0f;
			for(SingleGame sg : competitorGames.get(c)) {
				Competitor c1 = competitorMap.get(sg.getCompetitorW()); // gra białymi
				Competitor c2 = competitorMap.get(sg.getCompetitorB()); // gra czarnymi
				int score = sg.getScore(); // 1 - wygrały białe, 2 - czarne, 3 - remis;
				if(score==1 && c.equals(c1)) SBPoints+=competitorPoints.get(c2);
				if(score==2 && c.equals(c2)) SBPoints+=competitorPoints.get(c1);
				if(score==3 && c.equals(c1)) SBPoints+=0.5f*competitorPoints.get(c2);
				if(score==3 && c.equals(c2)) SBPoints+=0.5f*competitorPoints.get(c1);
			}
			competitorSBPoints.put(c, SBPoints);
		}
		competitors.sort((c1,c2)->(int)(4.*(competitorSBPoints.get(c2)-competitorSBPoints.get(c1))));
		competitors.sort((c1,c2)->(int)(2.*(competitorPoints.get(c2)-competitorPoints.get(c1))));
		((AbstractTableModel)table.getModel()).fireTableDataChanged();
	}
	
	public boolean isEditAllowed() {
		return turniej.getRoundsCompleted()<0;
	}
	
	class MyTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 6156876709229152200L;
		final String[] columnNames = {Strings.position, Strings.player, Strings.wonGames, Strings.lostGames, Strings.tieGames, Strings.points, Strings.pointsSB};
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if(columnIndex==0) return String.class;
			if(columnIndex==4) return Float.class;
			if(columnIndex==5) return Float.class;
			else return Integer.class;
		}
		@Override
		public int getColumnCount() {
			return 7;
		}
		@Override
		public String getColumnName(int columnIndex) {
			return columnNames[columnIndex];
		}
		@Override
		public int getRowCount() {
			return Math.max(competitors.size(),1);
		}
		@Override
		public Object getValueAt(int row, int col) {
			if(competitors.isEmpty()) return "N/A";
			Competitor c = competitors.get(row);
			if(col==0) return row+1;
			if(col==1) return c.toString();
			if(col==2) return competitorWon.get(c);
			if(col==3) return competitorLost.get(c);
			if(col==4) return competitorTie.get(c);
			if(col==5) return competitorPoints.get(c);
			if(col==6) return competitorSBPoints.get(c);
	        return null;
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}

		@Override
		public void setValueAt(Object aValue, int row, int col) {
			System.err.print("Do not use setValueAt in "+getClass());
		}
	}
}
