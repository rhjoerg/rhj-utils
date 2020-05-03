package ch.rhj.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class MutableTests {

	@Test
	public void test() {

		Mutable<String> string = Mutable.empty();

		assertNull(string.get());
		string.set("a");
		assertEquals("a", string.get());
	}
}
