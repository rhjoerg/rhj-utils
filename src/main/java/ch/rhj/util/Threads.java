package ch.rhj.util;

public interface Threads {

	public static ClassLoader contextClassLoader() {

		return Thread.currentThread().getContextClassLoader();
	}
}
