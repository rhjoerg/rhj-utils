package ch.rhj.util.security;

import static org.bouncycastle.bcpg.sig.KeyFlags.AUTHENTICATION;
import static org.bouncycastle.bcpg.sig.KeyFlags.CERTIFY_OTHER;
import static org.bouncycastle.bcpg.sig.KeyFlags.ENCRYPT_COMMS;
import static org.bouncycastle.bcpg.sig.KeyFlags.ENCRYPT_STORAGE;
import static org.bouncycastle.bcpg.sig.KeyFlags.SIGN_DATA;

import java.math.BigInteger;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Date;

import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.PublicKeyAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.bcpg.sig.Features;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPSignatureSubpacketVector;
import org.bouncycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPKeyPair;

import ch.rhj.util.Ex;

/**
 * NOT RE-USABLE!
 */
public class KeyBuilder {

	public final static int KEY_STRENGTH = 2048;
	public final static int CERTAINTY = 12;
	public final static BigInteger PUPLIC_EXPONENT = BigInteger.valueOf(0x10001);

	public final static int KEY_FLAGS = CERTIFY_OTHER | SIGN_DATA | ENCRYPT_COMMS | ENCRYPT_STORAGE | AUTHENTICATION;

	public final static int CERTIFICATION_LEVEL = PGPSignature.POSITIVE_CERTIFICATION;

	public final static int KEYPAIR_ALGORITHM = PublicKeyAlgorithmTags.RSA_GENERAL;
	public final static int ENCRYPTION_ALGORITHM = SymmetricKeyAlgorithmTags.AES_256;
	public final static int HASH_ALGORITHM = HashAlgorithmTags.SHA512;
	public final static int DIGEST_ALGORITHM = HashAlgorithmTags.SHA1;

	public final static int S2K_COUNT = 0xc0;

	public final Provider provider = Providers.bc();

	private String name;
	private String email;
	private String password;

	private PGPKeyPair keyPair;

	private KeyBuilder(String name, String email, String password) {

		this.name = name;
		this.email = email;
		this.password = password;
	}

	private void clear() {

		keyPair = null;

		password = null;
		email = null;
		name = null;
	}

	private PGPKeyPair keyPair() {

		if (keyPair == null) {

			SecureRandom secureRandom = new SecureRandom();
			RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();

			keyPairGenerator.init(new RSAKeyGenerationParameters(PUPLIC_EXPONENT, secureRandom, KEY_STRENGTH, CERTAINTY));

			AsymmetricCipherKeyPair asymmetricCipherKeyPair = keyPairGenerator.generateKeyPair();

			keyPair = Ex.supply(() -> new BcPGPKeyPair(KEYPAIR_ALGORITHM, asymmetricCipherKeyPair, new Date()));
		}

		return keyPair;
	}

	private PGPSignatureSubpacketGenerator keySignatureGenerator() {

		PGPSignatureSubpacketGenerator keySignatureGenerator = new PGPSignatureSubpacketGenerator();

		keySignatureGenerator.setKeyFlags(false, KEY_FLAGS);
		keySignatureGenerator.setPreferredSymmetricAlgorithms(false, new int[] { ENCRYPTION_ALGORITHM });
		keySignatureGenerator.setPreferredHashAlgorithms(false, new int[] { HASH_ALGORITHM });
		keySignatureGenerator.setFeature(false, Features.FEATURE_MODIFICATION_DETECTION);

		return keySignatureGenerator;
	}

	private PBESecretKeyEncryptor secretKeyEncryptor() {

		PGPDigestCalculator s2kDigestCalculator = Ex.supply(() -> new BcPGPDigestCalculatorProvider().get(HASH_ALGORITHM));
		char[] password = this.password.toCharArray();

		return new BcPBESecretKeyEncryptorBuilder(ENCRYPTION_ALGORITHM, s2kDigestCalculator, S2K_COUNT).build(password);
	}

	private PGPKeyRingGenerator keyRingGenerator() {

		String id = String.format("%1$s <%2$s>", name, email);
		PGPDigestCalculator digestCalculator = Ex.supply(() -> new BcPGPDigestCalculatorProvider().get(DIGEST_ALGORITHM));
		PGPSignatureSubpacketVector keySignature = keySignatureGenerator().generate();
		PGPContentSignerBuilder contentSignerBuilder = Signatures.contentSignerBuilder(keyPair().getPrivateKey());

		return Ex.supply(() -> new PGPKeyRingGenerator( //
				CERTIFICATION_LEVEL, keyPair(), id, digestCalculator, //
				keySignature, null, contentSignerBuilder, secretKeyEncryptor()));
	}

	public PGPSecretKeyRing build() {

		PGPSecretKeyRing secretKeyRing = keyRingGenerator().generateSecretKeyRing();

		clear();

		return secretKeyRing;
	}

	public static KeyBuilder keyBuilder(String name, String email, String password) {

		return new KeyBuilder(name, email, password);
	}
}
