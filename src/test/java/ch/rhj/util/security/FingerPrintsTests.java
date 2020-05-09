package ch.rhj.util.security;

import static ch.rhj.util.security.FingerPrints.fingerPrint;
import static ch.rhj.util.security.KeyBuilder.keyBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.junit.jupiter.api.Test;

import ch.rhj.util.test.TestPaths;

public class FingerPrintsTests implements TestPaths {

	@Test
	public void test() throws Exception {

		PGPSecretKeyRing secretKeyRing = keyBuilder("example", "example@example.com", "password").build();
		PGPSecretKey secretKey = secretKeyRing.iterator().next();
		PGPPrivateKey privateKey = Keys.privateKey(secretKey, "password");

		String fingerPrint1 = fingerPrint(secretKey);
		String fingerPrint2 = fingerPrint(privateKey);

		assertEquals(fingerPrint1, fingerPrint2);
	}
}
