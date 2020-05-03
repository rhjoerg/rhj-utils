package ch.rhj.util.functions;

@FunctionalInterface
public interface ThrowingRunnable<E extends Throwable> {

	public void run() throws E;
}
