package ch.rhj.util.security;

import static ch.rhj.util.security.Fingers.fingerPrintCalculator;
import static java.util.stream.Collectors.toList;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPUtil;

import ch.rhj.util.collection.Iterables;
import ch.rhj.util.function.ThrowingConsumer;
import ch.rhj.util.function.ThrowingSupplier;
import ch.rhj.util.io.IO;

public interface Asc {

	public static ThrowingSupplier<InputStream, Exception> decoderStream(InputStream input) {

		Providers.bc();

		return () -> PGPUtil.getDecoderStream(input);
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static List<Object> objects(InputStream input) {

		return IO.apply(decoderStream(input), decoderStream -> {

			PGPObjectFactory objectFactory = new PGPObjectFactory(decoderStream, fingerPrintCalculator());

			return Iterables.list(Iterables.iterable(objectFactory));
		});
	}

	public static List<Object> objects(Path ascPath) {

		return IO.apply(IO.inputStream(ascPath), i -> objects(i));
	}

	public static <T> List<T> objects(InputStream input, Class<T> type) {

		return Asc.objects(input).stream() //
				.filter(o -> type.isAssignableFrom(o.getClass())) //
				.map(o -> type.cast(o)) //
				.collect(toList());
	}

	public static <T> List<T> objects(Path ascPath, Class<T> type) {

		return IO.apply(IO.inputStream(ascPath), i -> objects(i, type));
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static void write(OutputStream output, ThrowingConsumer<OutputStream, ? extends Throwable> consumer) {

		IO.consumeOutput(() -> new ArmoredOutputStream(output), consumer);
	}

	public static void write(Path output, boolean replace, ThrowingConsumer<OutputStream, ? extends Throwable> consumer) {

		IO.consumeOutput(IO.outputStream(output, replace), consumer);
	}
}
