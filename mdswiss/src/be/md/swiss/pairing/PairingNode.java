package be.md.swiss.pairing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import be.md.swiss.Pairing;

public class PairingNode {

	private Collection<PairingNode> children;
	private List<Pairing> pairings;
	private PairingNode parent;

	private PairingNode(List<Pairing> pairings, Collection<PairingNode> childNodes) {
		this.pairings = pairings;
		this.children = childNodes;
	}

	public static PairingNode createNodeWithChildren(List<Pairing> pairings, Collection<PairingNode> childNodes) {
		PairingNode result = new PairingNode(pairings, childNodes);
		for (PairingNode child : childNodes) {
			child.setParent(result);
		}
		return result;

	}

	public void setParent(PairingNode parent) {
		this.parent = parent;
	}

	public Collection<PairingNode> getChildren() {
		return Collections.unmodifiableCollection(children);
	}

	public boolean isEmpty() {
		return children.isEmpty() && pairings.isEmpty();
	}

	public boolean isEndNode() {
		return this.children.isEmpty();
	}

	public List<Pairing> getPairingsFromThisNodeAndUp() {
		List<Pairing> result = new ArrayList<>();
		result.addAll(pairings);
		if (parent != null)
			result.addAll(parent.getPairingsFromThisNodeAndUp());
		return result;
	}

	public int getNumberOfPairingsForThisNodeOnly() {
		return pairings.size();
	}

	@Override
	public String toString() {
		List<Pairing> pairings = getPairingsFromThisNodeAndUp();
		StringBuilder builder = new StringBuilder();
		for (Pairing p : pairings)
			builder.append(p.toString());
		return "PairingNode: node?:" + isEndNode() + " " + builder.toString();

	}
}
