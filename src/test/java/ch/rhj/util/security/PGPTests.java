package ch.rhj.util.security;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPUtil;
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

		assertEquals(2, PGP.secretKeys(keyPath).size());
		assertTrue(PGP.secretKey(keyPath, fingerPrint).isPresent());
		assertEquals(fingerPrint, PGP.fingerPrint(PGP.privateKey(keyPath, fingerPrint, password).get()));
	}

	@Test
	public void testLists() {

		assertEquals(1, PGP.objects(sigPath).size());
		assertEquals(1, PGP.objects(sigPath, PGPSignatureList.class).size());
		assertEquals(1, PGP.secretKeyRings(keyPath).size());
		assertEquals(1, PGP.signatureLists(sigPath).size());
	}

	@Test
	public void testSignatures() {

		List<PGPSignature> signatures = PGP.signatures(sigPath);

		assertEquals(1, signatures.size());
		assertEquals(PGPUtil.SHA256, signatures.get(0).getHashAlgorithm());
	}

	@Test
	public void testSign() throws IOException {

		PGPPrivateKey privateKey = PGP.privateKey(keyPath, fingerPrint, password).get();
		PGPSignature signature = PGP.sign(pomPath, privateKey);
		Path path = Paths.get("target", "test-data", "PGPTests", "pom.asc");

		PGP.write(signature, path, true);

		PGPSignature signature2 = PGP.signatures(path).get(0);

		assertArrayEquals(signature.getEncoded(), signature2.getEncoded());
	}

	@Test
	public void experiment() throws Exception {
	}
}
