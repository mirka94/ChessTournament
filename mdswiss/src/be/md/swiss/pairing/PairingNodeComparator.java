package be.md.swiss.pairing;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import be.md.swiss.Pairing;

public class PairingNodeComparator implements Comparator<PairingNode> {

	@Override
	public int compare(PairingNode o1, PairingNode o2) {
		if (o1.equals(o2))
			return 0;

		Collection<PairingNode> o1Children = o1.getChildren();
		Collection<PairingNode> o2Children = o2.getChildren();

		if (o1Children.size() != o2Children.size())
			return -1;

		List<Pairing> o2Pairings = o2.getPairingsFromThisNodeAndUp();
		List<Pairing> o1Pairings = o1.getPairingsFromThisNodeAndUp();

		if (o1Pairings.size() != o2Pairings.size())
			return -1;

		Set<Pairing> o2PairingSet = new HashSet<>();
		o2PairingSet.addAll(o2Pairings);
		if (!o2PairingSet.containsAll(o1Pairings))
			return -1;

		return 0;
	}
}
