package ch.rhj.util.security;

import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

import ch.rhj.util.Ex;

public interface Pass {

	public static PBESecretKeyDecryptor secretKeyDecryptor(String password) {

		return Ex.supply(() -> new JcePBESecretKeyDecryptorBuilder().setProvider(Providers.bc()).build(password.toCharArray()));
	}
}
