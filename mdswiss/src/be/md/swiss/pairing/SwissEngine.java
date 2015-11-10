package be.md.swiss.pairing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.md.swiss.Pairing;
import be.md.swiss.PairingImpl;
import be.md.swiss.Player;
import be.md.swiss.PlayerComparatorBySonnenbornBerger;

public class SwissEngine implements PairingEngine {

	private int roundNumber = 0;
	private Set<Pairing> allPairingsDoneSoFar = new HashSet<>();

	@Override
	public Round pairNextRound(Set<Player> players) {
		Round result;

		if (firstRound()) {
			result = pairGroupOfPlayersFirstRound(roundNumber, players);
		} else {
			result = pairGroupOfPlayers(roundNumber, players);
		}

		addPairingsToCacheAndInitializeTheCacheIfNecessary(result);
		roundNumber++;
		return result;
	}

	private boolean firstRound() {
		return roundNumber < 1;
	}

	Round pairGroupOfPlayers(int roundNumber, Set<Player> unsortedPlayers) {
		Set<Player> fromLowToHigh = new TreeSet<>(ComparatorFactory.getPlayerComparatorLowToHigh());
		fromLowToHigh.addAll(unsortedPlayers);

		Player byePlayer = null;

		boolean oddNumberOffPlayers = fromLowToHigh.size() % 2 == 1;
		Round result = null;
		if (oddNumberOffPlayers) {
			int skip = 0;
			boolean valid = false;
			result = Round.createRound(roundNumber, new ArrayList<Pairing>());
			while (!valid) {
				byePlayer = getLowestPlayerWithoutBye(fromLowToHigh, skip);
				if (byePlayer == null) {
					break;
				}

				fromLowToHigh.remove(byePlayer);
				result = pairGroupOfPlayersWithByePlayer(roundNumber, fromLowToHigh, byePlayer);

				valid = result.hasPairings() && result.pairings.size() == fromLowToHigh.size() / 2;

				if (!valid) {
					skip++;
					undoTheRemovalOfByeFromThePlayersGroup(fromLowToHigh, byePlayer);
				}
			}

		} else {
			result = pairGroupOfPlayersWithByePlayer(roundNumber, fromLowToHigh, byePlayer);
		}

		letTheByePlayerKnowHeHasBeenBye(result);
		return result;
	}

	private void undoTheRemovalOfByeFromThePlayersGroup(Set<Player> fromLowToHigh, Player byePlayer) {
		fromLowToHigh.add(byePlayer);
	}

	private void letTheByePlayerKnowHeHasBeenBye(Round result) {
		if (result.hasBye()) {
			result.bye.setBye();
		}
	}

	Round pairGroupOfPlayersWithByePlayer(int roundNumber, Set<Player> fromLowToHigh, Player byePlayer) {
		Map<Integer, ScoreGroup> scoreGroups = ScoreGroup.splitPlayersIntoScoreGroupsWithEqualPoints(fromLowToHigh);

		Set<Integer> pointsFromHighToLow = getPointsFromHighToLowFrom(scoreGroups);

		Round result = Round.createRound(roundNumber, new ArrayList<Pairing>());

		LinkedList<ScoreGroup> sgFromHighToLow = orderScoreGroupsFromHighToLow(scoreGroups, pointsFromHighToLow);

		List<PairingNode> nodes = getAllPossiblePairings(sgFromHighToLow);

		int bestColorScore = Integer.MAX_VALUE;

		for (PairingNode node : nodes) {
			List<Pairing> pairings = node.getPairingsFromThisNodeAndUp();
			Round toCheck = Round.createRound(roundNumber, pairings, byePlayer);

			if (toFewPairings(fromLowToHigh, toCheck)) {
				// log("Unpaired:" + toCheck.unpairedPlayers.size() + ": " +
				// getUnpairedPlayers(fromLowToHigh, pairings)
				// + " bye was:" + byePlayer);

			} else {
				int colorScore = determineScoreFor(pairings);
				if (colorScore < bestColorScore) {
					bestColorScore = colorScore;
					result = toCheck;
				}
			}
		}

		return result;
	}

	private LinkedList<ScoreGroup> orderScoreGroupsFromHighToLow(Map<Integer, ScoreGroup> scoreGroups,
			Set<Integer> pointsFromHighToLow) {
		LinkedList<ScoreGroup> sgFromHighToLow = new LinkedList<>();

		for (int points : pointsFromHighToLow) {
			ScoreGroup scoreGroup = scoreGroups.get(points);
			sgFromHighToLow.addLast(scoreGroup);
		}
		return sgFromHighToLow;
	}

