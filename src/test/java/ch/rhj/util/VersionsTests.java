package ch.rhj.util;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

public class VersionsTests {

	public static class TestVersion {

		public final String version;

		public TestVersion(String version) {

			this.version = version;
		}
	}

	private TestVersion testVersion(String version) {

		return new TestVersion(version);
	}

	private final Function<TestVersion, String> mapper = v -> v.version;

	@Test
	public void testStringVersions() {

		String v1 = "1.0.0";
		String v2 = "1.1.0";
		String v3 = "1.10.0";

		assertTrue(Versions.compare(v1, v2) < 0);
		assertTrue(Versions.compare(v2, v3) < 0);

		String v4 = v1 + "-snapshot";

		assertTrue(Versions.compare(v4, v1) < 0);

		String v5 = v1 + "-alpha";

		assertTrue(Versions.compare(v5, v1) < 0);
	}

	@Test
	public void testComplexVersions() {

		TestVersion v1 = testVersion("1.0.0");
		TestVersion v2 = testVersion("1.1.0");
		TestVersion v3 = testVersion("1.1.0-alpha");

		assertTrue(Versions.compare(mapper, v1, v2) < 0);

		List<TestVersion> versions = Arrays.asList(v1, v2, v3);
		List<TestVersion> expected = Arrays.asList(v1, v3, v2);
		List<TestVersion> actual = Versions.sorted(mapper, versions.stream()).collect(toList());

		assertEquals(expected, actual);

		assertTrue(v2 == Versions.latest(mapper, versions.stream()).get());
	}
}
