package ch.rhj.util;

import static java.util.stream.Collectors.toList;

import java.util.List;
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

	public static <T> List<T> list(Iterable<T> iterable) {

		return stream(iterable).collect(toList());
	}
}
