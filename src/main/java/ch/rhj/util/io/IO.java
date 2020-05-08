package ch.rhj.util.io;

import static ch.rhj.util.Threads.contextClassLoader;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Stream;

import ch.rhj.util.Ex;
import ch.rhj.util.SysProps;
import ch.rhj.util.function.ThrowingConsumer;
import ch.rhj.util.function.ThrowingFunction;
import ch.rhj.util.function.ThrowingSupplier;

public interface IO {

	public static class Options {

		private final static CopyOption[] NO_COPY_OPTIONS = {};
		private final static CopyOption[] REPLACE_COPY_OPTIONS = { StandardCopyOption.REPLACE_EXISTING };

		public static CopyOption[] copyOptions(boolean replace) {

			return (replace ? REPLACE_COPY_OPTIONS : NO_COPY_OPTIONS).clone();
		}

		private final static OpenOption[] NO_OPEN_OPTIONS = {};
		private final static OpenOption[] REPLACE_OPEN_OPTIONS = { StandardOpenOption.CREATE_NEW };

		public static OpenOption[] openOptions(boolean replace) {

			return (replace ? REPLACE_OPEN_OPTIONS : NO_OPEN_OPTIONS).clone();
		}
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static ThrowingSupplier<InputStream, IOException> inputStream(Path path) {

		return () -> Files.newInputStream(path);
	}

