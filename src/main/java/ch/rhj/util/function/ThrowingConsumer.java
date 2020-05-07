package ch.rhj.util.function;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> {

	public void accept(T t) throws E;
}
