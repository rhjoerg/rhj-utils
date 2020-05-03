package ch.rhj.util.functions;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> {

	public void accept(T t) throws E;
}
