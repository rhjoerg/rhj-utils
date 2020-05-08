package ch.rhj.util.security;

import static ch.rhj.util.collection.Iterators.stream;
import static ch.rhj.util.security.Asc.objects;
import static ch.rhj.util.security.Fingers.fingerPrint;
import static ch.rhj.util.security.Pass.secretKeyDecryptor;
import static java.util.stream.Collectors.toList;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;

import ch.rhj.util.Ex;
import ch.rhj.util.io.IO;

public interface Keys {

	public static List<PGPSecretKeyRing> secretKeyRings(InputStream input) {

		return objects(input, PGPSecretKeyRing.class);
	}

	public static List<PGPSecretKeyRing> secretKeyRings(Path path) {

		return IO.apply(IO.inputStream(path), i -> secretKeyRings(i));
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static List<PGPSecretKey> secretKeys(InputStream input) {

		return secretKeyRings(input).stream() //
				.flatMap(skr -> stream(skr.getSecretKeys())) //
				.collect(toList());
	}

	public static List<PGPSecretKey> secretKeys(Path path) {

		return IO.apply(IO.inputStream(path), i -> secretKeys(i));
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static Optional<PGPSecretKey> secretKey(InputStream input, String fingerPrint) {

		String fixedFingerPrint = fingerPrint.toUpperCase();

		return secretKeys(input).stream().filter(k -> fingerPrint(k).equals(fixedFingerPrint)).findFirst();
	}

	public static Optional<PGPSecretKey> secretKey(Path path, String fingerPrint) {

		return IO.apply(IO.inputStream(path), i -> secretKey(i, fingerPrint));
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static PGPPrivateKey privateKey(PGPSecretKey secretKey, String password) {

		return Ex.supply(() -> secretKey.extractPrivateKey(secretKeyDecryptor(password)));
	}

	public static Optional<PGPPrivateKey> privateKey(InputStream input, String fingerPrint, String password) {

		return secretKey(input, fingerPrint).map(sk -> privateKey(sk, password));
	}

	public static Optional<PGPPrivateKey> privateKey(Path path, String fingerPrint, String password) {

		return IO.apply(IO.inputStream(path), i -> privateKey(i, fingerPrint, password));
	}

	// ----------------------------------------------------------------------------------------------------------------

	// ----------------------------------------------------------------------------------------------------------------

}