	public static ThrowingSupplier<OutputStream, IOException> outputStream(Path path, boolean replace) {

		return () -> {

			if (replace && exists(path))
				delete(path);

			createDirectories(path.getParent());

			return Files.newOutputStream(path, Options.openOptions(replace));
		};
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static boolean exists(Path path) {

		return path != null && Files.exists(path);
	}

	public static boolean isDirectory(Path path) {

		return path != null && Files.isDirectory(path);
	}

	public static Stream<Path> list(Path directory) {

		if (!isDirectory(directory))
			return Stream.empty();

		return Ex.supply(() -> Files.list(directory));
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static void delete(Path path) {

		if (!exists(path))
			return;

		if (isDirectory(path)) {

			Ex.apply(Files::walk, path).filter(p -> !p.equals(path)).forEach(p -> Ex.consume(IO::delete, p));
			Ex.consume(Files::delete, path);

		} else {

			Ex.consume(Files::delete, path);
		}
	}

	public static void createDirectories(Path directory) {

		Ex.consume(Files::createDirectories, directory);
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static <T extends InputStream> void consumeInput(ThrowingSupplier<T, ? extends Throwable> open,
			ThrowingConsumer<? super T, ? extends Throwable> consumer) {

		Ex.run(() -> {

			try (T input = open.get()) {

				consumer.accept(input);
			}
		});
	}

	public static <T extends OutputStream> void consumeOutput(ThrowingSupplier<T, ? extends Throwable> open,
			ThrowingConsumer<? super T, ? extends Throwable> consumer) {

		Ex.run(() -> {

			try (T output = open.get()) {

				consumer.accept(output);
			}
		});
	}

	public static <I extends InputStream, T> T applyToInput(ThrowingSupplier<I, ? extends Throwable> open,
			ThrowingFunction<? super I, T, ? extends Throwable> function) {

		return Ex.supply(() -> {

			try (I input = open.get()) {

				return function.apply(input);
			}
		});
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static void copy(InputStream source, OutputStream target) {

		byte[] buffer = new byte[0x10000];
		AtomicInteger length = new AtomicInteger();

		length.set(Ex.apply(source::read, buffer));

		while (length.get() > 0) {

			Ex.run(() -> target.write(buffer, 0, length.get()));
			length.set(Ex.apply(source::read, buffer));
		}
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static byte[] read(InputStream input) {

		ByteArrayOutputStream output = new ByteArrayOutputStream();

		copy(input, output);

		return output.toByteArray();
	}

	public static byte[] read(Path path) {

		return applyToInput(inputStream(path), IO::read);
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static void copy(InputStream source, Path target, boolean replace) {

		consumeOutput(outputStream(target, replace), o -> copy(source, o));
	}

	public static void copy(Path source, OutputStream target) {

		consumeInput(inputStream(source), i -> copy(i, target));
	}

	public static void copy(Path source, Path target, boolean replace) {

		consumeInput(inputStream(source), i -> copy(i, target, replace));
	}

	public static FileSystem fileSystem(URL url) {

		String protocol = url.getProtocol();

		if ("file".equals(protocol))
			return FileSystems.getDefault();

		if (!"jar".equals(protocol))
			return FileSystems.getFileSystem(Ex.apply(URL::toURI, url));

		FileSystemProvider zfsp = FileSystemProvider.installedProviders().stream() //
				.filter(p -> "jar".equals(p.getScheme())) //
				.findFirst().get();

		FileSystem fs = null;
		URI uri = URI.create(url.toExternalForm());

		try {

			fs = zfsp.getFileSystem(URI.create(url.toExternalForm()));

		} catch (FileSystemNotFoundException e) {

			Map<String, String> env = Map.of("create", "true");
			fs = Ex.supply(() -> FileSystems.newFileSystem(uri, env));
		}

		return fs;
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static URI toUri(URL url) {

		fileSystem(url);

		return Ex.apply(URL::toURI, url);
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static List<Path> classLoaderPaths(ClassLoader classLoader, String name) {

		if (name.startsWith("/"))
			name = name.substring(1);

		return classLoader.resources(name).map(IO::toUri).map(Paths::get).collect(toList());
	}

	public static List<Path> classLoaderPaths(String name) {

		return classLoaderPaths(contextClassLoader(), name);
	}

	public static Path classLoaderPath(ClassLoader classLoader, String name) {

		List<Path> paths = classLoaderPaths(classLoader, name);

		return paths.isEmpty() ? null : paths.get(0);
	}

	public static Path classLoaderPath(String name) {

		return classLoaderPath(contextClassLoader(), name);
	}

	public static byte[] readResource(ClassLoader classLoader, String name) {

		return read(classLoaderPath(classLoader, name));
	}

	public static byte[] readResource(String name) {

		return readResource(contextClassLoader(), name);
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static Properties properties(Path path, Properties properties) {

		consumeInput(inputStream(path), i -> properties.load(i));

		return properties;
	}

	public static enum PropertyPathsDirection {

		METAINF_TO_USERDIR, USERDIR_TO_METAINF;
	}

	public static List<Path> propertyPaths(ClassLoader classLoader, String name, PropertyPathsDirection direction) {

		ArrayList<Path> paths = new ArrayList<>();

		paths.addAll(classLoaderPaths(classLoader, "META-INF/" + name));
		paths.addAll(classLoaderPaths(classLoader, name));

		Path userHomePath = SysProps.userHome().resolve(name);
		Path userDirPath = SysProps.userDir().resolve(name);

		if (exists(userHomePath))
			paths.add(userHomePath);

		if (exists(userDirPath))
			paths.add(userDirPath);

		if (direction == PropertyPathsDirection.USERDIR_TO_METAINF) {

			Collections.reverse(paths);
		}

		return paths;
	}

	public static Properties mergedProperties(ClassLoader classLoader, String name, Properties properties) {

		propertyPaths(classLoader, name, PropertyPathsDirection.METAINF_TO_USERDIR).stream().forEach(p -> properties(p, properties));

		return properties;
	}

	public static Properties mergedProperties(String name, Properties properties) {

		return mergedProperties(contextClassLoader(), name, properties);
	}

	public static List<Properties> allProperties(ClassLoader classLoader, String name) {

		return propertyPaths(classLoader, name, PropertyPathsDirection.USERDIR_TO_METAINF) //
				.stream().map(p -> properties(p, new Properties())).collect(toList());
	}

	public static List<Properties> allProperties(String name) {

		return allProperties(contextClassLoader(), name);
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static <T> HttpResponse<T> httpResponse(HttpClient httpClient, HttpRequest request, BodyHandler<T> bodyHandler) {

		return Ex.supply(() -> httpClient.send(request, bodyHandler));
	}

	public static <T> T httpResponseBody(HttpResponse<T> response) {

		if (response.statusCode() > 399) {

			Logger.getLogger("ch.rhj.io").fine(response.toString());

			return null;
		}

		return response.body();
	}

	public static <T> T read(HttpClient httpClient, HttpRequest request, BodyHandler<T> bodyHandler) {

		return httpResponseBody(httpResponse(httpClient, request, bodyHandler));
	}

	public static byte[] read(HttpClient httpClient, HttpRequest request) {

		return read(httpClient, request, BodyHandlers.ofByteArray());
	}

	public static String readString(HttpClient httpClient, HttpRequest request) {

		return read(httpClient, request, BodyHandlers.ofString());
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static void write(InputStream input, Path target, boolean replace) {

		copy(input, target, replace);
	}

	public static void write(byte[] bytes, Path target, boolean replace) {

		write(new ByteArrayInputStream(bytes), target, replace);
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static String readString(InputStream input, Charset charset) {

		return new String(read(input), charset);
	}

	public static String readString(Path path, Charset charset) {

		return new String(read(path), charset);
	}

	public static List<String> readLines(InputStream input, Charset charset) {

		return Ex.supply(() -> {

			try (Reader reader = new InputStreamReader(input, charset)) {

				try (BufferedReader bufferedReader = new BufferedReader(reader)) {

					return bufferedReader.lines().collect(toList());
				}
			}
		});
	}

	public static List<String> readLines(Path path, Charset charset) {

		return applyToInput(inputStream(path), i -> readLines(i, charset));
	}
}
