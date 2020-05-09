package ch.rhj.util.io;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

public class TarTests {

	@Test
	public void testExtract() {

		Path dir = Paths.get("target", "test-data", "TarTests");
		Path path = dir.resolve("hello.tar");
		byte[] bytes = IO.readResource("io/hello.tar");

		IO.delete(dir);
		IO.write(bytes, path, true);

		Tar.extract(path, name -> name, dir, true);
		Tar.extract(bytes, name -> name, dir, true);

		IO.delete(dir);
		IO.write(bytes, path, true);

		Tar.extract(path, name -> true, (name, bs) -> IO.write(bs, dir.resolve(name), true));
		Tar.extract(bytes, name -> true, (name, bs) -> IO.write(bs, dir.resolve(name), true));
	}
}
