package ch.rhj.util;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;

public class Cfg implements BiFunction<String, String, String> {

	public static class Resolver {

		public static class Token {

			public final String content;
			public final boolean isExpression;

			public Token(String content, boolean isExpression) {

				this.content = content;
				this.isExpression = isExpression;
			}

			public String resolve(BiFunction<String, String, String> valueProvider) {

				if (!isExpression)
					return content;

				return String.valueOf(valueProvider.apply(content, null));
			}
		}

		public List<Token> tokens(String value) {

			List<Token> tokens = new ArrayList<>();
			int length = value.length();
			int position = 0;

			while (position < length) {

				int start = value.indexOf("${", position);

				if (start < 0) {

					tokens.add(new Token(value.substring(position), false));
					break;
				}

				int end = value.indexOf("}", position);

				if (end < 0) {

					tokens.add(new Token(value.substring(position), false));
					break;
				}

				tokens.add(new Token(value.substring(start + 2, end), true));
				position = end + 1;
			}

			return tokens;
		}

		public String resolve(String value, BiFunction<String, String, String> valueProvider) {

			return tokens(value).stream().map(t -> t.resolve(valueProvider)).collect(joining());
		}
	}

	public static class Builder {

		private String prefix = "";
		private boolean system = false;
		private Resolver resolver = new Resolver();
		private final ArrayList<Properties> store = new ArrayList<>();

		public String prefix() {

			return prefix;
		}

		public Builder prefix(String prefix) {

			this.prefix = prefix;
			return this;
		}

		public boolean system() {

			return system;
		}

		public Builder system(boolean system) {

			this.system = system;
			return this;
		}

		public Resolver resolver() {

			return resolver;
		}

		public Builder resolver(Resolver resolver) {

			this.resolver = resolver;
			return this;
		}

		public Properties[] store() {

			return store.toArray(Properties[]::new);
		}

		public Builder store(Properties... store) {

			this.store.clear();
			this.store.addAll(Arrays.asList(store));
			return this;
		}

		public Cfg build() {

			return new Cfg(prefix, system, resolver, store);
		}
	}

	public static Builder builder() {

		return new Builder();
	}

	public final Cfg root;

	public final String prefix;
	public final boolean system;
	public final Resolver resolver;

	private final ArrayList<Properties> store = new ArrayList<>();

	public Cfg(String prefix, boolean system, Resolver resolver, Collection<? extends Properties> store) {

		this.prefix = fixPrefix(prefix);
		this.system = system;
		this.resolver = resolver;

		if (this.prefix.isEmpty()) {

			this.root = null;
			this.store.addAll(store);

		} else {

			this.root = new Cfg("", system, resolver, store);
		}
	}

	public Cfg(Cfg cfg, String prefix) {

		this.prefix = fixPrefix(cfg.prefix + prefix);
		this.system = cfg.system;
		this.resolver = cfg.resolver;

		if (this.prefix.isEmpty()) {

			this.root = null;
			this.store.addAll(cfg.store);

		} else {

			if (cfg.root == null) {

				this.root = new Cfg("", system, resolver, cfg.store);

			} else {

				this.root = cfg.root;
			}
		}
	}

	public String resolve(String value) {

		if (value == null)
			return value;

		return resolver.resolve(value, this.root == null ? this : this.root);
	}

	public String get(String key, String defaultValue) {

		String prefixedKey = prefix + key;

		if (root != null) {

			return root.get(prefixedKey, defaultValue);
		}

		if (system) {

			String result;

			if ((result = System.getProperty(prefixedKey)) != null)
				return resolve(result);

			if ((result = System.getenv(prefixedKey)) != null)
				return resolve(result);
		}

		return resolve(store.stream().filter(p -> p.containsKey(prefixedKey)).findFirst() //
				.map(p -> p.getProperty(prefixedKey)).orElse(defaultValue));
	}

	public String get(String key) {

		return get(key, null);
	}

	public Set<String> keys() {

		if (root == null) {

			Set<String> keys = new TreeSet<>();

			if (system) {

				keys.addAll(System.getProperties().stringPropertyNames());
				keys.addAll(System.getenv().keySet());
			}

			store.stream().map(s -> s.stringPropertyNames()).forEach(s -> keys.addAll(s));

			return keys;

		} else {

			int length = prefix.length();

			return root.keys().stream().filter(s -> s.startsWith(prefix)).map(s -> s.substring(length)).collect(toSet());
		}
	}

	public Set<String> values() {

		return keys().stream().map(this::get).collect(toSet());
	}

	public Cfg sub(String prefix) {

		return new Cfg(this, prefix);
	}

	@Override
	public String apply(String key, String defaultValue) {

		return get(key, defaultValue);
	}

	public static String fixPrefix(String prefix) {

		if (prefix == null)
			prefix = "";

		prefix = prefix.trim();

		if (prefix.isBlank())
			return prefix;

		if (!prefix.endsWith("."))
			prefix += ".";

		return prefix;
	}

	public static Cfg cfg(String prefix, boolean system, Properties... store) {

		return builder().prefix(prefix).system(system).store(store).build();
	}

	public static Cfg cfg(String prefix, boolean system, Collection<? extends Properties> store) {

		return cfg(prefix, system, store.toArray(Properties[]::new));
	}

	public static Cfg system() {

		return cfg("", true);
	}
}
