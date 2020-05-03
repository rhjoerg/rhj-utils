package ch.rhj.util;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import ch.rhj.util.functions.ThrowingBiFunction;
import ch.rhj.util.functions.ThrowingConsumer;
import ch.rhj.util.functions.ThrowingFunction;
import ch.rhj.util.functions.ThrowingRunnable;
import ch.rhj.util.functions.ThrowingSupplier;

public interface Ex {

	public static RuntimeException runtimeException(Throwable t) {

		if (t instanceof RuntimeException)
			return RuntimeException.class.cast(t);

		return new RuntimeException(t);
	}

	public static Runnable runnable(ThrowingRunnable<? extends Throwable> throwingRunnable) {

		return () -> {

			try {

				throwingRunnable.run();

			} catch (Throwable e) {

				throw runtimeException(e);
			}
		};
	}

	public static void run(ThrowingRunnable<? extends Throwable> throwingRunnable) {

		try {

			throwingRunnable.run();

		} catch (Throwable e) {

			throw runtimeException(e);
		}
	}

	public static <T> Consumer<T> consumer(ThrowingConsumer<T, ? extends Throwable> throwingConsumer) {

		return (t) -> {

			try {

				throwingConsumer.accept(t);

			} catch (Throwable e) {

				throw runtimeException(e);
			}
		};
	}

	public static <T> void consume(ThrowingConsumer<T, ? extends Throwable> throwingConsumer, T t) {

		try {

			throwingConsumer.accept(t);

		} catch (Throwable e) {

			throw runtimeException(e);
		}
	}

	public static <R> Supplier<R> supplier(ThrowingSupplier<R, ? extends Throwable> throwingSupplier) {

		return () -> {

			try {

				return throwingSupplier.get();

			} catch (Throwable e) {

				throw runtimeException(e);
			}
		};
	}

	public static <R> R supply(ThrowingSupplier<R, ? extends Throwable> throwingSupplier) {

		try {

			return throwingSupplier.get();

		} catch (Throwable e) {

			throw runtimeException(e);
		}
	}

	public static <T, R> Function<T, R> function(ThrowingFunction<T, R, ? extends Throwable> throwingFunction) {

		return (t) -> {

			try {

				return throwingFunction.apply(t);

			} catch (Throwable e) {

				throw runtimeException(e);
			}
		};
	}

	public static <T, R> R apply(ThrowingFunction<T, R, ? extends Throwable> throwingFunction, T t) {

		try {

			return throwingFunction.apply(t);

		} catch (Throwable e) {

			throw runtimeException(e);
		}
	}

	public static <T, U, R> BiFunction<T, U, R> biFunction(ThrowingBiFunction<T, U, R, ? extends Throwable> throwingBiFunction) {

		return (t, u) -> {

			try {

				return throwingBiFunction.apply(t, u);

			} catch (Throwable e) {

				throw runtimeException(e);
			}
		};
	}

	public static <T, U, R> R biApply(ThrowingBiFunction<T, U, R, ? extends Throwable> throwingBiFunction, T t, U u) {

		try {

			return throwingBiFunction.apply(t, u);

		} catch (Throwable e) {

			throw runtimeException(e);
		}
	}
}
