package panel;

import java.awt.Dimension;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import model.Database;
import model.SingleGame;
import model.Tournament;
import res.Strings;
import tools.Dialogs;

public class FinaleGamesPanel extends AbstractGamesPanel {
	private static final long serialVersionUID = 5148054112594621303L;
	private JButton finishFinales = new JButton(Strings.endTournament);
	private final onFinalesEndListener listener;
	
	/**
	 * @param t - id turnieju
	 * @param db - baza danych
	 */
	public FinaleGamesPanel(Tournament t, Database db, onFinalesEndListener listener){
		super(t,db);
		this.listener = listener;
		initComponents();
	}
	
	public void initComponents() {
		removeAll();
		competitors = DB.getCompetitors(turniej.getId()).stream()
				.filter(c->c.getGoesFinal()).collect(Collectors.toList());
		competitorMap = competitors.stream()
				.collect(Collectors.toMap(c->c.getId(), c->c));
		singleGames = DB.getSingleGames(turniej.getId(), true).stream()
				.filter(sg->competitorMap.containsKey(sg.getCompetitorW())&&
							competitorMap.containsKey(sg.getCompetitorB()))
				.collect(Collectors.toList());
		setDisqualifiedPlayersScores();
		// filtrowanie powyżej, bo baza zwraca również gry, 
		// gdzie grali (dostał się do finałów) vs (nie dostał się)
		recalcColors();
		JTable table = new JTable(new MyTableModel());
		table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(
        		new JComboBox<String>(new String[] {Strings.notPlayedYet, Strings.whiteWon, Strings.blackWon, Strings.tie})
        ));
		table.setDefaultRenderer(String.class, new MyCellRenderer());
	    add(new JScrollPane(table));
		add(Box.createRigidArea(new Dimension(0, 20)));
		finishFinales.addActionListener((e)->{
			if(singleGames.stream().filter(sg->sg.getScore()==0).count()>0) {
				Dialogs.gryBezWyniku();
			}
			else {
				finishFinales.setVisible(false);
				listener.onFinalesEnd();
			}
		});
		if(turniej.getRoundsCompleted()<3) add(finishFinales);
		mapKeyActions(table);
	}
	
	@FunctionalInterface 
	public interface onFinalesEndListener {
		public void onFinalesEnd();
	}
	
	class MyTableModel extends AbstractGamesPanel.MyTableModel {
		private static final long serialVersionUID = -451704734755151876L;

		@Override
		public boolean isCellEditable(int row, int col) {
			return 
				col==3 
				&& turniej.getRoundsCompleted()==2 
				&& singleGames.get(row).getRound()==-1;
		}

		@Override
		public void setValueAt(Object aValue, int row, int col) {
			int w = -1;
			SingleGame sg = singleGames.get(row);
			if(col==3 && sg.getRound()==-1) {
				if(Strings.notPlayedYet.equals((String)aValue)) w=0;
				if(Strings.whiteWon.equals((String)aValue)) w=1;
				if(Strings.blackWon.equals((String)aValue)) w=2;
				if(Strings.tie.equals((String)aValue)) w=3;
			}
			if(w!=-1) {
				if(sg.getScore()!=w) {
					sg.setScore(w);
					DB.insertOrUpdateSingleGame(sg, turniej.getId());
					recalcColors();
					this.fireTableDataChanged();
				}
			}
			else System.err.print("Do not use setValueAt in "+getClass());
		}
	}
}
