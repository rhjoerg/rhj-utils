package ch.rhj.util.security;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;

public class ProvidersTests {

	@Test
	public void testProviders() {

		assertTrue(Providers.names().contains("BC"));
		assertTrue(Providers.provider("BC") instanceof BouncyCastleProvider);
	}
}
