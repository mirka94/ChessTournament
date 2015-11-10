package be.md.swiss;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestColorPreference {

	@Test(expected = IllegalStateException.class)
	public void testYouCanNotBeWhiteIFYouMustBeBlack() {
		ColorPreference colorPreference = ColorPreference.MUST_BE_BLACK;
		colorPreference = colorPreference.getNewStatusIfPlayerIsNowWhite();
	}

	@Test(expected = IllegalStateException.class)
	public void testYouCanNotBeWhiteIFYouMustBeWhite() {
		ColorPreference colorPreference = ColorPreference.MUST_BE_WHITE;
		colorPreference = colorPreference.getNewStatusIfPlayerIsNowBlack();
	}

	@Test
	public void testPreferenceChangesFromBlackToWhite() {
		ColorPreference colorPreference = ColorPreference.MUST_BE_BLACK;
		colorPreference = colorPreference.getNewStatusIfPlayerIsNowBlack();
		assertEquals(ColorPreference.PREFERS_BLACK, colorPreference);

		colorPreference = colorPreference.getNewStatusIfPlayerIsNowBlack();
		assertEquals(ColorPreference.NO_PREFERENCE, colorPreference);

		colorPreference = colorPreference.getNewStatusIfPlayerIsNowBlack();
		assertEquals(ColorPreference.PREFERS_WHITE, colorPreference);

		colorPreference = colorPreference.getNewStatusIfPlayerIsNowBlack();
		assertEquals(ColorPreference.MUST_BE_WHITE, colorPreference);
	}

	@Test
	public void testPreferenceChangesFromWhiteToBlack() {
		ColorPreference colorPreference = ColorPreference.MUST_BE_WHITE;
		colorPreference = colorPreference.getNewStatusIfPlayerIsNowWhite();
		assertEquals(ColorPreference.PREFERS_WHITE, colorPreference);

		colorPreference = colorPreference.getNewStatusIfPlayerIsNowWhite();
		assertEquals(ColorPreference.NO_PREFERENCE, colorPreference);

		colorPreference = colorPreference.getNewStatusIfPlayerIsNowWhite();
		assertEquals(ColorPreference.PREFERS_BLACK, colorPreference);

		colorPreference = colorPreference.getNewStatusIfPlayerIsNowWhite();
		assertEquals(ColorPreference.MUST_BE_BLACK, colorPreference);
	}
}
