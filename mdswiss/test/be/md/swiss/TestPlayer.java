package be.md.swiss;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestPlayer {

	@Test
	public void testCreatePlayer() {
		String firstname = "John";
		String lastname = "Doe";
		int rating = 1500;
		Player player = Player.createPlayerWithFirstnameLastname(firstname,
				lastname);
		player.setRating(rating);

		assertEquals(firstname, player.getFirstname());
		assertEquals(lastname, player.getLastname());
		assertEquals(rating, player.getRating());
	}

}
