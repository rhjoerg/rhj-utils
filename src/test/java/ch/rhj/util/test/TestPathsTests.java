package ch.rhj.util.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.rhj.util.io.IO;

public class TestPathsTests implements TestPaths {

	@Test
	public void testTestPaths() {

		assertTrue(IO.exists(inputDirectory()));
		assertTrue(IO.exists(outputDirectory()));

		assertNotNull(inputPath("hello.txt"));
		assertTrue(outputPath("hello.txt").toUri().toASCIIString().endsWith("/target/test-data/ch.rhj.util.test/hello.txt"));
	}
}
