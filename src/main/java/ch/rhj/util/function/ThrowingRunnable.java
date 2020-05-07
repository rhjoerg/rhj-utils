package ch.rhj.util.function;

@FunctionalInterface
public interface ThrowingRunnable<E extends Throwable> {

	public void run() throws E;
}
