package ch.rhj.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;

public class SystemPropertiesTests {

	@Test
	public void testNames() {

		assertTrue(SystemProperties.names().contains("user.name"));
	}

	@Test
	public void testSetIfAbsent() {

		String userName = System.getProperty("user.name");

		assertNull(System.getProperty("SystemPropertiesTests.1"));
		assertNull(System.getProperty("SystemPropertiesTests.2"));

		Map<String, String> map = Map.of("user.name", "foo", "SystemPropertiesTests.1", "foo");

		SystemProperties.setIfAbsent("user.name", "foo");
		assertEquals(userName, System.getProperty("user.name"));

		SystemProperties.setIfAbsent(map);
		assertEquals(userName, System.getProperty("user.name"));
		assertEquals("foo", System.getProperty("SystemPropertiesTests.1"));

		Properties properties = new Properties();
		properties.put("user.name", "foo");
		properties.put("SystemPropertiesTests.2", "bar");

		SystemProperties.setIfAbsent(properties);
		assertEquals(userName, System.getProperty("user.name"));
		assertEquals("bar", System.getProperty("SystemPropertiesTests.2"));
	}

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

	@Test
	public void testCopy() {

		Properties properties = SystemProperties.copy();

		assertTrue(properties.containsKey("user.name"));
	}
}
