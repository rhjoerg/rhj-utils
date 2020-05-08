package ch.rhj.util.security;

import static ch.rhj.util.security.KeyBuilder.keyBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.junit.jupiter.api.Test;

import ch.rhj.util.collection.Iterables;

public class KeyBuilderTests {

	@Test
	public void test() {

		PGPSecretKeyRing secretKeyRing = keyBuilder("example", "example@example.com", "password").build();

		assertEquals(1, Iterables.list(secretKeyRing).size());
	}
}
