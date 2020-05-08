package ch.rhj.util.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import ch.rhj.util.function.ThrowingSupplier;

public interface Gzip {

	public static ThrowingSupplier<GzipCompressorInputStream, IOException> gzipCompressorInputStream(InputStream input) {

		return () -> new GzipCompressorInputStream(input);
	}

	public static byte[] extract(InputStream input) {

		return IO.apply(gzipCompressorInputStream(input), IO::read);
	}

	public static byte[] extract(byte[] bytes) {

		return extract(new ByteArrayInputStream(bytes));
	}

	public static byte[] extract(Path path) {

		return IO.apply(IO.inputStream(path), Gzip::extract);
	}

	public static void extract(InputStream input, Path target, boolean replace) {

		IO.consumeInput(gzipCompressorInputStream(input), i -> IO.copy(i, target, replace));
	}

	public static void extract(byte[] bytes, Path target, boolean replace) {

		extract(new ByteArrayInputStream(bytes), target, replace);
	}

	public static void extract(Path source, Path target, boolean replace) {

		IO.consumeInput(IO.inputStream(source), i -> IO.copy(i, target, replace));
	}
}
