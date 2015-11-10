package be.md.swiss;

public class PlayerUtils {
	public static Player createPlayer(String firstname, String lastname) {
		return Player.createPlayerWithFirstnameLastname(firstname, lastname);
	}

	public static Player createPlayer() {
		return createPlayer("John", "Doe");
	}

	public static Player createPlayerJohn() {
		return createPlayer("John", "Doe");
	}

	public static Player createPlayerJoanna() {
		return createPlayer("Joanna", "Doe");
	}

	public static Player createRatedPlayer(int rating) {
		Player rated = PlayerUtils.createPlayerJohn();
		rated.setRating(rating);
		return rated;
	}

	public static Player createPlayer(String firstname, String lastname,
			int rating) {
		Player result = Player.createPlayerWithFirstnameLastname(firstname,
				lastname);
		result.setRating(rating);
		return result;
	}
}
