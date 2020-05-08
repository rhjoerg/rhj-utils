package ch.rhj.util.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TestPathsTests implements TestPaths {

	@Test
	public void testTestPaths() {

		assertNotNull(inputPath("hello.txt"));
		assertTrue(outputPath("hello.txt").toUri().toASCIIString().endsWith("/target/test-data/ch.rhj.util.test/hello.txt"));
	}
}
