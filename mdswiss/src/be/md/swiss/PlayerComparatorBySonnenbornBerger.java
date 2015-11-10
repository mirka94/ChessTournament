package be.md.swiss;

import java.util.Comparator;

public class PlayerComparatorBySonnenbornBerger implements Comparator<Player> {

	private static final int ORDER_DOESNT_MATTER_HERE = 1;
	private boolean fromHighToLow = true;

	private PlayerComparatorBySonnenbornBerger(boolean fromHighToLow) {
		this.fromHighToLow = fromHighToLow;
	}

	@Override
	public int compare(Player player1, Player player2) {
		if (player1 == player2)
			return 0;

		int points1 = player1.getPoints();
		int points2 = player2.getPoints();

		if (points1 != points2) {
			return fromHighToLow ? fromHighToLow(points1, points2) : fromLowToHigh(points1, points2);
		}

		float sb1 = player1.getSonnebornBerner();
		float sb2 = player2.getSonnebornBerner();

		if (sb1 != sb2) {
			return Math.round(fromHighToLow ? fromHighToLow(sb1, sb2) : fromLowToHigh(sb1, sb2));
		}

		int rating1 = player1.getRating();
		int rating2 = player2.getRating();

		if (ratingIsEqual(rating1, rating2))
			return ORDER_DOESNT_MATTER_HERE;

		return fromHighToLow ? fromHighToLow(rating1, rating2) : fromLowToHigh(rating1, rating2);
	}

	private float fromLowToHigh(float float1, float float2) {
		return float1 - float2;
	}

	private float fromHighToLow(float float1, float float2) {
		return float2 - float1;
	}

	private boolean ratingIsEqual(int rating1, int rating2) {
		return rating1 == rating2;
	}

	private int fromLowToHigh(int rating1, int rating2) {

		return rating1 - rating2;
	}

	private int fromHighToLow(int rating1, int rating2) {
		return rating2 - rating1;
	}

	public static Comparator<Player> createFromHighToLowPointsSonnebornRating() {
		return new PlayerComparatorBySonnenbornBerger(true);
	}

	public static Comparator<Player> createFromLowToHighPointsSonnebornRating() {
		return new PlayerComparatorBySonnenbornBerger(false);
	}

}
