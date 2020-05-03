package ch.rhj.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class PropsTest {

	@Test
	public void testUserHome() {

		Path directory = Props.userHome();

		assertTrue(Files.exists(directory));
		assertTrue(Files.isDirectory(directory));
	}

	@Test
	public void testWorkingDir() {

		Path directory = Props.workingDir();

		assertTrue(Files.exists(directory));
		assertTrue(Files.isDirectory(directory));
	}
}
