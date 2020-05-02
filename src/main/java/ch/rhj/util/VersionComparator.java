package ch.rhj.util;

import java.util.Comparator;
import java.util.function.Function;

import org.apache.maven.artifact.versioning.ComparableVersion;

public class VersionComparator<T> implements Comparator<T> {

	private final Function<T, String> mapper;

	public VersionComparator(Function<T, String> mapper) {

		this.mapper = mapper;
	}

	@Override
	public int compare(T o1, T o2) {

		ComparableVersion v1 = new ComparableVersion(mapper.apply(o1));
		ComparableVersion v2 = new ComparableVersion(mapper.apply(o2));

		return v1.compareTo(v2);
	}

	public static <T> VersionComparator<T> comparator(Function<T, String> mapper) {

		return new VersionComparator<T>(mapper);
	}
}
