package be.md.swiss;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class TestPlayerComparatorByElo {

	private Player highRated = PlayerUtils.createRatedPlayer(2500);
	private Player classARated = PlayerUtils.createRatedPlayer(1900);
	private Player classBRated = PlayerUtils.createRatedPlayer(1600);
	private Player lowRated = PlayerUtils.createRatedPlayer(1100);

	@Test
	public void testComparePlayersByEloFromHighToLow() {
		Set<Player> sorted = new TreeSet<Player>(
				PlayerComparatorBySonnenbornBerger.createFromHighToLowPointsSonnebornRating());

		sorted.add(lowRated);
		sorted.add(classARated);
		sorted.add(highRated);
		sorted.add(classBRated);

		Iterator<Player> iterator = sorted.iterator();
		assertEquals(highRated, iterator.next());
		assertEquals(classARated, iterator.next());
		assertEquals(classBRated, iterator.next());
		assertEquals(lowRated, iterator.next());
	}

	@Test
	public void testComparePlayersByEloFromLowToHigh() {
		Set<Player> sorted = new TreeSet<Player>(
				PlayerComparatorBySonnenbornBerger.createFromLowToHighPointsSonnebornRating());

		sorted.add(lowRated);
		sorted.add(classARated);
		sorted.add(highRated);
		sorted.add(classBRated);

		Iterator<Player> iterator = sorted.iterator();
		assertEquals(lowRated, iterator.next());
		assertEquals(classBRated, iterator.next());
		assertEquals(classARated, iterator.next());
		assertEquals(highRated, iterator.next());
	}

}
