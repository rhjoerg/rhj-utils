package ch.rhj.util.io;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ch.rhj.util.io.compression.Zip;
import ch.rhj.util.test.TestPaths;

public class ZipTests implements TestPaths {

	@Test
	public void testNames() {

		Path input = inputPath("hello.zip");
		byte[] bytes = IO.read(input);

		Set<String> expected = Set.of("hello.txt", "f1/hello.txt", "f2/hello.txt");
		Set<String> actual;

		actual = Zip.names(input);
		assertEquals(expected, actual);

		actual = Zip.names(bytes);
		assertEquals(expected, actual);
	}

	@Test
	public void testExtract() {

		Path input = inputPath("hello.zip");
		byte[] bytes = IO.read(input);

		Path output = outputPath("ZipTests");

		IO.delete(output);

		Zip.extract(input, name -> name, output, true);

		IO.delete(output);
		Zip.extract(bytes, name -> name.equals("hello.txt") ? name : null, output, true);

		Zip.ZipConsumer noopConsumer = (s, b) -> {
		};

		Zip.extract(bytes, name -> name.equals("hello.txt"), noopConsumer);
		Zip.extract(input, name -> name.equals("hello.txt"), noopConsumer);
	}

	@Test
	public void testFileSystem() throws Exception {

		Path input = inputPath("hello.zip");
		FileSystem fileSystem = Zip.fileSystem(input);
		Path helloPath = fileSystem.getPath("hello.txt");

		assertEquals("hello, world!", IO.readString(helloPath, UTF_8));

		fileSystem = Zip.fileSystem(input);
		helloPath = fileSystem.getPath("hello.txt");

		assertEquals("hello, world!", IO.readString(helloPath, UTF_8));
	}
}
