package panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import model.Competitor;
import model.Database;
import model.Tournament;

public class GroupsPanel extends JPanel{
	private static final long serialVersionUID = 3870936863486240444L;
	private final Tournament turniej;
	private final Database DB;
	private JPanel container;
	private LinkedHashMap<Integer, JTable> tables;
	private List<Competitor> competitors;
	enum SortOption {
		NAME_ASC, NAME_DESC, 
		SURNAME_ASC, SURNAME_DESC, 
		AGE_ASC, AGE_DESC, 
		CHESSCATEGORY_ASC, CHESSCATEGORY_DESC
	}
	/**
	 * @param t - id turnieju
	 * @param db - baza danych
	 */
	public GroupsPanel(Tournament t, Database db){
		this.turniej = t;
		this.DB = db;
		this.setLayout(new BorderLayout());
		container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		//container.setAlignmentX(0.2f);
		add(new JScrollPane(container));
		initComponents();
	}
	
	public void initComponents() {
		competitors = DB.getCompetitors(turniej.getId());
		final int groups = turniej.getRounds();
		container.removeAll();
		tables = new LinkedHashMap<>();
		JLabel label = new JLabel("Uczestnicy nieprzydzieleni do grup", JLabel.CENTER);
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		container.add(label);
		container.add(Box.createRigidArea(new Dimension(0, 10)));
		MyTableModel modelNull = new MyTableModel(null);
		JTable tableN = new JTable(modelNull);
		tables.put(null, tableN);
		tableN.addMouseListener(new MyMouseListener(tableN, groups));
        container.add(tableN.getTableHeader());
        container.add(tableN);
        
		for(int i=0; i<groups; ++i) {
			container.add(Box.createRigidArea(new Dimension(0, 20)));
			container.add(new JLabel("Grupa "+(i+1), JLabel.CENTER));
			container.add(Box.createRigidArea(new Dimension(0, 10)));
			JTable table = new JTable(new MyTableModel(i));
			container.add(table);
			tables.put(i, table);
	        table.addMouseListener(new MyMouseListener(table, groups));
		}
	}
	
	void stableSort(SortOption o) {
		Comparator<Competitor> comp = null;
		switch(o) {
			case AGE_ASC:
				comp = (c1, c2) -> c1.getAge().compareTo(c2.getAge());
				break;
			case AGE_DESC:
				comp = (c2, c1) -> c1.getAge().compareTo(c2.getAge());
				break;
			case CHESSCATEGORY_ASC:
				comp = (c1, c2) -> c1.getChessCategory().compareTo(c2.getChessCategory());
				break;
			case CHESSCATEGORY_DESC:
				comp = (c2, c1) -> c1.getChessCategory().compareTo(c2.getChessCategory());
				break;
			case NAME_ASC:
				comp = (c1, c2) -> c1.getName().compareTo(c2.getName());
				break;
			case NAME_DESC:
				comp = (c2, c1) -> c1.getName().compareTo(c2.getName());
				break;
			case SURNAME_ASC:
				comp = (c1, c2) -> c1.getSurname().compareTo(c2.getSurname());
				break;
			case SURNAME_DESC:
				comp = (c2, c1) -> c1.getSurname().compareTo(c2.getSurname());
				break;
		}
		if(comp!=null) competitors.sort(comp);
		tables.values().forEach((t) -> ((AbstractTableModel)t.getModel()).fireTableDataChanged());
	}
	void shuffle() {
		Collections.shuffle(competitors);
		tables.values().forEach((t) -> ((AbstractTableModel)t.getModel()).fireTableDataChanged());
	}
	
	class MyMouseListener extends MouseAdapter {
		final int groups;
		final JTable table;
		MyMouseListener(JTable table, int groups) {
			super();
			this.table = table;
			this.groups = groups;
		}
		@Override
        public void mouseReleased(MouseEvent e) {
            int r = table.rowAtPoint(e.getPoint());
            if(r >= 0 && r < table.getRowCount()) 
                table.setRowSelectionInterval(r, r);
            else 
            	table.clearSelection();

            final int rowindex = table.getSelectedRow();
            if(rowindex < 0) return;
            if(e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                JPopupMenu popup = new JPopupMenu();
                if(((MyTableModel) table.getModel()).competitors().isEmpty()) return;
                Competitor c = ((MyTableModel) table.getModel()).competitors().get(rowindex);
                if(c.getGroup()!=null) {
                	MoveToAnotherGroupMenuItem jmiNull = new MoveToAnotherGroupMenuItem(null, c);
                	popup.add(jmiNull);
                }
                for(int i=0; i<groups; ++i) {
                	if(c.getGroup()!=null && c.getGroup()==i) continue;
                	MoveToAnotherGroupMenuItem jmi = new MoveToAnotherGroupMenuItem(i, c);
                    popup.add(jmi);
                }
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
	}
	
	class MoveToAnotherGroupMenuItem extends JMenuItem {
		private static final long serialVersionUID = 3141539647519682601L;

		public MoveToAnotherGroupMenuItem(Integer group, Competitor c) {
			super(group==null?"Usuń z grupy":"Przenieś do grupy "+(group+1));
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Integer oldGroup = c.getGroup();
					c.setGroup(group);
					((AbstractTableModel)tables.get(oldGroup)	.getModel()).fireTableDataChanged();
					((AbstractTableModel)tables.get(group)		.getModel()).fireTableDataChanged();
				}
			});
		}
	}
	
	class MyTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -7420896389109019010L;
		private Integer group;
		final String[] columnNames = {"Imię", "Nazwisko", "Wiek", "Kategoria"};
		public MyTableModel(Integer group) {
			this.group = group;
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
			return Math.max(competitors().size(),1);
		}
		@Override
		public Object getValueAt(int row, int col) {
			if(competitors().isEmpty()) return "N/A";
			Competitor c = competitors().get(row);
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
		
		public List<Competitor> competitors() {
			return competitors.stream()
					.filter(c->c.getGroup()==group)
					.collect(Collectors.toList());
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			System.err.print("Do not use setValueAt in "+getClass());
		}
	}
}
