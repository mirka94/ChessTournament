package panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.List;
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
import model.Tournament;

public class GamesPanel extends JPanel{
	private static final long serialVersionUID = 3870936863486240444L;
	private final Tournament turniej;
	private final Database DB;
	private JPanel container = new JPanel();
	private JButton startTournament = new JButton("Rozpocznij turniej");
	private LinkedHashMap<Integer, JTable> tables = new LinkedHashMap<>();
	private List<Competitor> competitors;
	
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
		final int groups = turniej.getRounds();
		container.removeAll();
		tables.clear();
		JLabel label = new JLabel("Uczestnicy", JLabel.CENTER);
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		container.add(label);
		container.add(Box.createRigidArea(new Dimension(0, 10)));
		JTable tableN = new JTable(new MyTableModel(null));
		tables.put(null, tableN);
        container.add(tableN.getTableHeader());
        container.add(tableN);
        
        if(!turniej.isSwiss()) {
        	label.setText("Uczestnicy nieprzydzieleni do grup");
			for(int i=0; i<groups; ++i) {
				container.add(Box.createRigidArea(new Dimension(0, 20)));
				container.add(new JLabel("Grupa "+(i+1), JLabel.CENTER));
				container.add(Box.createRigidArea(new Dimension(0, 10)));
				JTable table = new JTable(new MyTableModel(i));
				container.add(table);
				tables.put(i, table);
			}
        }
        else {
        	for(Competitor c : competitors) c.setGroup(null);
        	updateTables();
        }
		container.add(Box.createRigidArea(new Dimension(0, 50)));
		container.add(startTournament);
	}
	
	void updateTables() {
		tables.values().forEach((t) -> ((AbstractTableModel)t.getModel()).fireTableDataChanged());
	}

	
	class MyTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -7420896389109019010L;
		final String[] columnNames = {"Imię", "Nazwisko", "Wiek", "Kategoria"};
		private Integer group;
		private List<Competitor> competitors;
		public MyTableModel(Integer group) {
			this.group = group;
			setCompetitors();
		}
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if(columnIndex<2) return String.class;
			else return Integer.class;
		}
		@Override
		public int getColumnCount() {
			return 4;
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
			if(col==0) return c.getName();
			if(col==1) return c.getSurname();
			if(col==2) return c.getAge();
			if(col==3) return c.getChessCategory();
	        return null;
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}
		
		public void setCompetitors() {
			competitors = GamesPanel.this.competitors.stream()
					.filter(c->c.getGroup()==group)
					.collect(Collectors.toList());
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			System.err.print("Do not use setValueAt in "+getClass());
		}
		@Override
		public void fireTableDataChanged() {
			setCompetitors();
			super.fireTableDataChanged();
		}
	}
}
