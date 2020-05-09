package ch.rhj.util.security;

import static ch.rhj.util.security.FingerPrints.fingerPrint;
import static ch.rhj.util.security.KeyBuilder.keyBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ch.rhj.util.test.TestPaths;

public class KeysTests implements TestPaths {

	private static final PGPSecretKeyRing secretKeyRing = keyBuilder("example", "example@example.com", "password").build();
	private static final Path keyRingPath = TestPaths.outputPath(KeysTests.class, "KeysTests.asc");

	private static final PGPSecretKey secretKey = secretKeyRing.iterator().next();

	@BeforeAll
	public static void setup() {

		Keys.write(secretKeyRing, keyRingPath, true);
	}

	@Test
	public void testLists() {

		assertEquals(1, Keys.secretKeyRings(keyRingPath).size());
		assertEquals(1, Keys.secretKeys(keyRingPath).size());
	}

	@Test
	public void testSecretKey() {

		String fingerPrint = fingerPrint(secretKey);

		assertTrue(Keys.secretKey(keyRingPath, fingerPrint).isPresent());
	}

	@Test
	public void testPrivateKey() {

		String fingerPrint = fingerPrint(secretKey);

		assertTrue(Keys.privateKey(keyRingPath, fingerPrint, "password").isPresent());
	}
}
