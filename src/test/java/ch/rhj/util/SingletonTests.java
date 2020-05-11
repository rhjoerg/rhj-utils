package ch.rhj.util;

import static ch.rhj.util.Singleton.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SingletonTests {

	@Test
	public void test() {

		String expected = "hello";
		Singleton<String> string = singleton(() -> expected);

		assertFalse(string.hasValue());
		assertEquals(expected, string.get());
		assertTrue(string.get() == string.get());
		assertTrue(string.hasValue());
	}
}
