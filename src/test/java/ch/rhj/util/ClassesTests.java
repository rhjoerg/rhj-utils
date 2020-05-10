package ch.rhj.util;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import ch.rhj.util.io.IO;
import ch.rhj.util.io.Zip;

public class ClassesTests {

	@Test
	public void testFindJar() {

		Path root = Paths.get(System.getProperty("java.home"));

		long count = IO.findFilesWithExtension(root, false, "jar", Integer.MAX_VALUE) //
				.map(p -> Zip.fileSystem(p)) //
				.map(fs -> fs.getPath("META-INF", "MANIFEST.MF")) //
				.filter(p -> IO.exists(p)) //
				.count();

		System.out.println(count);
		System.out.println(root);
	}
}
