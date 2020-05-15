package ch.rhj.util.io;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import ch.rhj.util.io.compression.Gzip;

public class GzipTests {

	@Test
	public void testExtract() {

		Path dir = Paths.get("target", "test-data", "TarTests");
		Path source = dir.resolve("hello.tar.gz");
		Path target = dir.resolve("hello.tar");

		IO.delete(dir);
		IO.write(IO.readResource("io/hello.tar.gz"), source, true);

		Gzip.extract(source, target, true);
		Gzip.extract(source);

		byte[] bytes = IO.read(source);

		Gzip.extract(bytes);
		Gzip.extract(bytes, target, true);
	}
}
