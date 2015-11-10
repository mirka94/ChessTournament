package be.md.swiss.pairing;

import java.util.Comparator;

import be.md.swiss.Player;
import be.md.swiss.PlayerComparatorBySonnenbornBerger;

public class ComparatorFactory {

	public static Comparator<Player> getPlayerComparatorHighToLow() {
		return PlayerComparatorBySonnenbornBerger.createFromHighToLowPointsSonnebornRating();
	}

	public static Comparator<Integer> getHighToLowComparator() {
		return new HightToLowComparator();
	}

	public static Comparator<? super Player> getPlayerComparatorLowToHigh() {
		return PlayerComparatorBySonnenbornBerger.createFromLowToHighPointsSonnebornRating();
	}

	public static Comparator<PairingNode> getPairingNodeComparator() {
		return new PairingNodeComparator();
	}

}
