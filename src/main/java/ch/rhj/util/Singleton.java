package ch.rhj.util;

import java.util.function.Supplier;

public class Singleton<T> implements Supplier<T> {

	private final Supplier<? extends T> creator;
	private T value;

	public Singleton(Supplier<? extends T> creator) {

		this.creator = creator;
	}

	@Override
	public synchronized T get() {

		if (value == null) {

			value = creator.get();
		}

		return value;
	}

	public synchronized boolean hasValue() {

		return value != null;
	}

	public static <T> Singleton<T> singleton(Supplier<? extends T> creator) {

		return new Singleton<T>(creator);
	}
}
