package be.md.swiss.exceptions;

public class NotEnoughPlayersException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotEnoughPlayersException(int nrOfPlayers, int numberOFRounds) {
		super("Not enough players (" + nrOfPlayers + " for " + numberOFRounds
				+ " rounds");
	}

}
