package panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import model.Competitor;
import model.Database;
import model.SingleGame;
import model.Tournament;

public class GamesPanel extends JPanel{
	private static final long serialVersionUID = 6375980426732887418L;
	private final Tournament turniej;
	private final Database DB;
	private JPanel container = new JPanel();
	private List<Competitor> competitors;
	Map<Integer, Competitor> competitorMap;
	private List<SingleGame> singleGames;
	
	/**
	 * @param t - id turnieju
	 * @param db - baza danych
	 */
	public GamesPanel(Tournament t, Database db){
		this.turniej = t;
		this.DB = db;
		this.setLayout(new BorderLayout());
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		add(new JScrollPane(container));
		initComponents();
	}
	
	public void initComponents() {
		competitors = DB.getCompetitors(turniej.getId());
		singleGames = DB.getSingleGames(turniej.getId());
		competitorMap = competitors.stream()
				.collect(Collectors.toMap(c->c.getId(), c->c));
		JLabel label = new JLabel("Rozgrywki fazy eliminacji", JLabel.CENTER);
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		container.add(label);
		container.add(Box.createRigidArea(new Dimension(0, 10)));
		JTable table = new JTable(new MyTableModel());
		table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(
        		new JComboBox<String>(new String[] {"Brak wyniku", "Wygrały białe", "Wygrały czarne", "Remis"})
        ));
        container.add(table.getTableHeader());
        container.add(table);
		container.add(Box.createRigidArea(new Dimension(0, 20)));
	}
	
	class MyTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -7500940137857222909L;
		final String[] columnNames = {"Gra czarnymi", "Gra białymi", "Wynik"};

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}
		@Override
		public int getColumnCount() {
			return 3;
		}
		@Override
		public String getColumnName(int columnIndex) {
			return columnNames[columnIndex];
		}
		@Override
		public int getRowCount() {
			return singleGames.size();
		}
		@Override
		public Object getValueAt(int row, int col) {
			SingleGame sg = singleGames.get(row);
			if(col==0) return competitorMap.get(sg.getCompetitor1());
			if(col==1) return competitorMap.get(sg.getCompetitor2());
			if(col==2) {
				if(sg.getScore()==0) return " - ";
				if(sg.getScore()==1) return "Wygrały białe";
				if(sg.getScore()==2) return "Wygrały czarne";
				if(sg.getScore()==3) return "Remis";
			}
	        return null;
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return col==2;
		}

		@Override
		public void setValueAt(Object aValue, int row, int col) {
			int w = -1;
			SingleGame sg = singleGames.get(row);
			if(col==2) {
				if("Brak wyniku".equals((String)aValue)) w=0;
				if("Wygrały białe".equals((String)aValue)) w=1;
				if("Wygrały czarne".equals((String)aValue)) w=2;
				if("Remis".equals((String)aValue)) w=3;
			}
			if(w!=-1) {
				if(sg.getScore()!=w) {
					sg.setScore(w);
					DB.insertOrUpdateSingleGame(sg);
					this.fireTableDataChanged();
				}
			}
			else System.err.print("Do not use setValueAt in "+getClass());
		}
	}
}
