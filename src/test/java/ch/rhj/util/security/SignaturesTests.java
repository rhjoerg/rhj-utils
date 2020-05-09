package ch.rhj.util.security;

import static ch.rhj.util.security.KeyBuilder.keyBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ch.rhj.util.test.TestPaths;

public class SignaturesTests implements TestPaths {

	private static final PGPSecretKeyRing secretKeyRing = keyBuilder("example", "example@example.com", "password").build();
	private static final PGPSecretKey secretKey = secretKeyRing.iterator().next();
	private static final PGPPrivateKey privateKey = Keys.privateKey(secretKey, "password");

	private static final Path helloPath = TestPaths.inputPath(SignaturesTests.class, "hello.txt");
	private static final Path signaturePath = TestPaths.outputPath(SignaturesTests.class, "hello.txt.asc");

	@BeforeAll
	public static void setup() {

		Signatures.write(Signatures.sign(helloPath, privateKey), signaturePath, true);
	}

	@Test
	public void testLists() {

		assertEquals(1, Signatures.signatureLists(signaturePath).size());
		assertEquals(1, Signatures.signatures(signaturePath).size());
	}
}
