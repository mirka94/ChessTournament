package panel;

import java.awt.Dimension;
import java.util.List;
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
import tools.SwissTools;

public class SwissGamesPanel extends AbstractGamesPanel {
	private JButton finishB = new JButton(Strings.endRound);
	private List<SingleGame> rawGames;
	private onFailListener failListener;
	
	/**
	 * @param t - id turnieju
	 * @param db - baza danych
	 */
	public SwissGamesPanel(Tournament t, Database db, onFailListener failListener){
		super(t,db);
		this.failListener = failListener;
	}
	
	@FunctionalInterface
	public interface onFailListener {
		public abstract void onFail();
	}
	
	public void updateGames() {
		rawGames = DB.getSingleGames(turniej.getId(), false);
		singleGames = rawGames.stream().filter(sg->sg.getCompetitorB()!=sg.getCompetitorW())
				.collect(Collectors.toList());
	}
	
	public void initComponents() {
		removeAll();
		competitors = DB.getCompetitors(turniej.getId());
		updateGames();
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
				if(turniej.getRoundsCompleted()>=turniej.getRounds()) 
					finishB.setVisible(false);
				else {
					turniej.setRoundsCompleted(turniej.getRoundsCompleted()+1);
					String data = SwissTools.genJaVaFoData(turniej, competitors, rawGames);
					turniej.setRoundsCompleted(turniej.getRoundsCompleted()-1, true);
					if(data==null) {
						System.err.println("startInfo could not be generated");
						failListener.onFail();
					}
					else if(!SwissTools.saveJaVaFoData(data)) {
						System.err.println("startInfo could not be saved");
						failListener.onFail();
					}
					else if(!SwissTools.runJaVaFo()) {
						System.err.println("JaVaFo execution fail");
						failListener.onFail();
					}
					else {
						List<SingleGame> games = SwissTools.readPairings(competitors, turniej.getRoundsCompleted());
						if(games!=null) {
							turniej.setRoundsCompleted(turniej.getRoundsCompleted()+1);
							DB.insertOrUpdateSingleGame(games, turniej.getId());
							DB.insertOrUpdateTournament(turniej);
							initComponents();
						}
						else System.err.println("Null games");
					}
				}
			}
		});
		if(turniej.getRoundsCompleted()+1<turniej.getRounds()) add(finishB);
	}

	private class MyTableModel extends AbstractGamesPanel.MyTableModel {
		@Override
		public boolean isCellEditable(int row, int col) {
			SingleGame sg = singleGames.get(row);
			System.out.println("tR - "+turniej.getRoundsCompleted()+", sgR - "+sg.getRound());
			return turniej.getRoundsCompleted()==sg.getRound() && col==3;
		}
		
		public Object getValueAt(int row, int col) {
			if(col==3) {
				SingleGame sg = singleGames.get(row);
				if(!sg.getWasPlayed()) return super.getValueAt(row, col)+" #d";
			}
			return super.getValueAt(row, col);
		};
		
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
