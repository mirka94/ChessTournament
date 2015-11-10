package be.md.swiss;

public interface Pairing {
	Player getWhite();

	Player getBlack();

	boolean isBye();

	boolean existsOfPlayers(Player p1, Player p2);

	void whiteWins();

	void blackWins();

	void draw();

	boolean hasColorConflicts();

	boolean containsPlayer(Player floater);

	int getWhiteRating();

	int getBlackRaring();

	int getColorScore();

	public boolean isPlayed();

	public boolean isDraw();

	public boolean whiteWon();
}
