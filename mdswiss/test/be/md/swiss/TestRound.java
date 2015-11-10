package be.md.swiss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import be.md.swiss.pairing.Round;

public class TestRound {
	private Player highRated = PlayerUtils.createRatedPlayer(2500);
	private Player classARated = PlayerUtils.createRatedPlayer(1900);
	private Player classBRated = PlayerUtils.createRatedPlayer(1600);
	private Player classCRated = PlayerUtils.createRatedPlayer(1400);
	private Player classDRated = PlayerUtils.createRatedPlayer(1200);
	private Player lowRated = PlayerUtils.createRatedPlayer(1100);
	private Player beginner = PlayerUtils.createRatedPlayer(800);

	@Test
	public void testMergeRounds() {
		List<Round> roundToMerge = new ArrayList<>();
		Round round = Round.createRound(1, createPairings(highRated, lowRated));
		Round round2 = Round.createRound(1, createPairings(classARated, classBRated));
		Round round3 = Round.createRound(1, createPairings(classCRated, classDRated), beginner);
		for (int i = 0; i < 100; i++)
			roundToMerge.add(round);

		roundToMerge.add(round2);
		roundToMerge.add(round3);

		Round merged = Round.mergeRounds(10, roundToMerge, beginner);
		assertTrue(merged.pairings.size() == 3);
		assertEquals(merged.bye, beginner);
	}

	private List<Pairing> createPairings(Player p1, Player p2) {
		List<Pairing> pairings = new ArrayList<>();
		pairings.add(PairingImpl.createPairing(p1, p2));
		return pairings;
	}
}
