package ch.rhj.util.io;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ch.rhj.util.SysProps;
import ch.rhj.util.io.IO.Options;

@ExtendWith(MockitoExtension.class)
public class IOTests {

	private static final Path OUTPUT_DIRECTORY = Paths.get("target", "test-data", "IOTests");

	@Test
	public void testOptions() {

		new Options();

		assertEquals(0, Options.copyOptions(false).length);
		assertEquals(1, Options.copyOptions(true).length);
	}

	@Test
	public void testDirectories() {

		Path directory = OUTPUT_DIRECTORY.resolve("directories");
		Path subDirectory = directory.resolve("subdirectory");

		IO.delete(directory);
		IO.createDirectories(subDirectory);
		assertTrue(IO.exists(subDirectory));
		IO.delete(directory);
		assertFalse(IO.exists(directory));

		assertEquals(0, IO.list(null).count());
		assertTrue(IO.list(SysProps.userHome()).count() > 0);

		assertFalse(IO.exists(null));
	}

	@Test
	public void testClasspathPaths() {

		Path path1 = IO.classLoaderPath("/hello.tar");
		Path path2 = IO.classLoaderPath("hello.tar");

		assertEquals(path1, path2);

		Path path3 = IO.classLoaderPath("non-existing-entry");

		assertNull(path3);

		List<Path> paths = IO.classLoaderPaths("META-INF/MANIFEST.MF");

		assertTrue(paths.size() > 1);
	}

	@Test
	public void testCopy() {

		Path outputDirectory = OUTPUT_DIRECTORY.resolve("testCopy");
		Path source = IO.classLoaderPath("io/hello.tar");
		Path target = outputDirectory.resolve("io/hello.tar");

		IO.copy(source, target, true);

		assertTrue(target.toFile().exists());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		IO.copy(source, baos);
		assertEquals(10240, baos.toByteArray().length);
	}

	@Test
	public void testMergedProperties() {

		String fileName = "rhj-io.iotests.properties";

		Path userDirSource = IO.classLoaderPath("io/userdir/" + fileName);
		Path userHomeSource = IO.classLoaderPath("io/userhome/" + fileName);

		Path userDirTarget = SysProps.userDir().resolve(fileName);
		Path userHomeTarget = SysProps.userHome().resolve(fileName);

		try {

			IO.copy(userDirSource, userDirTarget, true);
			IO.copy(userHomeSource, userHomeTarget, true);

			Properties properties = IO.mergedProperties(fileName, new Properties());

			assertEquals("4", properties.getProperty("foo"));
			assertEquals("hello", properties.getProperty("foo1"));
			assertEquals("hello", properties.getProperty("foo2"));
			assertEquals("hello", properties.getProperty("foo3"));
			assertEquals("hello", properties.getProperty("foo4"));

		} finally {

			IO.delete(userDirTarget);
			IO.delete(userHomeTarget);
		}
	}

	@Test
	public void testAllProperties() {

		List<Properties> all = IO.allProperties("rhj-io.iotests.properties");

		assertEquals(2, all.size());
		assertEquals("2", all.get(0).getProperty("foo"));
	}

	@Test
	public void testReadString() {

		Path path = IO.classLoaderPath("io/string.txt");

		String expected = "hello, world!";
		String actual = IO.readString(path, UTF_8);

		assertEquals(expected, actual);

		actual = IO.applyToInput(() -> Files.newInputStream(path), i -> IO.readString(i, UTF_8));

		assertEquals(expected, actual);
	}

	@Test
	public void testReadLines() {

		Path path = IO.classLoaderPath("io/lines.txt");

		List<String> expected = Arrays.asList("line 1", "", "line 3");
		List<String> actual = IO.readLines(path, UTF_8);

		assertEquals(expected, actual);
	}

	@Mock
	private HttpClient httpClient;

	@Mock
	private HttpResponse<byte[]> httpBytesResponse;

	@Mock
	private HttpResponse<String> httpStringResponse;

	@Test
	public void testReadFromRequest() throws Exception {

		String string = "hello";
		byte[] bytes = string.getBytes();

		HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("https://www.example.com")).build();

		when(httpClient.send(request, BodyHandlers.ofByteArray())).thenReturn(httpBytesResponse);
		when(httpClient.send(request, BodyHandlers.ofString())).thenReturn(httpStringResponse);

		when(httpBytesResponse.statusCode()).thenReturn(200);
		when(httpBytesResponse.body()).thenReturn(bytes);

		when(httpStringResponse.statusCode()).thenReturn(200);
		when(httpStringResponse.body()).thenReturn(string);

		assertArrayEquals(bytes, IO.read(httpClient, request));
		assertEquals(string, IO.readString(httpClient, request));

		when(httpBytesResponse.statusCode()).thenReturn(400);

		assertNull(IO.read(httpClient, request));
	}

	@Test
	public void testFind() {

		Path root = SysProps.userHome();

		long count1 = IO.findFiles(root, false, p -> p.toUri().toASCIIString().endsWith(".asc"), 2).count();
		long count2 = IO.findFilesWithExtension(root, false, ".asc", 2).count();
		long count3 = IO.findFilesWithExtension(root, false, "asc", 2).count();

		assertEquals(count1, count2);
		assertEquals(count2, count3);
	}
}
