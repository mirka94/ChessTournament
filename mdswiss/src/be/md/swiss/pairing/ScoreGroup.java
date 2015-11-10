package be.md.swiss.pairing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import be.md.swiss.Player;

public class ScoreGroup {

	private List<Player> players = new ArrayList<>();
	private LinkedList<Round> possiblePairings = new LinkedList<>();
	private int points = 0;

	private ScoreGroup(List<Player> players, int points) {
		this.players.addAll(players);
		this.points = points;

	}

	public static ScoreGroup createScoreGroup(List<Player> players, int points) {
		return new ScoreGroup(players, points);
	}

	public static Map<Integer, ScoreGroup> splitPlayersIntoScoreGroupsWithEqualPoints(Set<Player> players) {

		Map<Integer, ScoreGroup> result = new HashMap<>();
		for (Player player : players) {
			int points = player.getPoints();
			if (result.containsKey(points)) {
				ScoreGroup sg = result.get(points);
				sg.addPlayer(player);
			} else {
				List<Player> playerToAdd = new ArrayList<>();
				playerToAdd.add(player);
				ScoreGroup sg = new ScoreGroup(playerToAdd, points);
				result.put(points, sg);
			}
		}
		return result;

	}

	private void addPlayer(Player player) {
		if (player != null && !players.contains(player))
			players.add(player);
		else {
			throw new IllegalArgumentException("player already in group");
		}
	}

	public List<Player> getPlayers() {
		return players;

	}

	public boolean hasOnlyOnePlayer() {
		return players.size() == 1;
	}

	public Player getSinglePlayer() {
		if (players.size() == 1) {
			return players.get(0);
		} else {
			throw new IllegalStateException("Scoregroup has more than 1 player! (so you shouldn't call this method.)");
		}
	}

	public void addNextPossiblePairingToTheEnd(Round pairingsForScoreGroup) {
		possiblePairings.addLast(pairingsForScoreGroup);
	}

	public Round popNextPossiblePairingRoundToTry() {
		return possiblePairings.pop();
	}

	public boolean hasPossiblePairings() {
		return !possiblePairings.isEmpty();
	}

	public Object numberOfPossiblePairings() {
		return possiblePairings.size();
	}

	public int points() {
		return points;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Scoregroup players:[");
		for (Player p : players)
			builder.append(p);
		builder.append("]");
		return builder.toString();
	}

	public static ScoreGroup copy(ScoreGroup first) {
		ScoreGroup result = ScoreGroup.createScoreGroup(first.players, first.points);
		return result;
	}

	public static ScoreGroup makeCopyWithPlayersAndPointsOnly(ScoreGroup first, ScoreGroup second) {
		ScoreGroup result = first.copyWithPlayersAndPoints();

		result.players.addAll(second.players);
		return result;

	}

	private ScoreGroup copyWithPlayersAndPoints() {
		ScoreGroup result = ScoreGroup.createScoreGroup(players, points);
		return result;
	}
}
