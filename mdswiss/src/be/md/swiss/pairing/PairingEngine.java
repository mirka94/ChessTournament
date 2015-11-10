package be.md.swiss.pairing;

import java.util.Set;

import be.md.swiss.Player;

public interface PairingEngine {

	Round pairNextRound(Set<Player> players);

}
