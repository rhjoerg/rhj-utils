package ch.rhj.util.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ch.rhj.util.config.Cfg.Builder;

public class CfgTests {

	@Test
	public void testSystemCfg() {

		Cfg cfg = Cfg.system();

		assertEquals(System.getProperty("user.home"), cfg.get("user.home"));
		assertEquals(System.getenv("PATH"), cfg.get("PATH"));
		assertNull(cfg.get("non-existent-property"));
	}

	@Test
	public void testUserCfg() {

		Cfg cfg = Cfg.builder().prefix("user").system(true).build();

		assertEquals(System.getProperty("user.home"), cfg.get("home"));
	}

	@Test
	public void testResolver() {

		Properties properties = new Properties();

		properties.put("foo.bar", "${user.home}");
		properties.put("foo.baz", "${user.home");

		Cfg cfg = Cfg.builder().prefix("foo").system(true).store(properties).build();

		assertEquals(System.getProperty("user.home"), cfg.get("bar"));
		assertEquals("${user.home", cfg.get("baz"));
	}

	@Test
	public void testBuilder() {

		Builder builder = Cfg.builder();

		assertEquals("", builder.prefix());
		assertFalse(builder.system());

		assertNotNull(builder.resolver());
		assertTrue(builder.resolver() == builder.resolver(builder.resolver()).resolver());
		assertEquals(0, builder.store().length);
	}

	@Test
	public void testFixPrefix() {

		assertEquals("", Cfg.fixPrefix(null));
		assertEquals("foo.", Cfg.fixPrefix("foo."));
	}

	@Test
	public void testSub() {

		Properties properties = new Properties();

		properties.put("foo.bar.baz", "foobar");

		Cfg cfg = Cfg.builder().store(properties).build();
		Cfg sub0 = cfg.sub("");
		Cfg sub1 = cfg.sub("foo");
		Cfg sub2 = sub1.sub("bar");

		assertEquals("foobar", sub0.get("foo.bar.baz"));
		assertEquals("foobar", sub1.get("bar.baz"));
		assertEquals("foobar", sub2.get("baz"));
	}

	@Test
	public void testKeysAndValues() {

		Cfg cfg;
		Set<String> keys;

		cfg = Cfg.cfg("user", true);
		keys = cfg.keys();
		assertTrue(keys.contains("home"));

		Properties ps = new Properties();

		ps.setProperty("foo.bar", "foobar");

		cfg = Cfg.cfg("foo", false, Arrays.asList(ps));
		keys = cfg.keys();

		assertTrue(keys.contains("bar"));

		Set<String> expected = Set.of("foobar");

		assertEquals(expected, cfg.values());
	}
}
