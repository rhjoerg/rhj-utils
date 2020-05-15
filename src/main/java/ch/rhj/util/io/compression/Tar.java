package ch.rhj.util.io.compression;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import ch.rhj.util.Ex;
import ch.rhj.util.function.ThrowingBiConsumer;
import ch.rhj.util.function.ThrowingSupplier;
import ch.rhj.util.io.IO;

public interface Tar {

	@FunctionalInterface
	public static interface TarConsumer extends ThrowingBiConsumer<String, byte[], Exception> {
	}

	private static void doExtract(TarArchiveInputStream input, Predicate<String> filter, TarConsumer consumer) {

		Ex.run(() -> {

			TarArchiveEntry tarEntry;

			while ((tarEntry = input.getNextTarEntry()) != null) {

				String name = tarEntry.getName();

				if (!filter.test(name))
					continue;

				consumer.accept(name, IO.read(input));
			}
		});
	}

	public static ThrowingSupplier<TarArchiveInputStream, Exception> tarArchiveInputStream(InputStream input) {

		return () -> new TarArchiveInputStream(input);
	}

	public static void extract(InputStream input, Predicate<String> filter, TarConsumer consumer) {

		IO.consumeInput(tarArchiveInputStream(input), i -> doExtract(i, filter, consumer));
	}

	public static void extract(byte[] bytes, Predicate<String> filter, TarConsumer consumer) {

		extract(new ByteArrayInputStream(bytes), filter, consumer);
	}

	public static void extract(Path path, Predicate<String> filter, TarConsumer consumer) {

		IO.consumeInput(IO.inputStream(path), i -> extract(i, filter, consumer));
	}

	public static void extract(InputStream input, Function<String, String> mapper, Path directory, boolean replace) {

		Predicate<String> filter = s -> mapper.apply(s) != null;
		TarConsumer consumer = (name, bytes) -> IO.write(bytes, directory.resolve(mapper.apply(name)), replace);

		extract(input, filter, consumer);
	}

	public static void extract(byte[] bytes, Function<String, String> mapper, Path directory, boolean replace) {

		extract(new ByteArrayInputStream(bytes), mapper, directory, replace);
	}

	public static void extract(Path path, Function<String, String> mapper, Path directory, boolean replace) {

		IO.consumeInput(IO.inputStream(path), i -> extract(i, mapper, directory, replace));
	}
}
