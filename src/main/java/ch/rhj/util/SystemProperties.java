package ch.rhj.util;

import java.nio.file.Path;

public interface SystemProperties {

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
