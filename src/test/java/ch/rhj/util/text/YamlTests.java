package ch.rhj.util.text;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class YamlTests {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Hello {

		@JsonProperty("hello")
		public String hello;
	}

	@Test
	public void testRead() throws Exception {

		URL url = getClass().getClassLoader().getResource("hello.yaml");
		Path path = Path.of(url.toURI());
		byte[] bytes = Files.readAllBytes(path);
		Hello hello = Yaml.read(bytes, Hello.class);

		assertEquals("hello, world!", hello.hello);
	}
}
