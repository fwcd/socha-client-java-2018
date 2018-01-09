package com.thedroide.sc18.unittest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	CacheTest.class,
	NeuralTest.class
})
public class AllTests {
}
