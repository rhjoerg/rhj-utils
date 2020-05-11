package ch.rhj.util.config;

import java.util.Locale;

public interface Os {

	public static final String FAMILY_MAC = "mac";
	public static final String FAMILY_UNIX = "unix";
	public static final String FAMILY_WINDOWS = "windows";

	public static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.US);

	public static final String PATH_SEP = System.getProperty("path.separator");

	public static boolean isOs(String family) {

		if (family == null)
			return false;

		boolean isFamily = true;

		if (FAMILY_MAC.equalsIgnoreCase(family)) {

			isFamily = OS_NAME.contains(FAMILY_MAC);

		} else if (FAMILY_UNIX.equalsIgnoreCase(family)) {

			isFamily = PATH_SEP.equals(":") && (!isFamily(FAMILY_MAC) || OS_NAME.endsWith("x"));

		} else if (FAMILY_WINDOWS.equalsIgnoreCase(family)) {

			isFamily = OS_NAME.contains(FAMILY_WINDOWS);

		} else {

			isFamily = OS_NAME.contains(family.toLowerCase(Locale.US));
		}

		return isFamily;
	}

	public static boolean isFamily(String family) {

		return isOs(family);
	}

	public static boolean isMac() {

		return isOs(FAMILY_MAC);
	}

	public static boolean isUnix() {

		return isOs(FAMILY_UNIX);
	}

	public static boolean isWindows() {

		return isOs(FAMILY_WINDOWS);
	}
}
