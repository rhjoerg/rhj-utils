package ch.rhj.util.text;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public interface Yaml {

	public static <T> T read(byte[] bytes, Class<T> type) {

		return Json.read(new ObjectMapper(new YAMLFactory()), bytes, type);
	}
}
