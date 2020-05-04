package ch.rhj.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import ch.rhj.util.Cfg.Builder;

public class CfgTests {

	@Test
	public void testSystemCfg() {

		Cfg cfg = Cfg.builder().includeSystem(true).includeEnv(true).build();

		assertEquals(System.getProperty("user.home"), cfg.get("user.home"));
		assertEquals(System.getenv("PATH"), cfg.get("PATH"));
		assertNull(cfg.get("non-existent-property"));
	}

	@Test
	public void testUserCfg() {

		Cfg cfg = Cfg.builder().prefix("user").includeSystem(true).build();

		assertEquals(System.getProperty("user.home"), cfg.get("home"));
	}

	@Test
	public void testResolver() {

		Properties properties = new Properties();

		properties.put("foo.bar", "${user.home}");
		properties.put("foo.baz", "${user.home");

		Cfg cfg = Cfg.builder().prefix("foo").includeSystem(true).store(properties).build();

		assertEquals(System.getProperty("user.home"), cfg.get("bar"));
		assertEquals("${user.home", cfg.get("baz"));
	}

	@Test
	public void testBuilder() {

		Builder builder = Cfg.builder();

		assertEquals("", builder.prefix());
		assertFalse(builder.includeEnv());
		assertFalse(builder.includeSystem());

		assertNotNull(builder.resolver());
		assertTrue(builder.resolver() == builder.resolver(builder.resolver()).resolver());
		assertEquals(0, builder.store().length);
	}

	@Test
	public void testFixPrefix() {

		assertEquals("", Cfg.fixPrefix(null));
		assertEquals("foo.", Cfg.fixPrefix("foo."));
	}
}
