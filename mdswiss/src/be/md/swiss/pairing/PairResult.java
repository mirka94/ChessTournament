package be.md.swiss.pairing;

import java.util.Collections;
import java.util.List;

import be.md.swiss.Pairing;
import be.md.swiss.Player;

public class PairResult {
	private final List<Pairing> pairing;
	private final List<Player> unpairedPlayers;

	public PairResult(List<Pairing> pairing, List<Player> unpairedPlayers) {
		this.pairing = pairing;
		this.unpairedPlayers = unpairedPlayers;
	}

	public List<Pairing> pairing() {
		return Collections.unmodifiableList(pairing);
	}

	boolean hasBye() {
		return unpairedPlayers.size() > 0;
	}

	public boolean pairingSucces() {
		return pairingSucces(unpairedPlayers);
	}

	public Player getFloater() {
		Player floater = null;
		if (unpairedPlayers.size() > 0) {
			if (unpairedPlayers.size() > 1)
				throw new UnsupportedOperationException("TODO implement 2 players unpaired!");
			floater = unpairedPlayers.get(0);
		}
		return floater;
	}

	public static boolean pairingSucces(List<Player> unpairedPlayers) {
		return unpairedPlayers.size() < 2;
	}

	public List<Player> getUnpairedPlayers() {
		return unpairedPlayers;
	}
}
