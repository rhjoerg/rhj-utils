package ch.rhj.util.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

public class IteratorsTests {

	@Test
	public void testCast() {

		List<Integer> list = Arrays.asList(1, 2, 3);
		@SuppressWarnings("rawtypes")
		Iterator rawIterator = list.iterator();
		Iterator<Integer> iterator = Iterators.iterator(rawIterator);

		assertTrue(iterator == rawIterator);
	}

	@Test
	public void testStream() {

		List<Integer> list = Arrays.asList(1, 2, 3);

		assertEquals(3, Iterators.stream(list.iterator()).max((x, y) -> Integer.compare(x, y)).get());
	}

	@Test
	public void testList() {

		List<Integer> expected = Arrays.asList(1, 2, 3);
		List<Integer> actual = Iterators.list(expected.iterator());

		assertEquals(expected, actual);
	}
}
