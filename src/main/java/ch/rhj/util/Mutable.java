package ch.rhj.util;

public class Mutable<T> {

	private T value;

	public T get() {

		return value;
	}

	public void set(T value) {

		this.value = value;
	}

	public static <T> Mutable<T> empty() {

		return new Mutable<T>();
	}
}
