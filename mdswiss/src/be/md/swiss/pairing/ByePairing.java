package be.md.swiss.pairing;

import be.md.swiss.Pairing;
import be.md.swiss.Player;

public class ByePairing implements Pairing {

	public final Player byePlayer;

	public ByePairing(Player byePlayer) {
		this.byePlayer = byePlayer;
	}

	@Override
	public Player getBlack() {
		throw new UnsupportedOperationException("Bye Pairing");
	}

	@Override
	public Player getWhite() {
		throw new UnsupportedOperationException("Bye Pairing");
	}

	@Override
	public boolean isBye() {
		return true;
	}

	@Override
	public boolean existsOfPlayers(Player p1, Player p2) {
		return false;
	}

	@Override
	public void whiteWins() {
		throw new UnsupportedOperationException("Bye Pairing");

	}

	@Override
	public void blackWins() {
		throw new UnsupportedOperationException("Bye Pairing");

	}

	@Override
	public void draw() {
		throw new UnsupportedOperationException("Bye Pairing");

	}

	@Override
	public boolean hasColorConflicts() {
		throw new UnsupportedOperationException("Bye Pairing");
	}

	@Override
	public boolean containsPlayer(Player floater) {
		return byePlayer.equals(floater);
	}

	@Override
	public int getWhiteRating() {
		throw new UnsupportedOperationException("Bye Pairing");
	}

	@Override
	public int getBlackRaring() {
		throw new UnsupportedOperationException("Bye Pairing");
	}

	@Override
	public int getColorScore() {
		return byePlayer.getColorScore();
	}

	@Override
	public boolean isPlayed() {
		return true;
	}

	@Override
	public boolean isDraw() {
		return false;
	}

	@Override
	public boolean whiteWon() {
		return false;
	}

}
