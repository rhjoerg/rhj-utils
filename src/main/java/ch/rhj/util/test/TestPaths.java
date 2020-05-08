package ch.rhj.util.test;

import java.nio.file.Path;
import java.nio.file.Paths;

import ch.rhj.util.io.IO;

public interface TestPaths {

	default Path inputPath(String name) {

		return IO.classLoaderPath("test-data/" + getClass().getPackageName() + "/" + name);
	}

	default Path outputPath(String name) {

		return Paths.get("target", "test-data", getClass().getPackageName(), name);
	}
}
