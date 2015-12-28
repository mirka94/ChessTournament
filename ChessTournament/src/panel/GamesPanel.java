package panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import model.Competitor;
import model.Database;
import model.SingleGame;
import model.Tournament;
import tools.Dialogs;

public class GamesPanel extends JPanel{
	private static final long serialVersionUID = 6375980426732887418L;
	private final Tournament turniej;
	private final Database DB;
	private JButton finishEliminations = new JButton("Zakończ eliminacje");
	private List<Competitor> competitors;
	Map<Integer, Competitor> competitorMap;
	private List<SingleGame> singleGames;
	private List<SingleGame> currentlyPlayedGames;
	
	/**
	 * @param t - id turnieju
	 * @param db - baza danych
	 */
	public GamesPanel(Tournament t, Database db, onEliminationsEndListener listener){
		this.turniej = t;
		this.DB = db;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		initComponents(listener);
	}
	
	public void initComponents(onEliminationsEndListener listener) {
		competitors = DB.getCompetitors(turniej.getId());
		singleGames = DB.getSingleGames(turniej.getId(), false);
		competitorMap = competitors.stream()
				.collect(Collectors.toMap(c->c.getId(), c->c));
		recalcColors();
		JTable table = new JTable(new MyTableModel());
		table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(
        		new JComboBox<String>(new String[] {"Brak wyniku", "Wygrały białe", "Wygrały czarne", "Remis"})
        ));
		table.setDefaultRenderer(String.class, new MyCellRenderer());
		add(new JScrollPane(table));
		add(Box.createRigidArea(new Dimension(0, 20)));
		finishEliminations.addActionListener((e)->{
			if(singleGames.stream().filter(sg->sg.getScore()==0).count()>0) {
				Dialogs.gryBezWyniku();
			}
			else {
				finishEliminations.setVisible(false);
				listener.onEliminationsEnd();
			}
		});
		if(turniej.getRoundsCompleted()<1) add(finishEliminations);
	}
	
	void recalcColors() {
		currentlyPlayedGames = new ArrayList<>(turniej.getBoards());
		List<Integer> 	playingCompetitors 	= new ArrayList<>(2*turniej.getBoards()),
						takenBoards 		= new ArrayList<>(2*turniej.getBoards());
		for(SingleGame sg : singleGames.stream().filter(g->g.getScore()==0)
				.collect(Collectors.toList())){
			if(!takenBoards.contains(sg) &&
			   !playingCompetitors.contains(sg.getCompetitor1()) &&
			   !playingCompetitors.contains(sg.getCompetitor2())) 
			{
				takenBoards.add(sg.getBoard());
				playingCompetitors.add(sg.getCompetitor1());
				playingCompetitors.add(sg.getCompetitor2());
				currentlyPlayedGames.add(sg);
			}	
		};
		/*
		currentlyPlayedGames = singleGames.stream()
				.filter(g->g.getScore()==0).limit(turniej.getBoards())
				.collect(Collectors.toList());
		*/
	}
	
	@FunctionalInterface 
	public interface onEliminationsEndListener {
		public void onEliminationsEnd();
	}
	
	class MyCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 4811899196908294144L;
		@Override
		  public Component getTableCellRendererComponent(
				  JTable t, Object v, boolean isSelected, boolean hasFocus, int row, int col) {
		    Component c = super.getTableCellRendererComponent(t, v, isSelected, hasFocus, row, col);
		    c.setBackground(Color.decode(t.isRowSelected(row)?
		    	(currentlyPlayedGames.contains(singleGames.get(row)) ? "#5BFFB5" : "#84D4FF") : 
		    		(currentlyPlayedGames.contains(singleGames.get(row)) ? "#5BFF8C" : "#FFFFFF")	
		    	));
		  return c;
		}
	}
	
	class MyTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -7500940137857222909L;
		final String[] columnNames = {"Szachownica", "Gra białymi", "Gra czarnymi", "Wynik"};

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
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
			return singleGames.size();
		}
		@Override
		public Object getValueAt(int row, int col) {
			SingleGame sg = singleGames.get(row);
			if(col==0) return sg.getBoard()+1;
			if(col==1) return competitorMap.get(sg.getCompetitor1());
			if(col==2) return competitorMap.get(sg.getCompetitor2());
			if(col==3) {
				if(sg.getScore()==0) return " - ";
				if(sg.getScore()==1) return "Wygrały białe";
				if(sg.getScore()==2) return "Wygrały czarne";
				if(sg.getScore()==3) return "Remis";
			}
	        return null;
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return col==3 && turniej.getRoundsCompleted()==0;
		}

		@Override
		public void setValueAt(Object aValue, int row, int col) {
			int w = -1;
			SingleGame sg = singleGames.get(row);
			if(col==3) {
				if("Brak wyniku".equals((String)aValue)) w=0;
				if("Wygrały białe".equals((String)aValue)) w=1;
				if("Wygrały czarne".equals((String)aValue)) w=2;
				if("Remis".equals((String)aValue)) w=3;
			}
			if(w!=-1) {
				if(sg.getScore()!=w) {
					sg.setScore(w);
					DB.insertOrUpdateSingleGame(sg);
					recalcColors();
					this.fireTableDataChanged();
				}
			}
			else System.err.print("Do not use setValueAt in "+getClass());
		}
	}
}
