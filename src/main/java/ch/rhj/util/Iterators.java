package ch.rhj.util;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Iterators {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Iterator<T> iterator(Iterator iterator) {

		return (Iterator<T>) iterator;
	}

	public static <T> Stream<T> stream(Iterator<T> iterator) {

		return StreamSupport.stream(spliteratorUnknownSize(iterator, 0), false);
	}

	public static <T> List<T> list(Iterator<T> iterator) {

		return stream(iterator).collect(toList());
	}
}
