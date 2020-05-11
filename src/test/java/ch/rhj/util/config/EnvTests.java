package ch.rhj.util.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

public class EnvTests {

	@Test
	public void testKeyMapper() {

		Function<String, String> mapper1 = Env.keyMapper("", false);
		Function<String, String> mapper2 = Env.keyMapper("", true);

		assertEquals("foo", mapper1.apply("foo"));
		assertEquals("FOO", mapper2.apply("foo"));
	}

	@Test
	public void testAsProperties() {

		Properties properties = Env.asProperties("env.");

		assertTrue(properties.containsKey("env.PATH"));
	}
}
