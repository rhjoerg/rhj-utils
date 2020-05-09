package ch.rhj.util.security;

import static ch.rhj.util.security.KeyBuilder.keyBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ch.rhj.util.test.TestPaths;

public class AscTests implements TestPaths {

	private static final PGPSecretKeyRing secretKeyRing = keyBuilder("example", "example@example.com", "password").build();
	private static final Path keyRingPath = TestPaths.outputPath(AscTests.class, "AscTests.asc");

	@BeforeAll
	public static void setup() {

		Keys.write(secretKeyRing, keyRingPath, true);
	}

	@Test
	public void testObjects() {

		assertEquals(1, Asc.objects(keyRingPath).size());
		assertEquals(1, Asc.objects(keyRingPath, PGPSecretKeyRing.class).size());
	}

	@Test
	public void testWrite() {

		Path path = outputPath("AscTests.testWrite.asc");

		Asc.write(path, true, o -> secretKeyRing.encode(o));
	}
}
