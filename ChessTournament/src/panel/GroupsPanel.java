package panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
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
import javax.swing.table.JTableHeader;

import model.Competitor;
import model.Database;
import model.Tournament;
import res.Strings;
import tools.Dialogs;
import tools.Tools;

public class GroupsPanel extends JPanel{
	private static final long serialVersionUID = 5713186972494685342L;
	private final Tournament turniej;
	private final Database DB;
	private JPanel container = new JPanel();
	private JButton startTournament = new JButton(Strings.startTournament);
	private LinkedHashMap<Integer, JTable> tables = new LinkedHashMap<>();
	private List<Competitor> competitors;
	private JLabel label;
	private JTable tableN;
	private JTableHeader tableNHeader;
	private Component rigridAfterN;
	
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
		startTournament.setVisible(isEditAllowed());
		startTournament.addActionListener(e -> {
			Tools.checkGroups(turniej.getRounds(), competitors);
			if(competitors.stream().filter(c->c.getGroup()==null).count()>0)
				Dialogs.graczBezGrupy();
			else {
				competitors.forEach(c->DB.insertOrUpdateCompetitor(c, turniej.getId())); // słaba wydajność w tym punkcie
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
					DB.insertOrUpdateSingleGame(
						Tools.generateSingleGames(groupsList, turniej.getBoards()), 
						turniej.getId());
					startTournament.setVisible(false);
					listener.onTournamentStart();
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
		label = new JLabel(Strings.players, JLabel.CENTER);
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		container.add(label);
		container.add(Box.createRigidArea(new Dimension(0, 10)));
		tableN = new JTable(new MyTableModel(null));
		tables.put(null, tableN);
		tableN.addMouseListener(new MyMouseListener(tableN, groups));
        container.add(tableNHeader = tableN.getTableHeader());
        container.add(tableN);
        container.add(rigridAfterN = Box.createRigidArea(new Dimension(0, 20)));
        
		for(int i=0; i<groups; ++i) {
			container.add(new JLabel(Strings.group+(i+1), JLabel.CENTER));
			container.add(Box.createRigidArea(new Dimension(0, 10)));
			JTable table = new JTable(new MyTableModel(i));
			container.add(table.getTableHeader());
			container.add(table);
			tables.put(i, table);
			container.add(Box.createRigidArea(new Dimension(0, 20)));
	        table.addMouseListener(new MyMouseListener(table, groups));
		}
		
		container.add(Box.createRigidArea(new Dimension(0, 30)));
		container.add(startTournament);
		updateTables();
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
			groupsLists.get(n).add(new Competitor(null, "", "", 0, 0, false, 0)); //dodać 0
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
		if(tableN!=null && rigridAfterN!=null && tableNHeader!=null) {
			boolean allHaveGroup = (((MyTableModel)tableN.getModel()).rawGetRowCount()==0);
			tableN.setVisible(!allHaveGroup);
			rigridAfterN.setVisible(!allHaveGroup);
			tableNHeader.setVisible(!allHaveGroup);
			label.setText(allHaveGroup?"Kompletny podział na grupy":"Uczestnicy nieprzydzieleni do grup");
		}
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
		private static final long serialVersionUID = -70690392582608352L;

		public MoveToAnotherGroupMenuItem(Integer group, Competitor c) {
			super(group==null?"Usuń z grupy":"Przenieś do grupy "+(group+1));
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Integer oldGroup = c.getGroup();
					c.setGroup(group);
					DB.insertOrUpdateCompetitor(c, turniej.getId());
					((AbstractTableModel)tables.get(oldGroup)	.getModel()).fireTableDataChanged();
					((AbstractTableModel)tables.get(group)		.getModel()).fireTableDataChanged();
				}
			});
		}
	}
	
	class MyTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 3123540956848405467L;
		final String[] columnNames = {"Nazwisko", "Imię", "Wiek", "Kategoria"};
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
		public int rawGetRowCount() {
			return competitors.size();
		}
		@Override
		public Object getValueAt(int row, int col) {
			if(competitors.isEmpty()) return "N/A";
			Competitor c = competitors.get(row);
			if(col==0) return c.getSurname();
			if(col==1) return c.getName();
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
