package be.md.swiss.pairing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import be.md.swiss.Pairing;
import be.md.swiss.PairingImpl;
import be.md.swiss.Player;
import be.md.swiss.PlayerUtils;

public class TestSwissPairingEngine {
	private Player highRated = PlayerUtils.createRatedPlayer(2500);
	private Player classARated = PlayerUtils.createRatedPlayer(1900);
	private Player classBRated = PlayerUtils.createRatedPlayer(1600);
	private Player classCRated = PlayerUtils.createRatedPlayer(1400);
	private Player classDRated = PlayerUtils.createRatedPlayer(1200);
	private Player lowRated = PlayerUtils.createRatedPlayer(1100);
	private Player beginner = PlayerUtils.createRatedPlayer(800);

	@Before
	public void init() {

	}

	@Test
	public void firstRoundPairsTopHalfAgainstBottomHalfOfAnEloSortedListOfPlayers() {
		PairingEngine engine = new SwissEngine();
		Set<Player> players = createPlayerSet();
		engine.pairNextRound(players);
	}

	private Set<Player> createPlayerSet() {
		Set<Player> players = new HashSet<Player>();

		players.add(lowRated);
		players.add(classARated);
		players.add(highRated);
		players.add(classBRated);
		players.add(classCRated);
		players.add(classDRated);
		return players;
	}

	@Test
	public void testSetToSortedPlayerListFromHighToLowRating() {
		Set<Player> unsorted = createPlayerSet();
		SwissEngine engine = new SwissEngine();
		List<Player> ordered = engine.sortPlayerListFromHighToLowRating(unsorted);
		Iterator<Player> iterator = ordered.iterator();
		assertEquals(highRated, iterator.next());
		assertEquals(classARated, iterator.next());
		assertEquals(classBRated, iterator.next());
		assertEquals(classCRated, iterator.next());
		assertEquals(classDRated, iterator.next());
		assertEquals(lowRated, iterator.next());
	}

	@Test
	public void testPairEvenGroupOfPlayersFirstRound() {
		SwissEngine engine = new SwissEngine();
		Set<Player> unsorted = createPlayerSet();
		assertTrue(unsorted.size() % 2 == 0);
		Round firstRound = engine.pairGroupOfPlayersFirstRound(0, unsorted);
		List<Pairing> pairings = firstRound.pairings;
		assertTrue(pairings.size() == 3);

		Iterator<Pairing> iterator = pairings.iterator();
		testPairingExistsOfPlayers(iterator.next(), highRated, classCRated);
		testPairingExistsOfPlayers(iterator.next(), classARated, classDRated);
		testPairingExistsOfPlayers(iterator.next(), classBRated, lowRated);
		assertNull(firstRound.bye);
	}

	@Test
	public void testPairOddGroupOfPlayersFirstRound() {
		SwissEngine engine = new SwissEngine();
		Set<Player> unsorted = createPlayerSet();
		unsorted.add(beginner);
		assertTrue(unsorted.size() % 2 == 1);
		Round firstRound = engine.pairGroupOfPlayersFirstRound(0, unsorted);
		List<Pairing> pairings = firstRound.pairings;
		assertTrue(pairings.size() == 3);

		Iterator<Pairing> iterator = pairings.iterator();
		testPairingExistsOfPlayers(iterator.next(), highRated, classCRated);
		testPairingExistsOfPlayers(iterator.next(), classARated, classDRated);
		testPairingExistsOfPlayers(iterator.next(), classBRated, lowRated);
		assertEquals(firstRound.bye, beginner);
	}

	private void testPairingExistsOfPlayers(Pairing pairing, Player p1, Player p2) {
		assertTrue(pairing.existsOfPlayers(p1, p2));
	}

	@Test
	public void testPairWithOffset0() {
		SwissEngine engine = new SwissEngine();
		LinkedList<Player> players = createLinkedListWithABCDRatedPlayers();
		List<Pairing> pairings = engine.pair(players, 0, 0);
		assertTrue(pairings.size() == 2);
		Pairing p1 = pairings.get(0);
		Pairing p2 = pairings.get(1);
		testPairingExistsOfPlayers(p1, classARated, classDRated);
		testPairingExistsOfPlayers(p2, classBRated, classCRated);
	}

