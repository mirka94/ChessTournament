package be.md.swiss;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import be.md.swiss.pairing.TestHighToLowComparator;
import be.md.swiss.pairing.TestSwissPairingEngine;

@RunWith(Suite.class)
@SuiteClasses(value = { TestPlayer.class, TestTournament.class, TestPairing.class, TestTournamentPairing.class,
		TestPlayerComparatorByElo.class, TestSwissPairingEngine.class, TestHighToLowComparator.class, TestRound.class,
		TestColorPreference.class })
public class TestAll {

}
