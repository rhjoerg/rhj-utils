package ch.rhj.util.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;

public class MapsTests {

	@Test
	public void testMapProperties() {

		Properties properties = new Properties();

		properties.put("foo", "bar");

		Map<String, String> map = Maps.map(properties);

		assertEquals(1, map.size());
		assertEquals("bar", map.get("foo"));
	}
}
