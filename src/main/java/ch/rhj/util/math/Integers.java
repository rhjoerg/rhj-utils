package ch.rhj.util.math;

import java.math.BigInteger;

public interface Integers {

	public static BigInteger twosComplement(BigInteger original) {

		byte[] contents = original.toByteArray();
		byte[] result = new byte[contents.length + 1];

		System.arraycopy(contents, 0, result, 1, contents.length);
		result[0] = (contents[0] < 0) ? 0 : (byte) -1;

		return new BigInteger(result);
	}
}
