package ch.rhj.util.test;

import java.nio.file.Path;
import java.nio.file.Paths;

import ch.rhj.util.io.IO;

public interface TestPaths {

	public static Path inputDirectory(Class<?> type) {

		return IO.classLoaderPath("test-data/" + type.getPackageName()).toAbsolutePath();
	}

	public static Path outputDirectory(Class<?> type) {

		Path directory = Paths.get("target", "test-data", type.getPackageName()).toAbsolutePath();

		IO.createDirectories(directory);

		return directory;
	}

	public static Path inputPath(Class<?> type, String name) {

		return inputDirectory(type).resolve(name);
	}

	public static Path outputPath(Class<?> type, String name) {

		return outputDirectory(type).resolve(name);
	}

	default Path inputDirectory() {

		return inputDirectory(getClass());
	}

	default Path outputDirectory() {

		return outputDirectory(getClass());
	}

	default Path inputPath(String name) {

		return inputPath(getClass(), name);
	}

	default Path outputPath(String name) {

		return outputPath(getClass(), name);
	}
}
