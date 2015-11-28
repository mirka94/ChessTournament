package panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import model.Competitor;
import model.Database;
import model.SingleGame;
import model.Tournament;
import tools.Dialogs;
import tools.Tools;

public class GroupsPanel extends JPanel{
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
	public GroupsPanel(Tournament t, Database db, onTournamentStartListener listener){
		this.turniej = t;
		this.DB = db;
		this.setLayout(new BorderLayout());
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		add(new JScrollPane(container));
		initComponents();
		startTournament.addActionListener(e -> {
			if(turniej.isSwiss()) 
				Dialogs.doZrobienia();
			else {
				Tools.checkGroups(turniej.getRounds(), competitors);
				if(competitors.stream().filter(c->c.getGroup()==null).count()>0)
					Dialogs.graczBezGrupy();
				else {
					competitors.forEach(c->DB.insertOrUpdateCompetitor(c, null)); // słaba wydajność w tym punkcie
					int min = competitors.size();
					int max = 0;
					TreeMap<Integer, List<Competitor>> groupsList = Tools.groupsList(competitors);
					for(List<Competitor> groupL : groupsList.values() ) {
						int size = groupL.size();
						min = Math.min(min, size);
						max = Math.max(max, size);
					}
					if(max>min+1)
						Dialogs.nierownomiernyPodzial(min, max);
					else {
						turniej.setRoundsCompleted(0);
						listener.onTournamentStart();
						//DB.insertOrUpdateTournament(turniej); // odkomentować po testach
						
						// Od tego miejsca generowanie obsługa gier (będzie nowa zakładka)
						Map<Integer, Competitor> cm = competitors.stream()
							.collect(Collectors.toMap(c->c.getId(), c->c));
						for(SingleGame sg : Tools.generateSingleGames(groupsList)) {
							System.out.println(
									"Runda "+sg.getRound()+",\t"+
									"grają: "+
									cm.get(sg.getCompetitor1())+"\ti\t"+
									cm.get(sg.getCompetitor2())
									);
						};
					}
				}
			}
		});
	}
	
	@FunctionalInterface 
	public interface onTournamentStartListener {
		public void onTournamentStart();
	}
	
	public void initComponents() {
		competitors = DB.getCompetitors(turniej.getId());
		sortDefault();
		final int groups = turniej.getRounds();
		Tools.checkGroups(groups, competitors);
		container.removeAll();
		tables.clear();
		JLabel label = new JLabel("Uczestnicy", JLabel.CENTER);
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		container.add(label);
		container.add(Box.createRigidArea(new Dimension(0, 10)));
		JTable tableN = new JTable(new MyTableModel(null));
		tables.put(null, tableN);
		tableN.addMouseListener(new MyMouseListener(tableN, groups));
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
		        table.addMouseListener(new MyMouseListener(table, groups));
			}
        }
        else {
        	for(Competitor c : competitors) c.setGroup(null);
        	updateTables();
        }
		container.add(Box.createRigidArea(new Dimension(0, 50)));
		container.add(startTournament);
	}
	
	void autoGroup() {
		int groups = turniej.getRounds(), i = groups, n = -1;
		List<List<Competitor>> groupsLists = new ArrayList<>();
		for(Competitor c : competitors) {
			if(++i>=groups) {
				i = 0;
				groupsLists.add(new ArrayList<>());
				n++;
			}
			groupsLists.get(n).add(c);
		}
		while(++i<groups) { // dopełnienie ostatniej grupy wartościami "pustymi"
			groupsLists.get(n).add(new Competitor(null, "", "", 0, 0, false, 0));
		}
		for(List<Competitor> l : groupsLists) {
			Collections.shuffle(l);
			int g = 0;
			for(Competitor c : l) c.setGroup(g++);
		}
		updateTables();
	}
	
	void sortDefault() {
		stableSort(Competitor.SortOption.NAME_ASC);
		stableSort(Competitor.SortOption.SURNAME_ASC);
		stableSort(Competitor.SortOption.AGE_DESC);
		stableSort(Competitor.SortOption.CHESSCATEGORY_ASC);
	}
	
	void stableSort(Competitor.SortOption o) {
		competitors.sort(Competitor.comparators.get(o));
		updateTables();
	}
	
	void shuffle() {
		Collections.shuffle(competitors);
	}
	
	
	
	void updateTables() {
		tables.values().forEach((t) -> ((AbstractTableModel)t.getModel()).fireTableDataChanged());
	}
	
	public boolean isEditAllowed() {
		return turniej.getRoundsCompleted()<0;
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
			if(!isEditAllowed()) return;
            int r = table.rowAtPoint(e.getPoint());
            if(r >= 0 && r < table.getRowCount()) 
                table.setRowSelectionInterval(r, r);
            else 
            	table.clearSelection();

            final int rowindex = table.getSelectedRow();
            if(rowindex < 0) return;
            if(e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                JPopupMenu popup = new JPopupMenu();
                if(((MyTableModel) table.getModel()).competitors.isEmpty()) return;
                Competitor c = ((MyTableModel) table.getModel()).competitors.get(rowindex);
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
					DB.insertOrUpdateCompetitor(c, null);
					((AbstractTableModel)tables.get(oldGroup)	.getModel()).fireTableDataChanged();
					((AbstractTableModel)tables.get(group)		.getModel()).fireTableDataChanged();
				}
			});
		}
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
			competitors = GroupsPanel.this.competitors.stream()
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
