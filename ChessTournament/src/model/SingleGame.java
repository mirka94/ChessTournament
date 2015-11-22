package model;

/**
 * Przechowuje dane o pojedynczej rozgrywce - id graczy, wynik, czy była rozgrywana
 */
public class SingleGame {
	private Integer id;
	private int competitor1, competitor2;
	private int score;
	private boolean wasPlayed;
	private boolean inProgress;
	
	public SingleGame(Integer id, int competitor1, int competitor2, int score,
			boolean wasPlayed, boolean inProgress) {
		this.id = id;
		this.competitor1 = competitor1;
		this.competitor2 = competitor2;
		this.score = score;
		this.wasPlayed = wasPlayed;
		this.inProgress = inProgress;
	}

	public Integer getId() {
		return id;
	}

	public int getCompetitor1() {
		return competitor1;
	}

	public void setCompetitor1(int competitor1) {
		this.competitor1 = competitor1;
	}

	public int getCompetitor2() {
		return competitor2;
	}

	public void setCompetitor2(int competitor2) {
		this.competitor2 = competitor2;
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
}
