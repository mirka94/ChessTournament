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

public class GamesPanel extends AbstractGamesPanel {
	private JButton finishB = new JButton(Strings.endEliminations);
	private final onEliminationsEndListener listener;
	
	/**
	 * @param t - id turnieju
	 * @param db - baza danych
	 */
	public GamesPanel(Tournament t, Database db, onEliminationsEndListener listener){
		super(t,db);
		this.listener = listener;
	}
	
	public void initComponents() {
		removeAll();
		competitors = DB.getCompetitors(turniej.getId());
		singleGames = DB.getSingleGames(turniej.getId(), false);
		competitorMap = competitors.stream()
				.collect(Collectors.toMap(c->c.getId(), c->c));
		setDisqualifiedPlayersScores();
		recalcColors();
		JTable table = new JTable(new MyTableModel());
		table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(
        		new JComboBox<String>(new String[] {Strings.notPlayedYet, Strings.whiteWon, Strings.blackWon, Strings.tie})
        ));
		table.setDefaultRenderer(String.class, new MyCellRenderer());
		add(new JScrollPane(table));
		add(Box.createRigidArea(new Dimension(0, 20)));
		finishB.addActionListener((e)->{
			if(singleGames.stream().filter(sg->sg.getScore()==0).count()>0) {
				Dialogs.gryBezWyniku();
			}
			else {
				finishB.setVisible(false);
				listener.onEliminationsEnd();
			}
		});
		if(turniej.getRoundsCompleted()<1) add(finishB);
	}
	
	@FunctionalInterface 
	public interface onEliminationsEndListener {
		public void onEliminationsEnd();
	}
	
	private class MyTableModel extends AbstractGamesPanel.MyTableModel {
		@Override
		public boolean isCellEditable(int row, int col) {
			return col==3 && turniej.getRoundsCompleted()==0;
		}

		@Override
		public void setValueAt(Object aValue, int row, int col) {
			int w = -1;
			SingleGame sg = singleGames.get(row);
			if(col==3) {
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
