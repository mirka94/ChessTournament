package be.md.swiss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Test;

import be.md.swiss.exceptions.NotEnoughPlayersException;

public class TestTournamentPairing {

	@Test(expected = NotEnoughPlayersException.class)
	public void testPairingWith2PlayersThrowsExceptionIfMoreThen2Rounds() {
		Tournament tournament = Tournament.createTournament(3);
		tournament.addPlayer(PlayerUtils.createPlayerJohn());
		tournament.addPlayer(PlayerUtils.createPlayerJoanna());
		tournament.pairNextRound();
	}

	@Test
	public void testPairingWith3PlayersWith2RoundsWorks() {
		Tournament tournament = Tournament.createTournament(2);
		tournament.addPlayer(PlayerUtils.createPlayerJohn());
		tournament.addPlayer(PlayerUtils.createPlayerJoanna());
		tournament.addPlayer(PlayerUtils.createPlayer("paulus", "dbk"));
		Collection<Pairing> pairings = tournament.pairNextRound().pairings;
		assertEquals(1, pairings.size());
		Pairing pairing = pairings.iterator().next();
		assertNotNull(pairing.getBlack());
		assertNotNull(pairing.getWhite());
	}

}