	private int determineScoreFor(List<Pairing> pairings) {
		int sum = 0;
		for (Pairing pairing : pairings) {
			sum += pairing.getColorScore();
		}
		return sum;
	}

	private String getUnpairedPlayers(Set<Player> allPlayers, List<Pairing> pairings) {
		StringBuilder result = new StringBuilder();
		for (Player player : allPlayers) {
			appendCommaIfApplicable(result);

			boolean found = false;
			for (Pairing pairing : pairings) {
				if (pairing.containsPlayer(player)) {
					found = true;
					break;
				}
			}
			if (!found) {
				result.append(player.toString());
			}
		}
		return result.toString();
	}

	private void appendCommaIfApplicable(StringBuilder result) {
		if (result.length() > 0)
			result.append(",");
	}

	List<PairingNode> getAllPossiblePairings(LinkedList<ScoreGroup> fromHighToLow) {
		List<PairingNode> endNodes = new ArrayList<>();

		List<LinkedList<ScoreGroup>> allPossibleOrders = generateAllPossibleCombinationsFromHighToLowerImportance(fromHighToLow);
		for (LinkedList<ScoreGroup> groupsToPair : allPossibleOrders) {
			pairScoreGroupRecursive(groupsToPair, new HashSet<Player>(), endNodes);

		}
		List<PairingNode> result = new ArrayList<>();
		result.addAll(endNodes);
		return result;
	}

	List<LinkedList<ScoreGroup>> generateAllPossibleCombinationsFromHighToLowerImportance(List<ScoreGroup> fromHighToLow) {

		List<LinkedList<ScoreGroup>> result = new ArrayList<>();
		int mergeUntil = 1;

		ScoreGroup first = fromHighToLow.get(0);

		for (int offset = 0; offset < fromHighToLow.size(); offset++) {
			ScoreGroup mergedPool = ScoreGroup.copy(first);
			for (int cnt = 1; cnt < mergeUntil; cnt++) {
				ScoreGroup toMerge = fromHighToLow.get(cnt);
				mergedPool = ScoreGroup.makeCopyWithPlayersAndPointsOnly(mergedPool, toMerge);

			}

			LinkedList<ScoreGroup> aPossibleSequence = new LinkedList<>();
			if (mergeUntil > 0) {
				aPossibleSequence.add(mergedPool);
			}
			List<ScoreGroup> remaining = fromHighToLow.subList(mergeUntil, fromHighToLow.size());
			if (remaining.size() > 0) {
				aPossibleSequence.addAll(remaining);
			}
			result.add(aPossibleSequence);
			mergeUntil++;

		}
		return result;
	}

	private <T> LinkedList<T> copyOf(LinkedList<T> listToDuplicate) {
		LinkedList<T> result = new LinkedList<>();
		result.addAll(listToDuplicate);
		return result;
	}

	List<PairingNode> pairScoreGroupRecursive(LinkedList<ScoreGroup> scoregroupsfromHighToLow,
			Set<Player> unpairedPlayerFromAPreviousScoreGroup, List<PairingNode> endNodes) {

		List<PairingNode> result = new ArrayList<>();
		while (!scoregroupsfromHighToLow.isEmpty()) {
			ScoreGroup scoreGroupToPair = scoregroupsfromHighToLow.pop();
			addPossiblePairingsForSingleScoregroupToScoreGroup(scoreGroupToPair, unpairedPlayerFromAPreviousScoreGroup);

			if (!scoreGroupToPair.hasPossiblePairings()) {
				unpairedPlayerFromAPreviousScoreGroup.addAll(scoreGroupToPair.getPlayers());
				continue;
			}

			while (scoreGroupToPair.hasPossiblePairings()) {
				Round round = scoreGroupToPair.popNextPossiblePairingRoundToTry();
				List<PairingNode> childNodes = new ArrayList<>();
				List<PairingNode> childNodesMerged = new ArrayList<>();

				Set<Player> unpairedPlayers = copyOf(round.unpairedPlayers, ComparatorFactory.getPlayerComparatorHighToLow());
				Set<Player> unpairedPlayersMergedWithThisGroupsPlayers = mergePlayers(scoreGroupToPair, round);

				if (!scoregroupsfromHighToLow.isEmpty()) {
					LinkedList<ScoreGroup> scoregroupsfromHighToLowForMergedProcessing = copyOf(scoregroupsfromHighToLow);
					childNodes = pairScoreGroupRecursive(scoregroupsfromHighToLow, unpairedPlayers, endNodes);
					childNodesMerged = pairScoreGroupRecursive(scoregroupsfromHighToLowForMergedProcessing,
							unpairedPlayersMergedWithThisGroupsPlayers, endNodes);

				}
				PairingNode thisNode = PairingNode.createNodeWithChildren(round.pairings, childNodes);
				PairingNode thisNodeMerged = PairingNode.createNodeWithChildren(new ArrayList<Pairing>(), childNodesMerged);

				addIfNotEmpty(result, thisNode);
				addIfNotEmpty(result, thisNodeMerged);

				if (thisNode.isEndNode() && !thisNode.isEmpty()) {
					endNodes.add(thisNode);
				}
				if (thisNodeMerged.isEndNode() && !thisNodeMerged.isEmpty()) {
					endNodes.add(thisNodeMerged);
				}
			}
		}
		return result;

	}

