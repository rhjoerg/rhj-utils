package ch.rhj.util;

import java.util.function.Consumer;

public interface Consumers {

	public static <T> Consumer<T> empty() {

		return t -> {
		};
	}
}
