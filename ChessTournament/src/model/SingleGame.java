package model;

/**
 * Przechowuje dane o pojedynczej rozgrywce - id graczy, wynik, czy była rozgrywana
 */
public class SingleGame {
	private Integer id;
	private final int competitor1, competitor2;
	private int score;
	private int board;
	private boolean wasPlayed;
	private final int round;
	
	public SingleGame(Integer id, int competitor1, int competitor2, int score,
			boolean wasPlayed, int round, int board) {
		this.id = id;
		this.competitor1 = competitor1;
		this.competitor2 = competitor2;
		this.score = score;
		this.wasPlayed = wasPlayed;
		this.round = round;
		this.board = board;
	}
	
	public SingleGame(Competitor competitor1, Competitor competitor2, int round, int board) {
		this.id = null;
		this.competitor1 = competitor1.getId();
		this.competitor2 = competitor2.getId();
		this.score = 0;
		this.wasPlayed = false;
		this.round = round;
		this.board = board;
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getCompetitor1() {
		return competitor1;
	}

	public int getCompetitor2() {
		return competitor2;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean getWasPlayed() {
		return wasPlayed;
	}

	public void setWasPlayed(boolean wasPlayed) {
		this.wasPlayed = wasPlayed;
	}
	
	public Integer getRound() {
		return round;
	}
	
	public Integer getBoard() {
		return board;
	}
	
	@Override
    public boolean equals(Object obj) {
    	if(obj instanceof SingleGame) {
    		SingleGame sg1 = this, sg2=(SingleGame)obj;
    		if(sg1.getCompetitor1()==sg2.getCompetitor1() && sg1.getCompetitor2()==sg2.getCompetitor2()) return true;
    		if(sg1.getCompetitor1()==sg2.getCompetitor2() && sg1.getCompetitor2()==sg2.getCompetitor1()) return true;
    	}
    	return false;
    }
}
