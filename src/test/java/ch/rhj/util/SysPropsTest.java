package ch.rhj.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class SysPropsTest {

	@Test
	public void testUserName() {

		assertEquals(System.getProperty("user.name"), SysProps.userName());
	}

	@Test
	public void testUserHome() {

		Path directory = SysProps.userHome();

		assertTrue(Files.exists(directory));
		assertTrue(Files.isDirectory(directory));
	}

	@Test
	public void testWorkingDir() {

		Path directory = SysProps.workingDir();

		assertTrue(Files.exists(directory));
		assertTrue(Files.isDirectory(directory));
	}
}
