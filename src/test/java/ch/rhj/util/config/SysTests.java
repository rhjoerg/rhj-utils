package ch.rhj.util.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;

public class SysTests {

	@Test
	public void testNames() {

		assertTrue(Sys.names().contains("user.name"));
	}

	@Test
	public void testSetIfAbsent() {

		String userName = System.getProperty("user.name");

		assertNull(System.getProperty("SystemPropertiesTests.1"));
		assertNull(System.getProperty("SystemPropertiesTests.2"));

		Map<String, String> map = Map.of("user.name", "foo", "SystemPropertiesTests.1", "foo");

		Sys.setIfAbsent("user.name", "foo");
		assertEquals(userName, System.getProperty("user.name"));

		Sys.setIfAbsent(map);
		assertEquals(userName, System.getProperty("user.name"));
		assertEquals("foo", System.getProperty("SystemPropertiesTests.1"));

		Properties properties = new Properties();
		properties.put("user.name", "foo");
		properties.put("SystemPropertiesTests.2", "bar");

		Sys.setIfAbsent(properties);
		assertEquals(userName, System.getProperty("user.name"));
		assertEquals("bar", System.getProperty("SystemPropertiesTests.2"));
	}

	@Test
	public void testUserName() {

		assertEquals(System.getProperty("user.name"), Sys.userName());
	}

	@Test
	public void testUserHome() {

		Path directory = Sys.userHomeDirectory();

		assertTrue(Files.exists(directory));
		assertTrue(Files.isDirectory(directory));
	}

	@Test
	public void testWorkingDir() {

		Path directory = Sys.workingDirectory();

		assertTrue(Files.exists(directory));
		assertTrue(Files.isDirectory(directory));
	}

	@Test
	public void testCopy() {

		Properties properties = Sys.copy();

		assertTrue(properties.containsKey("user.name"));
	}
}
