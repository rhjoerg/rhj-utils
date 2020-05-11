package ch.rhj.util.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class OsTests {

	@Test
	public void test() {

		boolean isMac = Os.isMac();
		boolean isUnix = Os.isUnix();
		boolean isWindows = Os.isWindows();
		boolean isSomething = Os.isFamily("");

		assertTrue(isMac || isUnix || isWindows || isSomething);
		assertFalse(Os.isFamily(null));
	}
}
