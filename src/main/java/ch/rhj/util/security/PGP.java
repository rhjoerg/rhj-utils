package ch.rhj.util.security;

import static java.util.stream.Collectors.toList;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.KeyFingerPrintCalculator;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

import ch.rhj.util.Ex;
import ch.rhj.util.collection.Iterables;
import ch.rhj.util.collection.Iterators;
import ch.rhj.util.function.ThrowingSupplier;
import ch.rhj.util.io.IO;

public interface PGP {

	public static ThrowingSupplier<InputStream, Exception> decoderStream(InputStream input) {

		return () -> PGPUtil.getDecoderStream(input);
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static PBESecretKeyDecryptor secretKeyDecryptor(String password) {

		return Ex.supply(() -> new JcePBESecretKeyDecryptorBuilder().setProvider(Providers.bc()).build(password.toCharArray()));
	}

	public static KeyFingerPrintCalculator fingerPrintCalculator() {

		return new JcaKeyFingerprintCalculator();
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static String fingerPrint(byte[] bytes) {

		return new BigInteger(bytes).toString(16).toUpperCase();
	}

	public static String fingerPrint(PGPPublicKey publicKey) {

		return fingerPrint(publicKey.getFingerprint());
	}

	public static String fingerPrint(PGPSecretKey secretKey) {

		return fingerPrint(secretKey.getPublicKey());
	}

	public static String fingerPrint(PGPPrivateKey privateKey) {

		return fingerPrint(Ex.supply(() -> fingerPrintCalculator().calculateFingerprint(privateKey.getPublicKeyPacket())));
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static List<Object> objects(InputStream input) {

		Providers.bc();

		return IO.applyToInput(decoderStream(input), decoderStream -> {

			PGPObjectFactory objectFactory = new PGPObjectFactory(decoderStream, fingerPrintCalculator());

			return Iterables.list(Iterables.iterable(objectFactory));
		});
	}

	public static List<Object> objects(Path ascPath) {

		return IO.applyToInput(IO.inputStream(ascPath), i -> objects(i));
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static <T> List<T> objects(InputStream input, Class<T> type) {

		return objects(input).stream() //
				.filter(o -> type.isAssignableFrom(o.getClass())) //
				.map(o -> type.cast(o)) //
				.collect(toList());
	}

	public static <T> List<T> objects(Path ascPath, Class<T> type) {

		return IO.applyToInput(IO.inputStream(ascPath), i -> objects(i, type));
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static List<PGPSecretKeyRing> secretKeyRings(InputStream input) {

		return objects(input, PGPSecretKeyRing.class);
	}

	public static List<PGPSecretKeyRing> secretKeyRings(Path path) {

		return IO.applyToInput(IO.inputStream(path), PGP::secretKeyRings);
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static List<PGPSecretKey> secretKeys(InputStream input) {

		return secretKeyRings(input).stream() //
				.flatMap(skr -> Iterators.stream(skr.getSecretKeys())) //
				.collect(toList());
	}

	public static List<PGPSecretKey> secretKeys(Path path) {

		return IO.applyToInput(IO.inputStream(path), PGP::secretKeys);
	}

	public static Optional<PGPSecretKey> secretKey(InputStream input, String fingerPrint) {

		String fixedFingerPrint = fingerPrint.toUpperCase();

		return secretKeys(input).stream().filter(k -> fingerPrint(k).equals(fixedFingerPrint)).findFirst();
	}

	public static Optional<PGPSecretKey> secretKey(Path path, String fingerPrint) {

		return IO.applyToInput(IO.inputStream(path), i -> secretKey(i, fingerPrint));
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static PGPPrivateKey privateKey(PGPSecretKey secretKey, String password) {

		return Ex.supply(() -> secretKey.extractPrivateKey(secretKeyDecryptor(password)));
	}

	public static Optional<PGPPrivateKey> privateKey(InputStream input, String fingerPrint, String password) {

		return secretKey(input, fingerPrint).map(sk -> privateKey(sk, password));
	}

	public static Optional<PGPPrivateKey> privateKey(Path path, String fingerPrint, String password) {

		return IO.applyToInput(IO.inputStream(path), i -> privateKey(i, fingerPrint, password));
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static List<PGPSignatureList> signatureLists(InputStream input) {

		return objects(input, PGPSignatureList.class);
	}

	public static List<PGPSignatureList> signatureLists(Path path) {

		return IO.applyToInput(IO.inputStream(path), PGP::signatureLists);
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static List<PGPSignature> signatures(InputStream input) {

		return signatureLists(input).stream().flatMap(sl -> Iterables.stream(sl)).collect(toList());
	}

	public static List<PGPSignature> signatures(Path path) {

		return IO.applyToInput(IO.inputStream(path), PGP::signatures);
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static PGPContentSignerBuilder contentSignerBuilder(PGPPrivateKey privateKey) {

		int algorithm = privateKey.getPublicKeyPacket().getAlgorithm();

		return new JcaPGPContentSignerBuilder(algorithm, PGPUtil.SHA256).setProvider(Providers.bc());
	}

	public static PGPSignatureGenerator signatureGenerator(PGPPrivateKey privateKey) {

		return new PGPSignatureGenerator(contentSignerBuilder(privateKey));
	}

	public static PGPSignature sign(InputStream input, PGPPrivateKey privateKey) {

		PGPSignatureGenerator signatureGenerator = signatureGenerator(privateKey);

		return Ex.supply(() -> {

			signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey);
			signatureGenerator.update(IO.read(input));

			return signatureGenerator.generate();
		});
	}

	public static PGPSignature sign(Path path, PGPPrivateKey privateKey) {

		return IO.applyToInput(IO.inputStream(path), i -> sign(i, privateKey));
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static void write(PGPSignature signature, BCPGOutputStream output) {

		Ex.consume(signature::encode, output);
	}

	public static void write(PGPSignature signature, ArmoredOutputStream output) {

		IO.consumeOutput(() -> new BCPGOutputStream(output), o -> write(signature, o));
	}

	public static void write(PGPSignature signature, OutputStream output) {

		IO.consumeOutput(() -> new ArmoredOutputStream(output), o -> write(signature, o));
	}

	public static void write(PGPSignature signature, Path output, boolean replace) {

		IO.consumeOutput(IO.outputStream(output, replace), o -> write(signature, o));
	}

	// ----------------------------------------------------------------------------------------------------------------

}
