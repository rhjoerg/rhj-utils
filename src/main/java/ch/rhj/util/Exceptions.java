package ch.rhj.util;

public class Exceptions {

	public static RuntimeException runtime(Throwable t) {

		if (t instanceof RuntimeException)
			return RuntimeException.class.cast(t);

		return new RuntimeException(t);
	}
}
