package ch.rhj.util;

import java.nio.file.Path;

public interface SysProps {

	public static Path userHome() {

		return Path.of(System.getProperty("user.home"));
	}

	public static Path userDir() {

		return Path.of(System.getProperty("user.dir"));
	}

	public static Path workingDir() {

		return userDir();
	}
}