	@Test
	public void testPairWithOffset1Front() {
		SwissEngine engine = new SwissEngine();
		LinkedList<Player> players = createLinkedListWithABCDRatedPlayers();
		List<Pairing> pairings = engine.pair(players, 1, 0);
		assertTrue(pairings.size() == 2);

		Pairing p1 = pairings.get(0);
		Pairing p2 = pairings.get(1);
		testPairingExistsOfPlayers(p1, classBRated, classDRated);
		testPairingExistsOfPlayers(p2, classARated, classCRated);
	}

	@Test
	public void testPairWithOffset2Front() {
		SwissEngine engine = new SwissEngine();
		LinkedList<Player> players = createLinkedListWithABCDRatedPlayers();
		List<Pairing> pairings = engine.pair(players, 2, 0);
		assertTrue(pairings.size() == 2);

		Pairing p1 = pairings.get(0);
		Pairing p2 = pairings.get(1);
		testPairingExistsOfPlayers(p1, classCRated, classDRated);
		testPairingExistsOfPlayers(p2, classARated, classBRated);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPairWithOffset3Front() {
		SwissEngine engine = new SwissEngine();
		LinkedList<Player> players = createLinkedListWithABCDRatedPlayers();
		engine.pair(players, 3, 0);
	}

	@Test
	public void testPairWithOffset1Back() {
		SwissEngine engine = new SwissEngine();
		LinkedList<Player> players = createLinkedListWithABCDRatedPlayers();
		List<Pairing> pairings = engine.pair(players, 0, 1);
		assertTrue(pairings.size() == 2);

		Pairing p1 = pairings.get(0);
		Pairing p2 = pairings.get(1);
		testPairingExistsOfPlayers(p1, classARated, classCRated);
		testPairingExistsOfPlayers(p2, classBRated, classDRated);
	}

	@Test
	public void testPairWithOffset2Back() {
		SwissEngine engine = new SwissEngine();
		LinkedList<Player> players = createLinkedListWithABCDRatedPlayers();
		List<Pairing> pairings = engine.pair(players, 0, 2);
		assertTrue(pairings.size() == 2);

		Pairing p1 = pairings.get(0);
		Pairing p2 = pairings.get(1);
		testPairingExistsOfPlayers(p1, classARated, classBRated);
		testPairingExistsOfPlayers(p2, classCRated, classDRated);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPairWithOffset3Back() {
		SwissEngine engine = new SwissEngine();
		LinkedList<Player> players = createLinkedListWithABCDRatedPlayers();
		engine.pair(players, 0, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOffSetsThatCrossEachOtherGenerateException() {
		SwissEngine engine = new SwissEngine();
		LinkedList<Player> players = createLinkedListWithABCDRatedPlayers();
		engine.pair(players, 2, 2);
	}

	private LinkedList<Player> createLinkedListWithABCDRatedPlayers() {
		LinkedList<Player> players = new LinkedList<>();
		players.add(classARated);
		players.add(classBRated);
		players.add(classCRated);
		players.add(classDRated);
		return players;
	}

	@Test
	public void testPair3Rounds() {
		SwissEngine engine = new SwissEngine();
		Set<Player> players = createPlayerSet();

		Set<Pairing> allPairings = new HashSet<>();
		Round firstRound = engine.pairNextRound(players);
		List<Pairing> pairings = firstRound.pairings;
		allPairings.addAll(pairings);

		Round secondRound = engine.pairNextRound(players);
		pairings = secondRound.pairings;
		addPairingsToCacheAndCheckForUnicity(pairings, allPairings);
		Round thirdRound = engine.pairNextRound(players);
		pairings = thirdRound.pairings;
		addPairingsToCacheAndCheckForUnicity(pairings, allPairings);
	}

	private void addPairingsToCacheAndCheckForUnicity(List<Pairing> pairings, Set<Pairing> allPairings) {
		for (Pairing p : pairings) {
			boolean succes = allPairings.add(p);
			assertTrue("Pairing already happended!:" + p, succes);
		}
	}

	@Test
	public void testPlayersDidAlreadyPlayEachOtherWithEmptyCache() {
		SwissEngine engine = new SwissEngine();
		Player white = classARated;
		Player black = classBRated;
		PairingImpl pairing = PairingImpl.createPairing(white, black);
		assertFalse(engine.pairingAlreadyOccured(pairing));

	}

	@Test
	public void testPlayersDidAlreadyPlayEachOtherWithThePairingInCache() {
		SwissEngine engine = new SwissEngine();
		Player white = classARated;
		Player black = classBRated;
		Pairing pairing = PairingImpl.createPairing(white, black);
		List<Pairing> pairings = new ArrayList<Pairing>();
		pairings.add(pairing);
		Round round = Round.createRound(0, pairings);
		engine.addPairingsToCacheAndInitializeTheCacheIfNecessary(round);
		assertTrue(engine.pairingAlreadyOccured(pairing));
	}

	@Test
	public void testOffsetsValid() {
		SwissEngine engine = new SwissEngine();
		LinkedList<Player> players = createLinkedListWithABCDRatedPlayers();
		assertTrue(engine.offsetsValid(players, 0, 0));
		assertTrue(engine.offsetsValid(players, 2, 0));// 3th position
		assertTrue(engine.offsetsValid(players, 0, 2));
		assertFalse(engine.offsetsValid(players, 3, 0));// 4th position
		assertFalse(engine.offsetsValid(players, 0, 3));
	}

	@Test
	public void testSumOfOffsetsMustNotEndOnSameElement() {
		SwissEngine engine = new SwissEngine();
		LinkedList<Player> players = createLinkedListWithABCDRatedPlayers();
		// location is 0 based, so 2+1 = 3 and 1+1=2 , so thats 5 already in a 4
		// sized list
		assertFalse(engine.offsetsValid(players, 2, 1));
		assertTrue(engine.offsetsValid(players, 1, 1));
		assertFalse(engine.offsetsValid(players, 1, 2));
	}

	// This is what we aim for. From the site of the FIDE
	// http://www.fide.com/component/handbook/?id=86&view=article
	// 1 1*6 2*5 3*4
	// 2 1*6 2*4 3*5
	// 3 1*6 2*3 4*5
	// 4 1*5 2*6 3*4
	// 5 1*5 2*4 3*6
	// 6 1*5 2*3 4*6
	// 7 1*4 2*6 3*5
	// 8 1*4 2*5 3*6
	// 9 1*4 2*3 5*6
	// 10 1*3 2*6 4*5
	// 11 1*3 2*5 4*6
	// 12 1*3 2*4 5*6
	// 13 1*2 3*6 4*5
	// 14 1*2 3*5 4*6
	// 15 1*2 3*4 5*6
	@Test
	public void testGenerateAllPairingsFor6PlayersMustBeInRightOrder() {
		SwissEngine engine = new SwissEngine();
		Set<Player> players = new TreeSet<>(ComparatorFactory.getPlayerComparatorLowToHigh());
		List<Player> playerCache = new ArrayList<>();
		for (int i = 1; i < 7; i++) {
			Player player = PlayerUtils.createPlayer("" + i, "", 1500 - i);
			players.add(player);
			playerCache.add(player);
		}

		final Player p1 = playerCache.get(0);
		final Player p2 = playerCache.get(1);
		final Player p3 = playerCache.get(2);
		final Player p4 = playerCache.get(3);
		final Player p5 = playerCache.get(4);
		final Player p6 = playerCache.get(5);
		// Round round = engine.pairGroupOfPlayers(1, players);
		Round round = engine.pairGroupOfPlayersWithByePlayer(1, players, null);
		// 1*6 2*5 3*4
		assertTrue(roundHasPlayers(p1, p6, round));
		assertTrue(roundHasPlayers(p2, p5, round));
		assertTrue(roundHasPlayers(p3, p4, round));

		// 2 1*6 2*4 3*5
		Pairing p = PairingImpl.createPairing(p2, p5);
		engine.addPairingToPairingsCacheForTesting(p);
		round = engine.pairGroupOfPlayers(1, players);
		assertTrue(roundHasPlayers(p1, p6, round));
		assertTrue(roundHasPlayers(p2, p4, round));
		assertTrue(roundHasPlayers(p3, p5, round));

		createAndAddPairingToCache(engine, p2, p4);
		round = engine.pairGroupOfPlayers(1, players);
		assertTrue(roundHasPlayers(p1, p6, round));
		assertTrue(roundHasPlayers(p2, p3, round));
		assertTrue(roundHasPlayers(p4, p5, round));

		// clear cache and restart sub pairing
		engine.clearPairingCacheForTesting();
		createAndAddPairingToCache(engine, p1, p6);

		// 4 1*5 2*6 3*4
		round = engine.pairGroupOfPlayers(1, players);
		assertTrue(roundHasPlayers(p1, p5, round));
		assertTrue(roundHasPlayers(p2, p6, round));
		assertTrue(roundHasPlayers(p3, p4, round));
		createAndAddPairingToCache(engine, p2, p6);
		round = engine.pairGroupOfPlayers(1, players);
		assertTrue(roundHasPlayers(p1, p5, round));
		assertTrue(roundHasPlayers(p2, p4, round));
		assertTrue(roundHasPlayers(p3, p6, round));
		createAndAddPairingToCache(engine, p2, p4);
		round = engine.pairGroupOfPlayers(1, players);
		assertTrue(roundHasPlayers(p1, p5, round));
		assertTrue(roundHasPlayers(p2, p3, round));
		assertTrue(roundHasPlayers(p4, p6, round));

		// clear cache and restart sub pairing
		engine.clearPairingCacheForTesting();
		createAndAddPairingToCache(engine, p1, p6);
		createAndAddPairingToCache(engine, p1, p5);

		// 7 1*4 2*6 3*5
		round = engine.pairGroupOfPlayers(1, players);
		assertTrue(roundHasPlayers(p1, p4, round));
		assertTrue(roundHasPlayers(p2, p6, round));
		assertTrue(roundHasPlayers(p3, p5, round));
		createAndAddPairingToCache(engine, p2, p6);
		round = engine.pairGroupOfPlayers(1, players);
		assertTrue(roundHasPlayers(p1, p4, round));
		assertTrue(roundHasPlayers(p2, p5, round));
		assertTrue(roundHasPlayers(p3, p6, round));
		createAndAddPairingToCache(engine, p2, p5);
		round = engine.pairGroupOfPlayers(1, players);
		assertTrue(roundHasPlayers(p1, p4, round));
		assertTrue(roundHasPlayers(p2, p3, round));
		assertTrue(roundHasPlayers(p5, p6, round));

		// clear cache and restart sub pairing
		engine.clearPairingCacheForTesting();
		createAndAddPairingToCache(engine, p1, p6);
		createAndAddPairingToCache(engine, p1, p5);
		createAndAddPairingToCache(engine, p1, p4);

		// 10 1*3 2*6 4*5
		round = engine.pairGroupOfPlayers(1, players);
		assertTrue(roundHasPlayers(p1, p3, round));
		assertTrue(roundHasPlayers(p2, p6, round));
		assertTrue(roundHasPlayers(p4, p5, round));
		createAndAddPairingToCache(engine, p2, p6);
		round = engine.pairGroupOfPlayers(1, players);
		assertTrue(roundHasPlayers(p1, p3, round));
		assertTrue(roundHasPlayers(p2, p5, round));
		assertTrue(roundHasPlayers(p4, p6, round));
		createAndAddPairingToCache(engine, p2, p5);
		round = engine.pairGroupOfPlayers(1, players);
		assertTrue(roundHasPlayers(p1, p3, round));
		assertTrue(roundHasPlayers(p2, p4, round));
		assertTrue(roundHasPlayers(p5, p6, round));

		// clear cache and restart sub pairing
		engine.clearPairingCacheForTesting();
		createAndAddPairingToCache(engine, p1, p6);
		createAndAddPairingToCache(engine, p1, p5);
		createAndAddPairingToCache(engine, p1, p4);
		createAndAddPairingToCache(engine, p1, p3);

		// 13 1*2 3*6 4*5
		round = engine.pairGroupOfPlayers(1, players);
		assertTrue(roundHasPlayers(p1, p2, round));
		assertTrue(roundHasPlayers(p3, p6, round));
		assertTrue(roundHasPlayers(p4, p5, round));
		// 14 1*2 3*5 4*6
		createAndAddPairingToCache(engine, p3, p6);
		round = engine.pairGroupOfPlayers(1, players);
		assertTrue(roundHasPlayers(p1, p2, round));
		assertTrue(roundHasPlayers(p3, p5, round));
		assertTrue(roundHasPlayers(p4, p6, round));
		// 15 1*2 3*4 5*6
		createAndAddPairingToCache(engine, p3, p5);
		round = engine.pairGroupOfPlayers(1, players);
		assertTrue(roundHasPlayers(p1, p2, round));
		assertTrue(roundHasPlayers(p3, p4, round));
		assertTrue(roundHasPlayers(p5, p6, round));
	}

	private void createAndAddPairingToCache(SwissEngine engine, final Player p1, final Player p5) {
		Pairing p;
		p = PairingImpl.createPairing(p1, p5);
		engine.addPairingToPairingsCacheForTesting(p);
	}

	private boolean roundHasPlayers(Player p1, Player p2, Round round) {
		Pairing pairingToSearch = PairingImpl.createPairing(p1, p2);
		return round.pairings.contains(pairingToSearch);
	}

	@Test
	public void testGetLowestPlayerWithoutBye() {
		SwissEngine engine = new SwissEngine();
		Set<Player> fromLowToHigh = new TreeSet<>(ComparatorFactory.getPlayerComparatorLowToHigh());
		Player p1 = PlayerUtils.createPlayer("1", "", 1000);
		Player p2 = PlayerUtils.createPlayer("1", "", 1001);
		Player p3 = PlayerUtils.createPlayer("1", "", 1002);
		fromLowToHigh.add(p1);
		assertEquals(p1, engine.getLowestPlayerWithoutBye(fromLowToHigh, 0));
		fromLowToHigh.add(p2);
		assertEquals(p1, engine.getLowestPlayerWithoutBye(fromLowToHigh, 0));
		assertEquals(p2, engine.getLowestPlayerWithoutBye(fromLowToHigh, 1));
		fromLowToHigh.add(p3);
		assertEquals(p1, engine.getLowestPlayerWithoutBye(fromLowToHigh, 0));
		assertEquals(p2, engine.getLowestPlayerWithoutBye(fromLowToHigh, 1));
		assertEquals(p3, engine.getLowestPlayerWithoutBye(fromLowToHigh, 2));
		p1.setBye();
		assertEquals(p2, engine.getLowestPlayerWithoutBye(fromLowToHigh, 0));
		p2.setBye();
		assertEquals(p3, engine.getLowestPlayerWithoutBye(fromLowToHigh, 0));
	}

	@Test
	public void testgetAllPossiblePairings2Players() {
		SwissEngine engine = new SwissEngine();
		LinkedList<ScoreGroup> fromHighToLow = new LinkedList<>();
		List<Player> players = new ArrayList<>();
		players.add(beginner);
		players.add(classARated);
		ScoreGroup sg = ScoreGroup.createScoreGroup(players, 0);
		fromHighToLow.add(sg);
		List<PairingNode> node = engine.getAllPossiblePairings(fromHighToLow);
		assertEquals(node.size(), 1);
		List<Pairing> pairings = node.get(0).getPairingsFromThisNodeAndUp();
		assertEquals(pairings.size(), 1);
	}

	@Test
	public void testgetAllPossiblePairings3Players() {
		SwissEngine engine = new SwissEngine();
		LinkedList<ScoreGroup> fromHighToLow = new LinkedList<>();
		List<Player> players = new ArrayList<>();
		players.add(beginner);
		players.add(classARated);
		players.add(classBRated);
		ScoreGroup sg = ScoreGroup.createScoreGroup(players, 0);
		fromHighToLow.add(sg);
		List<PairingNode> node = engine.getAllPossiblePairings(fromHighToLow);
		assertEquals(node.size(), 3);
		List<Pairing> pairings = node.get(0).getPairingsFromThisNodeAndUp();
		assertEquals(pairings.size(), 1);
	}

	@Test
	public void testAddAllPossiblePairingsToScoreGroup() {
		SwissEngine engine = new SwissEngine();
		List<Player> players = new ArrayList<>();
		players.add(beginner);
		players.add(classARated);
		players.add(classBRated);
		ScoreGroup scoreGroup = ScoreGroup.createScoreGroup(players, 0);
		Collection<Player> unpairedPlayerFromAPreviousScoreGroup = new ArrayList<>();
		ScoreGroup result = engine.addPossiblePairingsForSingleScoregroupToScoreGroup(scoreGroup,
				unpairedPlayerFromAPreviousScoreGroup);
		assertTrue(result.hasPossiblePairings());
		assertEquals(3, result.numberOfPossiblePairings());

		Round round = result.popNextPossiblePairingRoundToTry();
		assertTrue(roundHasPlayers(beginner, classARated, round));
		assertEquals(classBRated, round.bye);
		round = result.popNextPossiblePairingRoundToTry();
		assertTrue(roundHasPlayers(classBRated, classARated, round));
		assertEquals(beginner, round.bye);
		round = result.popNextPossiblePairingRoundToTry();
		assertTrue(roundHasPlayers(beginner, classBRated, round));
		assertEquals(classARated, round.bye);
	}

	@Test
	public void testOverlap() {

		LinkedList<Player> playersToPair = new LinkedList<>();
		playersToPair.addAll(createPlayerSet());
		int size = playersToPair.size();
		assertTrue(size == 6);
		assertOverlapSucces(0, 0, playersToPair);
		assertOverlapSucces(0, 1, playersToPair);
		assertOverlapSucces(0, 2, playersToPair);
		assertOverlapSucces(0, 3, playersToPair);
		assertOverlapSucces(0, 4, playersToPair);
		assertOverlapFails(0, 5, playersToPair);

		assertOverlapSucces(1, 0, playersToPair);
		assertOverlapSucces(2, 0, playersToPair);
		assertOverlapSucces(3, 0, playersToPair);
		assertOverlapSucces(4, 0, playersToPair);
		assertOverlapFails(5, 0, playersToPair);

		assertOverlapFails(4, 1, playersToPair);
		assertOverlapFails(3, 2, playersToPair);
		assertOverlapFails(2, 3, playersToPair);
		assertOverlapFails(1, 4, playersToPair);

		assertOverlapSucces(3, 1, playersToPair);
		assertOverlapSucces(2, 2, playersToPair);
		assertOverlapSucces(3, 1, playersToPair);
	}

	private void assertOverlapSucces(int i, int j, LinkedList<Player> playersToPair) {
		SwissEngine engine = new SwissEngine();
		assertTrue(engine.noOverlap(i, j, playersToPair));
	}

	private void assertOverlapFails(int i, int j, LinkedList<Player> playersToPair) {
		SwissEngine engine = new SwissEngine();
		assertFalse(engine.noOverlap(i, j, playersToPair));
	}

	@Test
	public void testPairScoreGroupRecursive() {
		SwissEngine engine = new SwissEngine();
		List<PairingNode> endNodes = new ArrayList<>();
		LinkedList<ScoreGroup> scoregroupsfromHighToLow = new LinkedList<>();
		scoregroupsfromHighToLow.add(createSinglePlayerScoreGroup(classARated, 40));
		scoregroupsfromHighToLow.add(createSinglePlayerScoreGroup(classBRated, 30));
		scoregroupsfromHighToLow.add(createSinglePlayerScoreGroup(classCRated, 20));
		scoregroupsfromHighToLow.add(createSinglePlayerScoreGroup(classDRated, 10));
		engine.pairScoreGroupRecursive(scoregroupsfromHighToLow, new HashSet<Player>(), endNodes);

		assertTrue(endNodes.size() >= 3);
	}

	@Test
	public void testGenerateAllPossibleCombinationsFromHighToLowerImportanceWith3PlayersHas3Results() {
		List<LinkedList<ScoreGroup>> result = getGeneratedScoreGroupsWithABCPlayers();
		assertEquals(3, result.size());
		testGenerateAllPossibleCombinationsFromHighToLowerImportanceWith3PlayersContains3ScoreGroupsOfOneItem();
		testGenerateAllPossibleCombinationsFromHighToLowerImportanceWith3PlayersContains2ScoreGroups();
		testGenerateAllPossibleCombinationsFromHighToLowerImportanceWith3PlayersContains1ScoreGroupWithAllThreeItems();

	}

	private void testGenerateAllPossibleCombinationsFromHighToLowerImportanceWith3PlayersContains1ScoreGroupWithAllThreeItems() {
		List<LinkedList<ScoreGroup>> result = getGeneratedScoreGroupsWithABCPlayers();
		LinkedList<ScoreGroup> group = result.get(2);
		assertEquals(1, group.size());

		ScoreGroup firstGroup = group.get(0);

		assertEquals(3, firstGroup.getPlayers().size());

		assertEquals(classARated, firstGroup.getPlayers().get(0));
		assertEquals(classBRated, firstGroup.getPlayers().get(1));
		assertEquals(classCRated, firstGroup.getPlayers().get(2));

	}

	private void testGenerateAllPossibleCombinationsFromHighToLowerImportanceWith3PlayersContains2ScoreGroups() {
		List<LinkedList<ScoreGroup>> result = getGeneratedScoreGroupsWithABCPlayers();
		LinkedList<ScoreGroup> group = result.get(1);
		assertEquals(2, group.size());

		ScoreGroup firstGroup = group.get(0);
		ScoreGroup secondGroup = group.get(1);

		assertEquals(2, firstGroup.getPlayers().size());
		assertEquals(1, secondGroup.getPlayers().size());

		assertEquals(classARated, firstGroup.getPlayers().get(0));
		assertEquals(classBRated, firstGroup.getPlayers().get(1));
		assertEquals(classCRated, secondGroup.getPlayers().get(0));

	}

	private List<LinkedList<ScoreGroup>> getGeneratedScoreGroupsWithABCPlayers() {
		SwissEngine engine = new SwissEngine();
		LinkedList<ScoreGroup> fromHighToLow = new LinkedList<>();
		fromHighToLow.add(createSinglePlayerScoreGroup(classARated, 40));
		fromHighToLow.add(createSinglePlayerScoreGroup(classBRated, 30));
		fromHighToLow.add(createSinglePlayerScoreGroup(classCRated, 20));
		List<LinkedList<ScoreGroup>> result = engine.generateAllPossibleCombinationsFromHighToLowerImportance(fromHighToLow);
		return result;
	}

	private void testGenerateAllPossibleCombinationsFromHighToLowerImportanceWith3PlayersContains3ScoreGroupsOfOneItem() {
		List<LinkedList<ScoreGroup>> result = getGeneratedScoreGroupsWithABCPlayers();
		LinkedList<ScoreGroup> group = result.get(0);
		assertEquals(3, group.size());

		ScoreGroup firstGroup = group.get(0);
		ScoreGroup secondGroup = group.get(1);
		ScoreGroup thirdGroup = group.get(2);

		assertTrue(firstGroup.hasOnlyOnePlayer());
		assertTrue(secondGroup.hasOnlyOnePlayer());
		assertTrue(thirdGroup.hasOnlyOnePlayer());

		assertEquals(classARated, firstGroup.getPlayers().get(0));
		assertEquals(classBRated, secondGroup.getPlayers().get(0));
		assertEquals(classCRated, thirdGroup.getPlayers().get(0));
	}

	private ScoreGroup createSinglePlayerScoreGroup(Player player, int points) {
		List<Player> players = new ArrayList<>();
		players.add(player);
		ScoreGroup sc = ScoreGroup.createScoreGroup(players, points);
		return sc;
	}
}
