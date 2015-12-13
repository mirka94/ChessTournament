package model;

/**
 * Przechowuje dane o pojedynczej rozgrywce - id graczy, wynik, czy była rozgrywana
 */
public class SingleGame {
	private Integer id;
	private final int competitor1, competitor2;
	private int score;
	private boolean wasPlayed;
	/**
	 * czy można jeszcze zmieniać wynik
	 */
	private boolean inProgress;
	private final int round;
	
	public SingleGame(Integer id, int competitor1, int competitor2, int score,
			boolean wasPlayed, boolean inProgress, int round) {
		this.id = id;
		this.competitor1 = competitor1;
		this.competitor2 = competitor2;
		this.score = score;
		this.wasPlayed = wasPlayed;
		this.inProgress = inProgress;
		this.round = round;
	}
	
	public SingleGame(Competitor competitor1, Competitor competitor2, int round) {
		this.id = null;
		this.competitor1 = competitor1.getId();
		this.competitor2 = competitor2.getId();
		this.score = 0;
		this.wasPlayed = false;
		this.inProgress = false;
		this.round = round;
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

	public boolean isInProgress() {
		return inProgress;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

	public Integer getRound() {
		return round;
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
