package ch.rhj.util.functions;

@FunctionalInterface
public interface ThrowingBiConsumer<T, U, E extends Throwable> {

	public void accept(T t, U u) throws E;
}
