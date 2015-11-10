package be.md.swiss;

public class Player {

	private static final int DEFAULT_RATING = 1200;
	private String firstname;
	private String lastname;
	private int rating;
	private ColorPreference colorPreference = ColorPreference.NO_PREFERENCE;
	int points = 0;
	boolean hasBeenBye = false;
	private float sonnebornBerner;
	private boolean wasJustBlack;
	private boolean wasJustWhite;

	private Player(String firstName, String lastName) {
		this.firstname = firstName;
		this.lastname = lastName;
		this.rating = DEFAULT_RATING;
	}

	public static Player createPlayerWithFirstnameLastname(String firstName, String lastName) {
		Player player = new Player(firstName, lastName);
		return player;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public int getRating() {
		return rating;
	}

	@Override
	public String toString() {
		return "[" + firstname + " " + lastname + " " + rating + " " + points + " " + colorPreference + "]";
	}

	public int getPoints() {
		return points;
	}

	public void addWin() {
		points += 10;
	}

	public void addDraw() {
		points += 5;
	}

	public void setBye() {
		hasBeenBye = true;
		points += 10;
	}

	public void isWhite() {
		colorPreference = colorPreference.getNewStatusIfPlayerIsNowWhite();
		wasJustBlack = false;
		wasJustWhite = true;
	}

	public void isBlack() {
		colorPreference = colorPreference.getNewStatusIfPlayerIsNowBlack();
		wasJustBlack = true;
		wasJustWhite = false;
	}

	public int getColorScore() {
		return colorPreference.getScore();
	}

	public boolean hasBeenBye() {
		return hasBeenBye;
	}

	public float getSonnebornBerner() {
		return sonnebornBerner;
	}

	public void addSonnebornBerner(float i) {
		this.sonnebornBerner += i;

	}

	public boolean wantsToBeWhite() {
		return colorPreference == ColorPreference.MUST_BE_WHITE //
				|| colorPreference == ColorPreference.PREFERS_WHITE //
				|| colorPreference == ColorPreference.NO_PREFERENCE;
	}

	public boolean wantsToBeBlack() {
		return colorPreference == ColorPreference.MUST_BE_BLACK //
				|| colorPreference == ColorPreference.PREFERS_BLACK //
				|| colorPreference == ColorPreference.NO_PREFERENCE;
	}

	boolean wasJustWhite() {
		return wasJustWhite;
	}

	boolean wasJustBlack() {
		return wasJustBlack;
	}

	public int getColorScoreIfYouWouldBeWhite() {
		int score = 0;
		try {
			score = colorPreference.getNewStatusIfPlayerIsNowWhite().getScore();
		} catch (IllegalStateException e) {
			score = ColorPreference.getMaxScore();
		}
		return score;

	}

	public int getColorScoreIfYouWouldBeBlack() {
		int score = 0;
		try {
			score = colorPreference.getNewStatusIfPlayerIsNowBlack().getScore();
		} catch (IllegalStateException e) {
			score = ColorPreference.getMaxScore();
		}
		return score;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firstname == null) ? 0 : firstname.hashCode());
		result = prime * result + ((lastname == null) ? 0 : lastname.hashCode());
		result = prime * result + rating;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (firstname == null) {
			if (other.firstname != null)
				return false;
		} else if (!firstname.equals(other.firstname))
			return false;
		if (lastname == null) {
			if (other.lastname != null)
				return false;
		} else if (!lastname.equals(other.lastname))
			return false;
		if (rating != other.rating)
			return false;
		return true;
	}

}
