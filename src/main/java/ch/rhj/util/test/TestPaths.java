package ch.rhj.util.test;

import java.nio.file.Path;
import java.nio.file.Paths;

import ch.rhj.util.io.IO;

public interface TestPaths {

	public static Path inputPath(Class<?> type, String name) {

		return IO.classLoaderPath("test-data/" + type.getPackageName() + "/" + name);
	}

	public static Path outputPath(Class<?> type, String name) {

		return Paths.get("target", "test-data", type.getPackageName(), name);
	}

	default Path inputPath(String name) {

		return inputPath(getClass(), name);
	}

	default Path outputPath(String name) {

		return outputPath(getClass(), name);
	}
}