	private void addIfNotEmpty(List<PairingNode> result, PairingNode thisNode) {
		if (!thisNode.isEmpty()) {
			result.add(thisNode);
		}
	}

	private Set<Player> mergePlayers(ScoreGroup scoreGroupToPair, Round round) {
		Set<Player> result = copyOf(round.unpairedPlayers, ComparatorFactory.getPlayerComparatorHighToLow());
		result.addAll(scoreGroupToPair.getPlayers());
		return result;
	}

	private <T> Set<T> copyOf(Set<T> toCopy, Comparator<T> comparator) {
		Set<T> result = new TreeSet<>(comparator);
		result.addAll(toCopy);
		return result;
	}

	private boolean toFewPairings(Set<Player> fromLowToHigh, Round result) {
		return result.pairings.size() < fromLowToHigh.size() / 2;
	}

	private Set<Integer> getPointsFromHighToLowFrom(Map<Integer, ScoreGroup> scoreGroups) {
		Set<Integer> pointsFromHighToLow = new TreeSet<Integer>(ComparatorFactory.getHighToLowComparator());
		pointsFromHighToLow.addAll(scoreGroups.keySet());
		return pointsFromHighToLow;
	}

	private void log(Object toLog) {
		Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, toLog.toString());

	}

	Player getLowestPlayerWithoutBye(Set<Player> fromLowToHigh, int skip) {

		int skipCnt = 0;
		for (Player player : fromLowToHigh) {
			if (!player.hasBeenBye()) {
				if (skipCnt == skip) {
					return player;
				}
				skipCnt++;
			}
		}

		// throw new IllegalStateException("No player found to set bye!");
		return null;
	}

	ScoreGroup addPossiblePairingsForSingleScoregroupToScoreGroup(ScoreGroup scoreGroup,
			Collection<Player> unpairedPlayerFromAPreviousScoreGroup) {

		Set<Player> remainingPlayers = createPlayerSetFrom(scoreGroup);
		LinkedList<Player> playersToPair = mergePlayers(unpairedPlayerFromAPreviousScoreGroup, remainingPlayers);

		List<Pairing> pairResult = null;
		int secondPlayerOffset = 0;
		int firstPlayerOffset = 0;

		boolean tryPair = noOverlap(firstPlayerOffset, secondPlayerOffset, playersToPair);
		boolean advancingInPlayersFromRightToLeft = true;
		if (tryPair) {
			while (tryPair) {
				pairResult = pair(playersToPair, firstPlayerOffset, secondPlayerOffset);
				if (PairResult.pairingSucces(playersToPair)) {
					scoreGroup.addNextPossiblePairingToTheEnd(Round.createRound(roundNumber, pairResult, playersToPair));
					playersToPair = mergePlayers(unpairedPlayerFromAPreviousScoreGroup, remainingPlayers);
				}

				if (advancingInPlayersFromRightToLeft) {
					secondPlayerOffset++;
					if (hasOffsetOverlap(playersToPair, firstPlayerOffset, secondPlayerOffset)) {
						playersToPair = mergePlayers(unpairedPlayerFromAPreviousScoreGroup, remainingPlayers);
						secondPlayerOffset = 0;
						firstPlayerOffset++;
						advancingInPlayersFromRightToLeft = false;
					}
				} else {

					firstPlayerOffset++;
					if (hasOffsetOverlap(playersToPair, firstPlayerOffset, secondPlayerOffset)) {
						break;
					}
				}

				tryPair = noOverlap(firstPlayerOffset, secondPlayerOffset, playersToPair);
			}
		} else {
			Set<Player> unique = new TreeSet<Player>(ComparatorFactory.getPlayerComparatorHighToLow());
			unique.addAll(playersToPair);
			unique.addAll(remainingPlayers);
			resetPlayersForNextPairingGeneration(unique, playersToPair);
		}

		return scoreGroup;
	}

	private void resetPlayersForNextPairingGeneration(Set<Player> remainingPlayers, LinkedList<Player> playersToPair) {
		playersToPair.clear();
		playersToPair.addAll(remainingPlayers);
	}

	private LinkedList<Player> mergePlayers(Collection<Player> unpairedPlayerFromAPreviousScoreGroup, Set<Player> remainingPlayers) {
		LinkedList<Player> playersToPair = copyOfSetToList(remainingPlayers);
		if (notEmpty(unpairedPlayerFromAPreviousScoreGroup)) {
			playersToPair.addAll(unpairedPlayerFromAPreviousScoreGroup);
		}
		Collections.sort(playersToPair, ComparatorFactory.getPlayerComparatorHighToLow());
		return playersToPair;
	}

	private <T> LinkedList<T> copyOfSetToList(Set<T> toCopy) {
		LinkedList<T> result = new LinkedList<>();
		result.addAll(toCopy);
		return result;
	}

	private boolean notEmpty(Collection<Player> unpairedPlayerFromAPreviousScoreGroup) {
		return unpairedPlayerFromAPreviousScoreGroup != null && !unpairedPlayerFromAPreviousScoreGroup.isEmpty();
	}

	private Set<Player> createPlayerSetFrom(ScoreGroup scoreGroup) {
		Set<Player> players = new TreeSet<Player>(ComparatorFactory.getPlayerComparatorHighToLow());
		players.addAll(scoreGroup.getPlayers());
		return players;
	}

	boolean noOverlap(int firstPlayerOffset, int secondPlayerOffset, LinkedList<Player> playersToPair) {
		return !hasOffsetOverlap(playersToPair, firstPlayerOffset, secondPlayerOffset);
	}

	List<Pairing> pair(LinkedList<Player> unpairedPlayers, final int firstPlayerOffset, final int secondPlayerOffset) {

		int newOffsetFirstPlayer = 0;
		int newOffsetSecondPlayer = 0;
		List<Pairing> result = new ArrayList<>();

		validateOffsetOverlap(unpairedPlayers, firstPlayerOffset, secondPlayerOffset);
		Player white = unpairedPlayers.get(firstPlayerOffset);

		// pick the last one
		int locationSecondPlayer = calculateLocationInListAndCheckBoundries(unpairedPlayers, secondPlayerOffset);
		Player black = unpairedPlayers.get(locationSecondPlayer);

		boolean pairingSucceeded = false;
		boolean whiteAndBlackAreTheSame = white.equals(black);

		if (!whiteAndBlackAreTheSame) {
			Pairing newPairing = PairingImpl.createPairing(white, black);
			if (!pairingAlreadyOccured(newPairing) && !newPairing.hasColorConflicts()) {
				result.add(newPairing);
				unpairedPlayers.remove(locationSecondPlayer);
				unpairedPlayers.remove(firstPlayerOffset);
				pairingSucceeded = true;
				newOffsetFirstPlayer = 0;
				newOffsetSecondPlayer = 0;
			}
		}

		if (!pairingSucceeded) {
			// pairing not possible. push the offsets

			newOffsetSecondPlayer = secondPlayerOffset + 1;

			if (hasOffsetOverlap(unpairedPlayers, newOffsetFirstPlayer, newOffsetSecondPlayer)) {
				// nothing more we can do here
				return result;
			}
		}
		if (validToDoAnotherPairing(unpairedPlayers, newOffsetFirstPlayer, newOffsetSecondPlayer)) {
			List<Pairing> pairings = pair(unpairedPlayers, newOffsetFirstPlayer, newOffsetSecondPlayer);
			if (!pairings.isEmpty()) {
				result.addAll(pairings);
				newOffsetFirstPlayer = 0;
				newOffsetSecondPlayer = 0;
			}
		}

		return result;
	}

	private boolean validToDoAnotherPairing(LinkedList<Player> players, int newOffsetFirstPlayer, int newOffsetSecondPlayer) {
		return players.size() >= 2 && offsetsValid(players, newOffsetFirstPlayer, newOffsetSecondPlayer);
	}

	boolean offsetsValid(Collection<Player> players, int frontOffset, int backOffset) {
		return !hasOffsetOverlap(players, frontOffset, backOffset) && validFrontOffset(players, frontOffset)
				&& validBackOffset(players, backOffset);
	}

	private void validateOffsetOverlap(Collection<Player> players, int frontOffset, int backOffset) {
		if (hasOffsetOverlap(players, frontOffset, backOffset))
			throw new IllegalArgumentException("backOffset - frontOffset overlap! backOffset:" + backOffset + "frontOffset:"
					+ frontOffset + " with " + players.size() + " players");

	}

	private boolean hasOffsetOverlap(Collection<Player> players, int frontOffset, int backOffset) {
		// e.g in a 4 sized list you have indexes 0,1,2,3. You have 2 players.
		// Offset 0 means nothing,3 is the maximum. The max INDEX for the first
		// player is 2.So that we have room for the second player on index 3. In
		// that case the OFFSET of the first player is 2 and of the second
		// player it is 0 (his offset (backOffset) comes from the right)
		// So the size-2 is the maximum the sum of front and back can be.

		boolean ok = frontOffset + backOffset <= players.size() - 2;
		return !ok;
	}

	private boolean validBackOffset(Collection<Player> players, int backOffset) {
		return !(backOffset + 1 == players.size());
	}

	private boolean validFrontOffset(Collection<Player> players, final int frontOffset) {
		return !(frontOffset > players.size() / 2);
	}

	private int calculateLocationInListAndCheckBoundries(LinkedList<Player> players, final int backOffset) {
		return players.size() - 1 - backOffset;
	}

	void addPairingsToCacheAndInitializeTheCacheIfNecessary(Round currentRound) {
		allPairingsDoneSoFar.addAll(currentRound.pairings);
		allPairingsDoneSoFar.add(currentRound.byePairing);

	}

	Round pairGroupOfPlayersFirstRound(int round, Set<Player> players) {
		List<Pairing> pairings = new ArrayList<>();
		List<Player> sortedPlayers = sortPlayerListFromHighToLowRating(players);
		Player bye = setPlayerByeIfOddSize(sortedPlayers);

		int size = sortedPlayers.size();
		int half = size / 2;
		boolean colorSwitch = true;
		for (int i = 0; i < half; i++) {
			Player firstPlayer = sortedPlayers.get(i);

			Player secondPlayer = sortedPlayers.get(i + half);
			if (colorSwitch) {
				Pairing pairing = PairingImpl.createPairing(firstPlayer, secondPlayer);
				pairings.add(pairing);
			} else {
				Pairing pairing = PairingImpl.createPairing(secondPlayer, firstPlayer);
				pairings.add(pairing);
			}
			colorSwitch = !colorSwitch;

		}

		return Round.createRound(round, pairings, bye);
	}

	private Player setPlayerByeIfOddSize(List<Player> sortedPlayers) {
		Player bye = null;
		if (isOddSize(sortedPlayers.size())) {

			int byeIndex = sortedPlayers.size() - 1;
			bye = sortedPlayers.get(byeIndex);
			sortedPlayers.remove(byeIndex);
			bye.setBye();
		}
		return bye;
	}

	private boolean isOddSize(int size) {
		return size % 2 == 1;
	}

	boolean pairingAlreadyOccured(Pairing pairing) {
		return allPairingsDoneSoFar.contains(pairing);

	}

	List<Player> sortPlayerListFromHighToLowRating(Set<Player> players) {
		Set<Player> sortedSet = new TreeSet<Player>(PlayerComparatorBySonnenbornBerger.createFromHighToLowPointsSonnebornRating());
		sortedSet.addAll(players);
		return new ArrayList<Player>(sortedSet);
	}

	void addPairingToPairingsCacheForTesting(Pairing pairing) {
		allPairingsDoneSoFar.add(pairing);
	}

	void clearPairingCacheForTesting() {
		allPairingsDoneSoFar.clear();
	}
}
