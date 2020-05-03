package ch.rhj.util;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Versions {

	public static <T> VersionComparator<T> comparator(Function<T, String> mapper) {

		return VersionComparator.<T>comparator(mapper);
	}

	public static int compare(String v1, String v2) {

		return comparator(Function.identity()).compare(v1, v2);
	}

	public static <T> int compare(Function<T, String> mapper, T t1, T t2) {

		return comparator(mapper).compare(t1, t2);
	}

	public static <T> Stream<T> sorted(Function<T, String> mapper, Stream<T> input) {

		return input.sorted(comparator(mapper));
	}

	public static <T> Optional<T> latest(Function<T, String> mapper, Stream<T> input) {

		return input.max(comparator(mapper));
	}
}
