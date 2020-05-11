package ch.rhj.util.collection;

import static java.util.Map.entry;

import java.util.Properties;
import java.util.TreeMap;

public interface Maps {

	public static TreeMap<String, String> map(Properties properties) {

		TreeMap<String, String> result = new TreeMap<>();

		properties.entrySet().stream() //
				.map(e -> entry(String.valueOf(e.getKey()), String.valueOf(e.getValue()))) //
				.forEach(e -> result.put(e.getKey(), e.getValue()));

		return result;
	}
}
