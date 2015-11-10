package be.md.swiss.pairing;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class TestHighToLowComparator {

	private final int MAX = 1000;

	@Test
	public void testHightToLow() {
		Integer[] toSort = { 10, 50, 9, 60, 8, 7, 6, 70, 60, 12, 33, 51, MAX };
		Set<Integer> sorted = new TreeSet<Integer>(ComparatorFactory.getHighToLowComparator());
		sorted.addAll(Arrays.asList(toSort));
		assertNumbersAreSortedHighToLow(sorted);
	}

	private void assertNumbersAreSortedHighToLow(Set<Integer> sorted) {
		int previous = MAX;
		for (int i : sorted) {
			assertTrue(i <= previous);
			previous = i;
		}
	}

}
