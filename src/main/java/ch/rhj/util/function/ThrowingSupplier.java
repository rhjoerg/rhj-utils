package ch.rhj.util.function;

@FunctionalInterface
public interface ThrowingSupplier<R, E extends Throwable> {

	public R get() throws E;
}
