package ch.rhj.util.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class IterablesTests {

	@Test
	public void testCast() {

		List<Integer> list = Arrays.asList(1, 2, 3);
		@SuppressWarnings("rawtypes")
		Iterable uncheckedIterable = list;
		Iterable<Integer> iterable = Iterables.iterable(uncheckedIterable);

		assertTrue(list == iterable);
	}

	@Test
	public void testStream() {

		List<Integer> list = Arrays.asList(1, 2, 3);

		assertEquals(3, Iterables.stream(list).max((x, y) -> Integer.compare(x, y)).get());
	}

	@Test
	public void testList() {

		List<Integer> expected = Arrays.asList(1, 2, 3);
		List<Integer> actual = Iterables.list(expected);

		assertEquals(expected, actual);
	}
}
