package ch.rhj.util.text;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.rhj.util.Ex;

public interface Json {

	public static <T> T read(ObjectMapper mapper, byte[] bytes, Class<T> type) {

		return Ex.biApply(mapper::readValue, bytes, type);
	}

	public static <T> T read(byte[] bytes, Class<T> type) {

		return read(new ObjectMapper(), bytes, type);
	}
}
