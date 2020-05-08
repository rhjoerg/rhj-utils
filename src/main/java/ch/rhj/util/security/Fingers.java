package ch.rhj.util.security;

import java.math.BigInteger;

import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.operator.KeyFingerPrintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;

import ch.rhj.util.Ex;
import ch.rhj.util.math.Integers;

public interface Fingers {

	public static KeyFingerPrintCalculator fingerPrintCalculator() {

		JcaKeyFingerprintCalculator fingerprintCalculator = new JcaKeyFingerprintCalculator();

		fingerprintCalculator.setProvider(Providers.bc());

		return fingerprintCalculator;
	}

	public static String fingerPrint(byte[] bytes) {

		BigInteger big = new BigInteger(bytes);

		if (big.signum() < 0)
			big = Integers.twosComplement(big);

		return big.toString(16).toUpperCase();
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
}
