package be.md.swiss.pairing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import be.md.swiss.Pairing;
import be.md.swiss.Player;

public class Round {

	public final List<Pairing> pairings;
	public final int roundNumber;
	public final Player bye;
	public final Pairing byePairing;
	public final Set<Player> unpairedPlayers;

	private Round(int round, List<Pairing> pairings, Collection<Player> unpairedPlayers) {
		this.roundNumber = round;
		List<Pairing> tempPairings = new ArrayList<>();
		tempPairings.addAll(pairings);
		this.pairings = Collections.unmodifiableList(tempPairings);
		Set<Player> unpaired = new TreeSet<>(ComparatorFactory.getPlayerComparatorHighToLow());
		unpaired.addAll(unpairedPlayers);
		this.unpairedPlayers = Collections.unmodifiableSet(unpaired);
		if (!unpairedPlayers.isEmpty())
			bye = unpairedPlayers.iterator().next();
		else
			bye = null;
		byePairing = new ByePairing(bye);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("round:" + roundNumber + " ");
		sb.append("bye:" + bye + " ");
		for (Pairing p : pairings)
			sb.append(p);
		return sb.toString();
	}

	public static Round mergeRounds(int roundNumber, Collection<Round> pairingsPerScoreGroup, Player bye) {
		Set<Pairing> allPairings = new HashSet<>();

		Set<Player> unpairedPlayersUnique = new TreeSet<Player>(ComparatorFactory.getPlayerComparatorHighToLow());
		for (Round round : pairingsPerScoreGroup) {
			allPairings.addAll(round.pairings);
			unpairedPlayersUnique.addAll(round.unpairedPlayers);
		}

		Set<Player> removeUs = new TreeSet<>(ComparatorFactory.getPlayerComparatorHighToLow());
		for (Player possibleUnpaired : unpairedPlayersUnique) {
			if (playerPlaysIn(possibleUnpaired, allPairings)) {
				removeUs.add(possibleUnpaired);
			}
		}
		unpairedPlayersUnique.removeAll(removeUs);
		if (bye != null) {
			unpairedPlayersUnique.add(bye);
		}
		return new Round(roundNumber, toList(allPairings), toList(unpairedPlayersUnique));
	}

	private static <T> List<T> toList(Set<T> unpairedPlayersUnique) {
		List<T> unpairedList = new ArrayList<>();
		unpairedList.addAll(unpairedPlayersUnique);
		return unpairedList;
	}

	private static boolean playerPlaysIn(Player floater, Collection<Pairing> pairingsToCheckForFloater) {
		for (Pairing pairing : pairingsToCheckForFloater) {
			if (pairing.containsPlayer(floater))
				return true;
		}
		return false;
	}

	public boolean hasBye() {
		return bye != null;
	}

	public static Round createRound(int round, List<Pairing> argPairings) {
		return new Round(round, argPairings, new ArrayList<Player>());
	}

	public static Round createRound(int round, List<Pairing> argPairings, Player argBye) {
		List<Player> unpaired = new ArrayList<Player>();
		unpaired.add(argBye);
		return new Round(round, argPairings, unpaired);
	}

	public static Round createRound(int roundNr, List<Pairing> pairings, Collection<Player> unpairedPlayers) {
		return new Round(roundNr, pairings, unpairedPlayers);
	}

	public boolean hasPairings() {
		return !pairings.isEmpty();
	}

}
