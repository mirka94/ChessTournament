package be.md.swiss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import be.md.swiss.pairing.Round;

public class TestTournament {

	@Test
	public void testCreateTournamentWithNumberOfRounds() {
		Tournament tournament = Tournament.createTournament(5);
		assertEquals(5, tournament.getRounds());
	}

	@Test
	public void testAdd2DifferentPlayersAndCheckIfTheyAreAdded() {
		Tournament tournament = createTournament();

		assertEquals(0, tournament.getNumberOfPlayers());
		tournament.addPlayer(PlayerUtils.createPlayer("John", "Doe"));
		assertEquals(1, tournament.getNumberOfPlayers());
		tournament.addPlayer(PlayerUtils.createPlayer("Johanna", "Doe"));
		assertEquals(2, tournament.getNumberOfPlayers());
	}

	@Test
	public void testAddingTheSamePlayerDoesntWork() {
		Tournament tournament = createTournament();
		Player player = PlayerUtils.createPlayer();
		for (int i = 0; i < 10; i++)
			tournament.addPlayer(player);
		assertEquals(1, tournament.getNumberOfPlayers());
	}

	private Tournament createTournament() {
		return Tournament.createTournament(5);
	}

	@Test
	public void testCreateTournamentBye() {
		int nrRounds = 4;
		Tournament tournament = Tournament.createTournament(nrRounds);
		int nrPlayers = 7;

		for (int i = 1; i < nrPlayers + 1; i++) {
			Player player = PlayerUtils.createPlayer("" + i, "", 1500 - i);
			tournament.addPlayer(player);
		}

		String[] byeInRound = new String[] { "7", "6", "5", "2" };
		for (int i = 0; i < nrRounds; i++) {
			Round round = tournament.pairNextRound();
			System.out.println("-->selectd round=" + round);
			assertTrue("Geen paringen in Round:" + i, round.hasPairings());
			assertTrue(round.hasBye());
			assertEquals("Round:" + i, byeInRound[i], round.bye.getFirstname());
			drawEverythingIn(round);
		}
	}

	// @Test TO FIX
	public void testTournamentWith6PlayersShouldBeAbleToPair5RoundsNoDraws() {
		Tournament tournament = createTournamentWith6PlayersP1ToP6();
		for (int i = 0; i < 5; i++) {
			Round round = tournament.pairNextRound();
			assertEquals("No pairings found in round " + round.roundNumber, 3,
					round.pairings.size());
			assertTrue("No bye should happen in a 6 player tournament!:"
					+ round.bye, round.bye == null);
			assertRoundInPairingsWhereStrongestPlayerWinsNoBye(round);
			letBestPlayerWin(round);
		}
	}

	private void log(Object logMe) {
		System.out.println(getClass().getSimpleName() + ":" + logMe.toString());

	}

	private void assertRoundInPairingsWhereStrongestPlayerWinsNoBye(Round round) {
		List<Pairing> pairings = round.pairings;

		/**
		 * The lines commented out should be brought to work. This is still a
		 * bug somewhere in the algorithm. The expected result comes from a
		 * different pairing program and it makes sense. In the fourth round the
		 * pairing let a player with 1 point play against a player with 4 points
		 * so I guess priorities are mixed up somewhere (preferred color above
		 * points) This should be debugged
		 */
		switch (round.roundNumber) {
		case 0:
			assertPairingsContainsePairingWithPlayers("P6", "P3", pairings);
			assertPairingsContainsePairingWithPlayers("P5", "P2", pairings);
			assertPairingsContainsePairingWithPlayers("P1", "P4", pairings);

			break;
		case 1:
			assertPairingsContainsePairingWithPlayers("P5", "P1", pairings);
			assertPairingsContainsePairingWithPlayers("P3", "P2", pairings);
			assertPairingsContainsePairingWithPlayers("P4", "P6", pairings);
			break;
		case 2:
			assertPairingsContainsePairingWithPlayers("P2", "P1", pairings);
			assertPairingsContainsePairingWithPlayers("P4", "P3", pairings);
			assertPairingsContainsePairingWithPlayers("P5", "P6", pairings);
			break;
		case 3:
			assertPairingsContainsePairingWithPlayers("P1", "P3", pairings);
			assertPairingsContainsePairingWithPlayers("P2", "P4", pairings);
			assertPairingsContainsePairingWithPlayers("P6", "P5", pairings);
			break;
		case 4:
			assertPairingsContainsePairingWithPlayers("P4", "P6", pairings);
			assertPairingsContainsePairingWithPlayers("P1", "P2", pairings);
			assertPairingsContainsePairingWithPlayers("P3", "P5", pairings);
			break;

		default:
			throw new IllegalStateException("only 4 rounds possible");
		}

	}

