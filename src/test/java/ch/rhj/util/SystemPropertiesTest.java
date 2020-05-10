package ch.rhj.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class SystemPropertiesTest {

	@Test
	public void testUserName() {

		assertEquals(System.getProperty("user.name"), SystemProperties.userName());
	}

	@Test
	public void testUserHome() {

		Path directory = SystemProperties.userHomeDirectory();

		assertTrue(Files.exists(directory));
		assertTrue(Files.isDirectory(directory));
	}

	@Test
	public void testWorkingDir() {

		Path directory = SystemProperties.workingDirectory();

		assertTrue(Files.exists(directory));
		assertTrue(Files.isDirectory(directory));
	}
}
