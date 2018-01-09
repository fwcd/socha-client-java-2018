package com.thedroide.sc18.test.unittest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ // The Playground class is intentionally not included
	CacheTest.class
})
public class AllTests {
}
