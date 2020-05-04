package ch.rhj.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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

			return tokens(value).stream().map(t -> t.resolve(valueProvider)).collect(Collectors.joining());
		}
	}

	public static class Builder {

		private String prefix = "";
		private boolean includeSystem = false;
		private boolean includeEnv = false;
		private Resolver resolver = new Resolver();
		private final ArrayList<Properties> store = new ArrayList<>();

		public String prefix() {

			return prefix;
		}

		public Builder prefix(String prefix) {

			this.prefix = prefix;
			return this;
		}

		public boolean includeSystem() {

			return includeSystem;
		}

		public Builder includeSystem(boolean includeSystem) {

			this.includeSystem = includeSystem;
			return this;
		}

		public boolean includeEnv() {

			return includeEnv;
		}

		public Builder includeEnv(boolean includeEnv) {

			this.includeEnv = includeEnv;
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

			return new Cfg(prefix, includeSystem, includeEnv, resolver, store);
		}
	}

	public static Builder builder() {

		return new Builder();
	}

	public final Cfg root;

	public final String prefix;
	public final boolean includeSystem;
	public final boolean includeEnv;
	public final Resolver resolver;

	private final LinkedList<Properties> store;

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

	public Cfg(String prefix, boolean includeSystem, boolean includeEnv, Resolver resolver, Collection<? extends Properties> store) {

		this.prefix = fixPrefix(prefix);
		this.includeSystem = includeSystem;
		this.includeEnv = includeEnv;
		this.resolver = resolver;
		this.store = new LinkedList<>();

		if (this.prefix.isEmpty()) {

			this.root = null;
			this.store.addAll(store);

		} else {

			this.root = new Cfg("", includeSystem, includeEnv, resolver, store);
		}
	}

	public String resolve(String value) {

		return resolver.resolve(value, this.root == null ? this : this.root);
	}

	public String get(String key, String defaultValue) {

		String prefixedKey = prefix + key;

		if (root != null) {

			return root.get(prefixedKey, defaultValue);
		}

		if (includeSystem) {

			String result = System.getProperty(prefixedKey);

			if (result != null)
				return resolve(result);
		}

		if (includeEnv) {

			String result = System.getenv(prefixedKey);

			if (result != null)
				return resolve(result);
		}

		Optional<Properties> properties = store.stream().filter(p -> p.containsKey(prefixedKey)).findFirst();

		if (properties.isEmpty())
			return defaultValue;

		return resolve(properties.get().getProperty(prefixedKey));
	}

	public String get(String key) {

		return get(key, null);
	}

	@Override
	public String apply(String key, String defaultValue) {

		return get(key, defaultValue);
	}
}
