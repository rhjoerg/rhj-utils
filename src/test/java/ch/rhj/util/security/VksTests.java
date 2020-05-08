package ch.rhj.util.security;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class VksTests {

	// @Test
	public void testGetByFingerprint() throws Exception {

		String fingerprint = "7D4C2F584D416778966CB60E4517E082A2E58156";
		HttpClient httpClient = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder().GET() //
				.uri(URI.create("https://keys.openpgp.org/vks/v1/by-fingerprint/" + fingerprint)).build();

		HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

		assertTrue(response.body().startsWith("-----BEGIN PGP PUBLIC KEY BLOCK-----"));
	}
}
