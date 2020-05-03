package ch.rhj.util.functions;

@FunctionalInterface
public interface ThrowingSupplier<R, E extends Throwable> {

	public R get() throws E;
}
