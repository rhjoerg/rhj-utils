package ch.rhj.util.config;

import java.util.Locale;
import java.util.Properties;
import java.util.function.Function;

public interface Env {

	public static Function<String, String> keyMapper(String prefix, boolean toUpperCase) {

		if (toUpperCase)
			return s -> prefix + s.toUpperCase(Locale.US);

		return s -> prefix + s;
	}

	public static Properties asProperties(String prefix) {

		Properties properties = new Properties();
		Function<String, String> keyMapper = keyMapper(prefix, Os.isWindows());

		System.getenv().forEach((k, v) -> properties.setProperty(keyMapper.apply(k), v));

		return properties;
	}
}
