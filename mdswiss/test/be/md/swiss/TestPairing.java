package be.md.swiss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestPairing {

	private Player white = PlayerUtils.createPlayerJohn();
	private Player black = PlayerUtils.createPlayerJoanna();

	@Test
	public void testPairingCreation() {
		PairingImpl pairing = PairingImpl.createPairing(white, black);
		assertEquals(white, pairing.getWhite());
		assertEquals(black, pairing.getBlack());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIfPlayersAreEqualThrowException() {
		PairingImpl.createPairing(white, white);
	}

	/*
	 * @Test public void testColorScore() { assertEquals(0,
	 * PairingImpl.colorScore(white, black)); white.isWhite(); assertEquals(6,
	 * PairingImpl.colorScore(white, black)); assertEquals(0,
	 * PairingImpl.colorScore(black, white)); white.isWhite(); assertEquals(6,
	 * PairingImpl.colorScore(white, black)); assertEquals(0,
	 * PairingImpl.colorScore(black, white)); black.isBlack(); assertEquals(12,
	 * PairingImpl.colorScore(white, black)); assertEquals(0,
	 * PairingImpl.colorScore(black, white)); black.isBlack(); assertEquals(12,
	 * PairingImpl.colorScore(white, black)); assertEquals(0,
	 * PairingImpl.colorScore(black, white)); }
	 */

	@Test
	public void testCreatePairingSwitchesColorsForBestScore() {
		white.isWhite();
		white.isWhite();
		black.isBlack();
		black.isBlack();
		Pairing pairing = PairingImpl.createPairing(white, black);
		assertEquals(white, pairing.getBlack());
		assertEquals(black, pairing.getWhite());

		assertFalse(pairing.hasColorConflicts());
	}

	@Test
	public void testColorConflicts() {
		Player p1 = PlayerUtils.createPlayerJoanna();
		Player p2 = PlayerUtils.createPlayerJohn();
		p1.isBlack();
		p1.isBlack();
		assertTrue(ColorPreference.MUST_BE_WHITE.getScore() == p1.getColorScore());

		Pairing pairing = PairingImpl.createPairing(p1, p2);
		assertEquals(pairing.getWhite(), p1);
		pairing = PairingImpl.createPairing(p2, p1);
		assertEquals(pairing.getWhite(), p1);

		p2.isBlack();
		p2.isBlack();
		assertTrue(ColorPreference.MUST_BE_WHITE.getScore() == p2.getColorScore());
		pairing = PairingImpl.createPairing(p1, p2);
		assertTrue(pairing.hasColorConflicts());
	}

	@Test
	public void testEquals() {
		PairingImpl p1 = PairingImpl.createPairing(white, black);
		PairingImpl p2 = PairingImpl.createPairing(black, white);
		assertEquals(p1, p2);

		white.wasJustWhite();
		black.wasJustBlack();
		assertEquals(p1, p2);
		white.addDraw();
		black.addWin();
		assertEquals(p1, p2);
		black.addSonnebornBerner(10);
		assertEquals(p1, p2);
	}
}
