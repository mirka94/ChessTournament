package be.md.swiss;

import java.util.HashSet;
import java.util.Set;

import be.md.swiss.exceptions.NotEnoughPlayersException;
import be.md.swiss.pairing.PairingEngine;
import be.md.swiss.pairing.Round;
import be.md.swiss.pairing.SwissEngine;

public class Tournament {

	private int numberOfRounds;
	private Set<Player> players = new HashSet<>();
	private PairingEngine pairingEngine = new SwissEngine();

	private Tournament() {
	}

	public static Tournament createTournament(int numberOfRounds) {
		Tournament tournament = new Tournament();
		tournament.setRounds(numberOfRounds);
		return tournament;
	}

	private void setRounds(int numberOfRounds) {
		this.numberOfRounds = numberOfRounds;
	}

	public int getRounds() {
		return numberOfRounds;
	}

	public void addPlayer(Player player) {
		players.add(player);
	}

	public int getNumberOfPlayers() {
		return players.size();
	}

	public Round pairNextRound() {
		validateIfThereAreEnoughPlayers();
		return pairingEngine.pairNextRound(players);

	}

	private void validateIfThereAreEnoughPlayers() {
		int maxRoundsPossible = getMaxNumberOfRoundsPossible();

		if (maxRoundsPossible < numberOfRounds) {
			throw new NotEnoughPlayersException(players.size(), numberOfRounds);
		}

	}

	private int getMaxNumberOfRoundsPossible() {
		return (players.size() - 1);
	}

}
