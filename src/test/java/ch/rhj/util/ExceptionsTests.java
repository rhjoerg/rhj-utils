package ch.rhj.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ExceptionsTests {

	@Test
	public void testCtor() {

		new Exceptions();
	}

	@Test
	public void testRuntime() {

		Throwable t1 = new RuntimeException();
		Throwable t2 = new Exception();

		assertTrue(Exceptions.runtime(t1) == t1);
		assertTrue(Exceptions.runtime(t2).getCause() == t2);
	}
}
