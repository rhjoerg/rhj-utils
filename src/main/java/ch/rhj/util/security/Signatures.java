package ch.rhj.util.security;

import static ch.rhj.util.collection.Iterables.stream;
import static java.util.stream.Collectors.toList;
import static org.bouncycastle.bcpg.HashAlgorithmTags.SHA512;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;

import ch.rhj.util.Ex;
import ch.rhj.util.io.IO;

public interface Signatures {

	public static List<PGPSignatureList> signatureLists(InputStream input) {

		return Asc.objects(input, PGPSignatureList.class);
	}

	public static List<PGPSignatureList> signatureLists(Path path) {

		return IO.apply(IO.inputStream(path), i -> signatureLists(i));
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static List<PGPSignature> signatures(InputStream input) {

		return signatureLists(input).stream().flatMap(sl -> stream(sl)).collect(toList());
	}

	public static List<PGPSignature> signatures(Path path) {

		return IO.apply(IO.inputStream(path), i -> signatures(i));
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static PGPContentSignerBuilder contentSignerBuilder(PGPPrivateKey privateKey, int hashAlgorithm) {

		int keyAlgorithm = privateKey.getPublicKeyPacket().getAlgorithm();

		JcaPGPContentSignerBuilder contentSignerBuilder = new JcaPGPContentSignerBuilder(keyAlgorithm, hashAlgorithm);

		contentSignerBuilder.setProvider(Providers.bc());

		return contentSignerBuilder;
	}

	public static PGPContentSignerBuilder contentSignerBuilder(PGPPrivateKey privateKey) {

		return contentSignerBuilder(privateKey, SHA512);
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static PGPSignatureGenerator signatureGenerator(PGPPrivateKey privateKey) {

		return new PGPSignatureGenerator(contentSignerBuilder(privateKey));
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static PGPSignature sign(InputStream input, PGPPrivateKey privateKey) {

		PGPSignatureGenerator signatureGenerator = signatureGenerator(privateKey);

		return Ex.supply(() -> {

			signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey);
			signatureGenerator.update(IO.read(input));

			return signatureGenerator.generate();
		});
	}

	public static PGPSignature sign(Path path, PGPPrivateKey privateKey) {

		return IO.apply(IO.inputStream(path), i -> sign(i, privateKey));
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static void write(PGPSignature signature, OutputStream output) {

		Asc.write(output, o -> signature.encode(o));
	}

	public static void write(PGPSignature signature, Path output, boolean replace) {

		IO.consumeOutput(IO.outputStream(output, replace), o -> write(signature, o));
	}
}
