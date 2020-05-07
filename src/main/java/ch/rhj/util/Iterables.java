package ch.rhj.util;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Iterables {

	@SuppressWarnings("unchecked")
	public static <T> Iterable<T> iterable(@SuppressWarnings("rawtypes") Iterable iterable) {

		return (Iterable<T>) iterable;
	}

	public static <T> Stream<T> stream(Iterable<T> iterable) {

		return StreamSupport.stream(iterable.spliterator(), false);
	}
}
