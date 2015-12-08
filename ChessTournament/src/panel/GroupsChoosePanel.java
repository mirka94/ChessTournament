package panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import model.Competitor;
import model.Database;
import model.SingleGame;
import model.Tournament;

public class GroupsChoosePanel extends JPanel{
	private static final long serialVersionUID = -2898571746717852493L;
	private final Tournament turniej;
	private final Database DB;
	private JPanel container = new JPanel();
	private JButton startFinales = new JButton("Rozpocznij fazę drugą");
	private LinkedHashMap<Integer, JTable> tables = new LinkedHashMap<>();
	private List<Competitor> competitors;
	Map<Integer, Competitor> competitorMap;
	private List<SingleGame> singleGames;
	private Map<Competitor, List<SingleGame>> competitorGames = new HashMap<>();
	private Map<Competitor, Integer> competitorWon = new HashMap<>();
	private Map<Competitor, Integer> competitorLost = new HashMap<>();
	private Map<Competitor, Integer> competitorTie = new HashMap<>();
	private Map<Competitor, Float> competitorPoints = new HashMap<>();
	private Map<Competitor, Float> competitorSBPoints = new HashMap<>();
	private Map<Competitor, Boolean> competitorGoesFurther = new HashMap<>();
	
	/**
	 * @param t - id turnieju
	 * @param db - baza danych
	 */
	public GroupsChoosePanel(Tournament t, Database db, onFinaleStartListener listener){
		this.turniej = t;
		this.DB = db;
		this.setLayout(new BorderLayout());
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		add(new JScrollPane(container));
		initComponents();
	}
	
	@FunctionalInterface 
	public interface onFinaleStartListener {
		public void onFinaleStart();
	}
	
	public void initComponents() {
		competitors = DB.getCompetitors(turniej.getId());
		competitorMap = competitors.stream()
				.collect(Collectors.toMap(c->c.getId(), c->c));
		singleGames = DB.getSingleGames(turniej.getId());
		for(Competitor c : competitors) {
			competitorGoesFurther.put(c, false);
			competitorGames.put(c, new LinkedList<>());
		}
		for(SingleGame sg : singleGames) {
			competitorGames.get(competitorMap.get(sg.getCompetitor1())).add(sg);
			competitorGames.get(competitorMap.get(sg.getCompetitor2())).add(sg);
		}
		final int groups = turniej.getRounds();
		container.removeAll();
		tables.clear();
		JLabel label = new JLabel("Wybór uczestników do finałów", JLabel.CENTER);
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
		for(int i=0; i<groups; ++i) {
			container.add(Box.createRigidArea(new Dimension(0, 20)));
			container.add(new JLabel("Grupa "+(i+1), JLabel.CENTER));
			container.add(Box.createRigidArea(new Dimension(0, 10)));
			JTable table = new JTable(new MyTableModel(i));
			container.add(table.getTableHeader());
			container.add(table);
			tables.put(i, table);
		}
		container.add(Box.createRigidArea(new Dimension(0, 50)));
		container.add(startFinales);
		updateTables();
	}	
	
	void updateTables() {
		for(Competitor c : competitors) {
			competitorWon.put(c, 0);
			competitorLost.put(c, 0);
			competitorTie.put(c, 0);
			for(SingleGame sg : competitorGames.get(c)) {
				Competitor c1 = competitorMap.get(sg.getCompetitor1()); // gra białymi
				Competitor c2 = competitorMap.get(sg.getCompetitor2()); // gra czarnymi
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
				Competitor c1 = competitorMap.get(sg.getCompetitor1()); // gra białymi
				Competitor c2 = competitorMap.get(sg.getCompetitor2()); // gra czarnymi
				int score = sg.getScore(); // 1 - wygrały białe, 2 - czarne, 3 - remis;
				if(score==1 && c.equals(c1)) SBPoints+=competitorPoints.get(c2);
				if(score==2 && c.equals(c2)) SBPoints+=competitorPoints.get(c1);
				if(score==3 && c.equals(c1)) SBPoints+=0.5f*competitorPoints.get(c2);
				if(score==3 && c.equals(c2)) SBPoints+=0.5f*competitorPoints.get(c1);
			}
			competitorSBPoints.put(c, SBPoints);
		}
		tables.values().forEach((t) -> ((AbstractTableModel)t.getModel()).fireTableDataChanged());
	}
	
	public boolean isEditAllowed() {
		return turniej.getRoundsCompleted()<0;
	}
	
	class MyTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -4117169486534731202L;
		final String[] columnNames = {"Gracz", "Wygranych", "Przegranych", "Zakończonych remisem", "Punkty", "Punkty SB", "Wchodzi do finału"};
		private Integer group;
		private List<Competitor> competitors;
		public MyTableModel(Integer group) {
			this.group = group;
			setCompetitors();
		}
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if(columnIndex==0) return String.class;
			if(columnIndex==4) return Float.class;
			if(columnIndex==5) return Float.class;
			if(columnIndex==6) return Boolean.class;
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
			if(col==0) return c.toString();
			if(col==1) return competitorWon.get(c);
			if(col==2) return competitorLost.get(c);
			if(col==3) return competitorTie.get(c);
			if(col==4) return competitorPoints.get(c);
			if(col==5) return competitorSBPoints.get(c);
			if(col==6) return competitorGoesFurther.get(c);
	        return null;
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return col==6;
		}
		
		public void setCompetitors() {
			competitors = GroupsChoosePanel.this.competitors.stream()
					.filter(c->c.getGroup()==group)
					.collect(Collectors.toList());
		}

		@Override
		public void setValueAt(Object aValue, int row, int col) {
			if(col==6) {
				Competitor c = competitors.get(row);
				competitorGoesFurther.put(c, (Boolean) aValue);
				fireTableDataChanged();
			}
			else System.err.print("Do not use setValueAt in "+getClass());
		}
		@Override
		public void fireTableDataChanged() {
			setCompetitors();
			super.fireTableDataChanged();
		}
	}
}
