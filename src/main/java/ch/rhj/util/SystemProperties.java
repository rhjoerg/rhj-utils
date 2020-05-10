package ch.rhj.util;

import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public interface SystemProperties {

	public static Set<String> names() {

		return System.getProperties().stringPropertyNames();
	}

	public static void setIfAbsent(String name, String value) {

		if (!names().contains(name))
			System.setProperty(name, value);
	}

	public static void setIfAbsent(Map<String, String> entries) {

		entries.forEach((name, value) -> setIfAbsent(name, value));
	}

	public static void setIfAbsent(Properties properties) {

		properties.forEach((name, value) -> setIfAbsent(String.valueOf(name), String.valueOf(value)));
	}

	public static String userName() {

		return System.getProperty("user.name");
	}

	public static Path userHomeDirectory() {

		return Path.of(System.getProperty("user.home"));
	}

	public static Path workingDirectory() {

		return Path.of(System.getProperty("user.dir"));
	}
}
