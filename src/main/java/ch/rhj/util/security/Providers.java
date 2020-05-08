package ch.rhj.util.security;

import static java.util.stream.Collectors.toSet;

import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Stream;

public interface Providers {

	public static void installFromServices() {

		Set<Class<? extends Provider>> installedTypes = Stream.of(Security.getProviders()).map(p -> p.getClass()).collect(toSet());

		ServiceLoader.load(Provider.class).stream() //
				.filter(p -> !installedTypes.contains(p.type())) //
				.forEach(p -> Security.addProvider(p.get()));
	}

	public static List<Provider> installed() {

		installFromServices();

		return Arrays.asList(Security.getProviders());
	}

	public static Set<String> names() {

		return installed().stream().map(p -> p.getName()).collect(toSet());
	}

	public static Provider provider(String name) {

		installFromServices();

		return Security.getProvider(name);
	}

	public static Provider bc() {

		return provider("BC");
	}
}
