package panel;

import java.awt.Component;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import model.Competitor;
import model.Database;
import model.SingleGame;
import model.Tournament;
import res.Colors;
import res.Strings;

public abstract class AbstractGamesPanel extends JPanel{
	private static final long serialVersionUID = 5147804241488047339L;
	protected final Tournament turniej;
	protected final Database DB;
	protected List<Competitor> competitors;
	protected Map<Integer, Competitor> competitorMap;
	protected List<SingleGame> singleGames;
	protected List<SingleGame> currentlyPlayedGames;
	
	/**
	 * @param t - id turnieju
	 * @param db - baza danych
	 */
	public AbstractGamesPanel(Tournament t, Database db){
		this.turniej = t;
		this.DB = db;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
	
	protected void setDisqualifiedPlayersScores() {
		for(SingleGame sg : singleGames) {
			if(sg.getScore()==0) {
				Competitor cW = competitorMap.get(sg.getCompetitorW());
				Competitor cB = competitorMap.get(sg.getCompetitorB());
				if(cW.getIsDisqualified() && cB.getIsDisqualified()) sg.setScore(3);
				else if(cW.getIsDisqualified()) sg.setScore(2);
				else if(cB.getIsDisqualified()) sg.setScore(1);
				if(sg.getScore()!=0) DB.insertOrUpdateSingleGame(sg, turniej.getId());
			}
		}
	}
	
	public abstract void initComponents();
	
	final void recalcColors() {
		currentlyPlayedGames = new ArrayList<>(turniej.getBoards());
		currentlyPlayedGames.clear();;
		List<Integer> 	playingCompetitors 	= new ArrayList<>(2*turniej.getBoards()),
						freeBoards 			= new LinkedList<>();
		for(int i=0; i<turniej.getBoards(); ++i) freeBoards.add(i);
		for(SingleGame sg : singleGames.stream().filter(g->g.getScore()==0&&g.getBoard()>=0)
				.collect(Collectors.toList())){
			freeBoards.remove(sg.getBoard());
			playingCompetitors.add(sg.getCompetitorW());
			playingCompetitors.add(sg.getCompetitorB());
			currentlyPlayedGames.add(sg);
		};
		for(SingleGame sg : singleGames.stream().filter(g->g.getScore()==0&&g.getBoard()<0)
				.collect(Collectors.toList())){
			if(freeBoards.isEmpty()) break;
			if(!playingCompetitors.contains(sg.getCompetitorW()) &&
			   !playingCompetitors.contains(sg.getCompetitorB())) 
			{
				int board = freeBoards.get(0);
				sg.setBoard(board);
				freeBoards.remove(0);
				playingCompetitors.add(sg.getCompetitorW());
				playingCompetitors.add(sg.getCompetitorB());
				currentlyPlayedGames.add(sg);
			}	
		};
	}
	
	class MyCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 3688274195106322671L;

		@Override
		  public Component getTableCellRendererComponent(
				  JTable t, Object v, boolean isSelected, boolean hasFocus, int row, int col) {
		    Component c = super.getTableCellRendererComponent(t, v, isSelected, hasFocus, row, col);
		    c.setBackground(t.isRowSelected(row)?
		    	(currentlyPlayedGames.contains(singleGames.get(row)) ? Colors.selInProgress : Colors.selNormal) : 
		    	(currentlyPlayedGames.contains(singleGames.get(row)) ? Colors.InProgress	: Colors.normal)	
		    );
		  return c;
		}
	}
	
	protected abstract class MyTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -8079013606990307646L;
		final String[] columnNames = {Strings.board, Strings.playsWithWhite, Strings.playsWithBlack, Strings.score};
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
			if(col==0 && sg.getBoard()==-1) return " - ";
			if(col==0) return sg.getBoard()+1;
			if(col==1) return competitorMap.get(sg.getCompetitorW());
			if(col==2) return competitorMap.get(sg.getCompetitorB());
			if(col==3) {
				if(sg.getScore()==0) return Strings.notPlayedYet;
				if(sg.getScore()==1) return Strings.whiteWon;
				if(sg.getScore()==2) return Strings.blackWon;
				if(sg.getScore()==3) return Strings.tie;
			}
	        return null;
		}
	}
}