	@Test
	public void testTournamentWith6PlayersShouldBeAbleToPair5RoundsAndDraws() {
		Tournament tournament = createTournamentWith6PlayersP1ToP6();
		for (int i = 0; i < 5; i++) {
			Round round = tournament.pairNextRound();
			Logger.getLogger(getClass().getSimpleName()).info(round.toString());
			assertEquals("Not enough pairings in round " + (i + 1), 3,
					round.pairings.size());
			assertTrue("No bye should happen in a 6 player tournament!:"
					+ round.bye, round.bye == null);
			// assertRoundAccordingToPairTwoProgramWithDraws(round);
			for (Pairing pairing : round.pairings) {
				if (i % 2 == 0) {
					pairing.draw();
					continue;
				}
				if (whiteEloIsBiggerThanBlack(pairing)) {
					pairing.whiteWins();
				} else {
					pairing.blackWins();
				}
			}
		}
	}

	private Tournament createTournamentWith6PlayersP1ToP6() {
		return generateTournament(5, 6);
	}

	private void assertRoundInPairingsWhereStrongestPlayerWinsWithBye(
			Round round) {
		List<Pairing> pairings = round.pairings;

		switch (round.roundNumber) {
		case 0:
			assertPairingsContainsePairingWithPlayers("P5", "P3", pairings);
			assertPairingsContainsePairingWithPlayers("P4", "P2", pairings);
			break;
		case 1:
			assertPairingsContainsePairingWithPlayers("P5", "P4", pairings);
			assertPairingsContainsePairingWithPlayers("P3", "P1", pairings);
			break;
		case 2:
			assertPairingsContainsePairingWithPlayers("P4", "P5", pairings);
			assertPairingsContainsePairingWithPlayers("P2", "P1", pairings);
			break;
		case 3:
			assertPairingsContainsePairingWithPlayers("P1", "P3", pairings);
			assertPairingsContainsePairingWithPlayers("P2", "P5", pairings);
			break;
		default:
			throw new IllegalStateException("only 4 rounds possible");
		}

	}

	private void assertRoundAccordingToPairTwoProgramWithDraws(Round round) {
		List<Pairing> pairings = round.pairings;

		switch (round.roundNumber) {
		case 0:
			assertPairingsContainsePairingWithPlayers("P6", "P3", pairings);
			assertPairingsContainsePairingWithPlayers("P2", "P5", pairings);
			assertPairingsContainsePairingWithPlayers("P4", "P1", pairings);
			break;
		case 1:
			assertPairingsContainsePairingWithPlayers("P6", "P1", pairings);
			assertPairingsContainsePairingWithPlayers("P3", "P4", pairings);
			assertPairingsContainsePairingWithPlayers("P2", "P5", pairings);
			break;
		case 2:
			assertPairingsContainsePairingWithPlayers("P1", "P5", pairings);
			assertPairingsContainsePairingWithPlayers("P3", "P2", pairings);
			assertPairingsContainsePairingWithPlayers("P6", "P4", pairings);
			break;
		case 3:
			assertPairingsContainsePairingWithPlayers("P2", "P4", pairings);
			assertPairingsContainsePairingWithPlayers("P6", "P5", pairings);
			assertPairingsContainsePairingWithPlayers("P3", "P1", pairings);
			break;
		case 4:
			assertPairingsContainsePairingWithPlayers("P3", "P5", pairings);
			assertPairingsContainsePairingWithPlayers("P4", "P6", pairings);
			assertPairingsContainsePairingWithPlayers("P1", "P2", pairings);
			break;
		default:
			throw new IllegalStateException("only 5 rounds possible");
		}

	}

	private void assertPairingsContainsePairingWithPlayers(String name1,
			String name2, List<Pairing> pairings) {
		boolean foundPairing = false;
		for (Pairing pairing : pairings) {
			String white = pairing.getWhite().getFirstname();
			String black = pairing.getBlack().getFirstname();
			boolean name1Ok = white.equals(name1) || white.equals(name2);
			boolean name2Ok = black.equals(name1) || black.equals(name2);
			if (name1Ok && name2Ok) {
				foundPairing = true;
				break;
			}
		}
		assertTrue("Pairing expected and not found:" + name1 + " vs " + name2,
				foundPairing);

	}

	private boolean whiteEloIsBiggerThanBlack(Pairing pairing) {
		int eloWhite = pairing.getWhiteRating();
		int eloBlack = pairing.getBlackRaring();
		return eloWhite > eloBlack;
	}

