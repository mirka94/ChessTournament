package be.md.swiss.pairing;

import java.util.Comparator;

public class HightToLowComparator implements Comparator<Integer> {

	@Override
	public int compare(Integer first, Integer second) {
		return second - first;
	}

}
