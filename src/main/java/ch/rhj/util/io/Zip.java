package ch.rhj.util.io;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ch.rhj.util.Ex;
import ch.rhj.util.function.ThrowingBiConsumer;
import ch.rhj.util.function.ThrowingSupplier;

public interface Zip {

	@FunctionalInterface
	public static interface ZipConsumer extends ThrowingBiConsumer<String, byte[], Exception> {
	}

	private static void doExtract(ZipInputStream input, Predicate<String> filter, ZipConsumer consumer) {

		Ex.run(() -> {

			ZipEntry entry;

			while ((entry = input.getNextEntry()) != null) {

				String name = entry.getName();

				if (!filter.test(name))
					continue;

				consumer.accept(name, IO.read(input));
			}
		});
	}

	public static ThrowingSupplier<ZipInputStream, Exception> zipInputStream(InputStream input) {

		return () -> new ZipInputStream(input);
	}

	public static void extract(InputStream input, Predicate<String> filter, ZipConsumer consumer) {

		IO.consumeInput(zipInputStream(input), i -> doExtract(i, filter, consumer));
	}

	public static void extract(byte[] input, Predicate<String> filter, ZipConsumer consumer) {

		extract(new ByteArrayInputStream(input), filter, consumer);
	}

	public static void extract(Path path, Predicate<String> filter, ZipConsumer consumer) {

		IO.consumeInput(IO.inputStream(path), i -> extract(i, filter, consumer));
	}

	public static void extract(InputStream input, Function<String, String> mapper, Path directory, boolean replace) {

		Predicate<String> filter = s -> mapper.apply(s) != null;
		ZipConsumer consumer = (name, bytes) -> IO.write(bytes, directory.resolve(mapper.apply(name)), replace);

		extract(input, filter, consumer);
	}

	public static void extract(byte[] bytes, Function<String, String> mapper, Path directory, boolean replace) {

		extract(new ByteArrayInputStream(bytes), mapper, directory, replace);
	}

	public static void extract(Path path, Function<String, String> mapper, Path directory, boolean replace) {

		IO.consumeInput(IO.inputStream(path), i -> extract(i, mapper, directory, replace));
	}

	public static FileSystem fileSystem(Path zip) {

		String zipUri = zip.toAbsolutePath().toUri().toString();
		URI jarUri = URI.create("jar:" + zipUri);
		FileSystem fileSystem;

		try {

			fileSystem = FileSystems.getFileSystem(jarUri);

		} catch (FileSystemNotFoundException e) {

			Map<String, String> env = Map.of("create", "true");

			fileSystem = Ex.supply(() -> FileSystems.newFileSystem(jarUri, env));
		}

		return fileSystem;
	}
}
