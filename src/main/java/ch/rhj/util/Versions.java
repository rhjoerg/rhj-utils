package ch.rhj.util;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.maven.artifact.versioning.ComparableVersion;

public interface Versions {

	public static class Comparator<T> implements java.util.Comparator<T> {

		private final Function<T, String> mapper;

		public Comparator(Function<T, String> mapper) {

			this.mapper = mapper;
		}

		@Override
		public int compare(T o1, T o2) {

			ComparableVersion v1 = new ComparableVersion(mapper.apply(o1));
			ComparableVersion v2 = new ComparableVersion(mapper.apply(o2));

			return v1.compareTo(v2);
		}

		public static <T> Comparator<T> comparator(Function<T, String> mapper) {

			return new Comparator<T>(mapper);
		}
	}

	public static <T> Versions.Comparator<T> comparator(Function<T, String> mapper) {

		return Comparator.<T>comparator(mapper);
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