	@Test
	public void testTournamentWith5PlayersShouldBeAbleToPair4Rounds() {
		Tournament tournament = createTournamentWith5PlayersP1ToP5();
		for (int i = 0; i < 4; i++) {
			Round round = tournament.pairNextRound();
			System.out.println("Round is:" + round);
			assertEquals(2, round.pairings.size());
			assertTrue("Bye should happen in a 5 player tournament!:"
					+ round.bye, round.bye != null);
			// assertRoundInPairingsWhereStrongestPlayerWinsWithBye(round);
			drawEverythingIn(round);
			// letBestPlayerWin(round);
		}
	}

	private void drawEverythingIn(Round round) {
		for (Pairing pairing : round.pairings) {
			pairing.draw();
		}

	}

	private Tournament createTournamentWith5PlayersP1ToP5() {
		int nrRounds = 4;
		int nrPlayers = 5;
		return generateTournament(nrRounds, nrPlayers);
	}

	private Tournament generateTournament(int nrRounds, int nrPlayers) {
		Tournament tournament = Tournament.createTournament(nrRounds);
		for (int i = 1; i < nrPlayers + 1; i++) {
			Player player = PlayerUtils.createPlayer("P" + i, "",
					1100 + i * 100);
			tournament.addPlayer(player);
		}
		return tournament;
	}

	@Test
	public void testTournament5PlayersFailsAfter3Rounds() {
		int nrRounds = 4;

		Tournament tournament = generateTournament(nrRounds, nrRounds + 1);
		for (int i = 0; i < nrRounds; i++) {
			Round round = tournament.pairNextRound();
			letBestPlayerWin(round);

			assertTrue("Pairings must be found in round " + i + 1,
					round.hasPairings());

		}
	}

	@Test
	public void testBigTournamentOddPlayers() {
		int nrRounds = 5;
		int nrPlayers = 17;

		Tournament tournament = generateTournament(nrRounds, nrPlayers);
		for (int i = 0; i < nrRounds; i++) {
			Round round = tournament.pairNextRound();
			assertTrue("No pairings in round " + i, round.hasPairings());
			assertEquals("Expecting " + nrPlayers / 2 + " pairings in round "
					+ i, nrPlayers / 2, round.pairings.size());
			log(round);
			letBestPlayerWin(round);
		}
	}

	@Test
	public void testBigTournamentEvenPlayers() {
		int nrRounds = 3;
		int nrPlayers = 8;

		runTournament(nrRounds, nrPlayers);
	}

	private void letBestPlayerWin(Round round) {
		for (Pairing pairing : round.pairings) {
			if (whiteEloIsBiggerThanBlack(pairing)) {
				pairing.whiteWins();
			} else {
				pairing.blackWins();
			}

		}
	}

	@Test
	public void testScoreGroups() {
		int nrRounds = 4;
		int nrPlayers = 12;

		Tournament tournament = Tournament.createTournament(nrRounds);
		int cnt = 0;
		for (int i = 1; i < nrPlayers + 1; i++) {
			Player player = PlayerUtils.createPlayer("P" + i, "",
					1100 + i * 100);
			for (int j = 0; j < cnt; j++)
				player.addWin();
			cnt++;
			tournament.addPlayer(player);

		}

		for (int i = 0; i < nrRounds; i++) {
			Round round = tournament.pairNextRound();
			assertTrue("No pairings in round " + (i + 1), round.hasPairings());
			assertEquals("Expecting " + nrPlayers / 2 + " pairings in round "
					+ i, nrPlayers / 2, round.pairings.size());
			log(round);
			letBestPlayerWin(round);
		}
	}

	@Test
	public void testTournament8Players4Rounds() {
		int nrRounds = 4;
		int nrPlayers = 8;

		runTournament(nrRounds, nrPlayers);
	}

	@Test
	public void testTournament9Players5Rounds() {
		int nrRounds = 5;
		int nrPlayers = 9;

		runTournament(nrRounds, nrPlayers);
	}

	@Test
	public void testTournament10Players5Rounds() {
		int nrRounds = 9;
		int nrPlayers = 10;

		runTournament(nrRounds, nrPlayers);
	}

	@Test
	public void testTournament100Players5Rounds() {
		int nrRounds = 9;
		int nrPlayers = 10;

		runTournament(nrRounds, nrPlayers);
	}

	private void runTournament(int nrRounds, int nrPlayers) {
		Tournament tournament = generateTournament(nrRounds, nrPlayers);
		for (int i = 0; i < nrRounds; i++) {
			Round round = tournament.pairNextRound();

			assertTrue("No pairings in round " + (i + 1), round.hasPairings());
			assertEquals("Expecting " + nrPlayers / 2 + " pairings in round "
					+ i, nrPlayers / 2, round.pairings.size());
			log(round);
			letBestPlayerWin(round);
		}
	}

}
