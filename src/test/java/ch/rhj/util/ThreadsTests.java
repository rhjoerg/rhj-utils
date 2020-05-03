package ch.rhj.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ThreadsTests {

	@Test
	public void testClassLoader() {

		assertTrue(Thread.currentThread().getContextClassLoader() == Threads.contextClassLoader());
	}
}
