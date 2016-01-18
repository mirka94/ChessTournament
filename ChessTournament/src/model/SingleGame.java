package model;

import java.io.Serializable;

/**
 * Przechowuje dane o pojedynczej rozgrywce - id graczy, wynik, oraz 
 * informację w której rundzie (czy też w finałach) rozgrywka była zawarta
 */
public class SingleGame implements Serializable {
	private static final long serialVersionUID = -3007183465072503118L;
	private Integer id;
	private final int competitorW, competitorB;
	private int score;
	private int board;
	private final int round;
	
	public SingleGame(Integer id, int competitorW, int competitorB, int score,
			int round, int board) {
		this.id = id;
		this.competitorW = competitorW;
		this.competitorB = competitorB;
		this.score = score;
		this.round = round;
		this.board = board;
	}
	
	public SingleGame(Competitor competitorW, Competitor competitorB, int round, int board) {
		this.id = null;
		this.competitorW = competitorW.getId();
		this.competitorB = competitorB.getId();
		this.score = 0;
		this.round = round;
		this.board = board;
	}
	
	public SingleGame(Competitor c, int round, int score) { // swiss bye / disqualified
		this.id = null;
		this.competitorB = c.getId(); 
		this.competitorW = c.getId();
		this.score = score;
		this.round = round;
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getCompetitorW() {
		return competitorW;
	}

	public int getCompetitorB() {
		return competitorB;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public Integer getRound() {
		return round;
	}
	
	public Integer getBoard() {
		return board;
	}
	
	public void setBoard(int board) {
		this.board = board;
	}
	
	@Override
    public boolean equals(Object obj) {
    	if(obj instanceof SingleGame) {
    		SingleGame sg1 = this, sg2=(SingleGame)obj;
    		if(sg1.getCompetitorW()==sg2.getCompetitorW() && sg1.getCompetitorB()==sg2.getCompetitorB()) return true;
    		if(sg1.getCompetitorW()==sg2.getCompetitorB() && sg1.getCompetitorB()==sg2.getCompetitorW()) return true;
    	}
    	return false;
    }
}
