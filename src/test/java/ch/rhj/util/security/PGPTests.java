package ch.rhj.util.security;

import static ch.rhj.util.security.Asc.objects;
import static ch.rhj.util.security.Fingers.fingerPrint;
import static ch.rhj.util.security.Keys.privateKey;
import static ch.rhj.util.security.Keys.secretKey;
import static ch.rhj.util.security.Keys.secretKeyRings;
import static ch.rhj.util.security.Keys.secretKeys;
import static ch.rhj.util.security.Signs.signatureLists;
import static ch.rhj.util.security.Signs.signatures;
import static org.bouncycastle.bcpg.HashAlgorithmTags.SHA512;
import static org.bouncycastle.bcpg.PublicKeyAlgorithmTags.RSA_GENERAL;
import static org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags.AES_256;
import static org.bouncycastle.bcpg.sig.KeyFlags.CERTIFY_OTHER;
import static org.bouncycastle.bcpg.sig.KeyFlags.ENCRYPT_COMMS;
import static org.bouncycastle.bcpg.sig.KeyFlags.ENCRYPT_STORAGE;
import static org.bouncycastle.bcpg.sig.KeyFlags.SIGN_DATA;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.sig.Features;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPSignatureSubpacketVector;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPKeyPair;
import org.junit.jupiter.api.Test;

import ch.rhj.util.SysProps;
import ch.rhj.util.io.IO;

public class PGPTests {

	private final Path keyPath = SysProps.userHome().resolve(".keys/rhjoerg.asc");
	private final Path pomPath = IO.classLoaderPath("security/rhj-parent-20200506.4.pom");
	private final Path sigPath = IO.classLoaderPath("security/rhj-parent-20200506.4.pom.asc");

	private final String fingerPrint = "7D4C2F584D416778966CB60E4517E082A2E58156";
	private final String password = System.getenv("GPG_PASSPHRASE");

	@Test
	public void testSecretKeys() {

		assertEquals(2, secretKeys(keyPath).size());
		assertTrue(secretKey(keyPath, fingerPrint).isPresent());
		assertEquals(fingerPrint, fingerPrint(privateKey(keyPath, fingerPrint, password).get()));
	}

	@Test
	public void testLists() {

		assertEquals(1, objects(sigPath).size());
		assertEquals(1, objects(sigPath, PGPSignatureList.class).size());
		assertEquals(1, secretKeyRings(keyPath).size());
		assertEquals(1, signatureLists(sigPath).size());
	}

	@Test
	public void testSignatures() {

		List<PGPSignature> signatures = signatures(sigPath);

		assertEquals(1, signatures.size());
		assertEquals(PGPUtil.SHA256, signatures.get(0).getHashAlgorithm());
	}

	@Test
	public void testSign() throws IOException {

		PGPPrivateKey privateKey = privateKey(keyPath, fingerPrint, password).get();
		PGPSignature signature = Signs.sign(pomPath, privateKey);
		Path path = Paths.get("target", "test-data", "PGPTests", "pom.asc");

		Signs.write(signature, path, true);

		PGPSignature signature2 = signatures(path).get(0);

		assertArrayEquals(signature.getEncoded(), signature2.getEncoded());
	}

	@Test
	public void experiment() throws Exception {

		Providers.bc();

		Date now = new Date();
		SecureRandom secureRandom = new SecureRandom();
		RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();

		keyPairGenerator.init(new RSAKeyGenerationParameters(BigInteger.valueOf(0x10001), secureRandom, 2048, 12));

		PGPKeyPair keyPair = new BcPGPKeyPair(RSA_GENERAL, keyPairGenerator.generateKeyPair(), now);

		PGPSignatureSubpacketGenerator keySignatureGenerator = new PGPSignatureSubpacketGenerator();

		keySignatureGenerator.setKeyFlags(false, ENCRYPT_COMMS | ENCRYPT_STORAGE | SIGN_DATA | CERTIFY_OTHER);
		keySignatureGenerator.setPreferredSymmetricAlgorithms(false, new int[] { AES_256 });
		keySignatureGenerator.setPreferredHashAlgorithms(false, new int[] { SHA512 });
		keySignatureGenerator.setFeature(false, Features.FEATURE_MODIFICATION_DETECTION);

		PGPSignatureSubpacketVector keySignature = keySignatureGenerator.generate();

		PGPDigestCalculator digestCalculator = new BcPGPDigestCalculatorProvider().get(HashAlgorithmTags.SHA1);
		PGPContentSignerBuilder contentSignerBuilder = new BcPGPContentSignerBuilder(keyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1);
		PBESecretKeyEncryptor secretKeyEncryptor = new BcPBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256,
				new BcPGPDigestCalculatorProvider().get(HashAlgorithmTags.SHA256), 0xc0).build("password".toCharArray());
		PGPKeyRingGenerator keyRingGenerator = new PGPKeyRingGenerator(PGPSignature.POSITIVE_CERTIFICATION, keyPair, "foo <foo@example.com>", digestCalculator,
				keySignature, null, contentSignerBuilder, secretKeyEncryptor);

		PGPSecretKeyRing secretKeyRing = keyRingGenerator.generateSecretKeyRing();

		Path output = Paths.get("target", "whatever.asc");
		IO.createDirectories(output.getParent());
		IO.delete(output);

		Asc.write(output, true, o -> secretKeyRing.encode(o));
	}
}
