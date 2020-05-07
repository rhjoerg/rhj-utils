package ch.rhj.util;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import ch.rhj.util.function.ThrowingBiConsumer;
import ch.rhj.util.function.ThrowingBiFunction;
import ch.rhj.util.function.ThrowingConsumer;
import ch.rhj.util.function.ThrowingFunction;
import ch.rhj.util.function.ThrowingRunnable;
import ch.rhj.util.function.ThrowingSupplier;

public interface Ex {

	public static RuntimeException runtimeException(Throwable t) {

		if (t instanceof RuntimeException)
			return RuntimeException.class.cast(t);

		return new RuntimeException(t);
	}

	// ----------------------------------------------------------------------------------------------------------------

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

		runnable(throwingRunnable).run();
	}

	// ----------------------------------------------------------------------------------------------------------------

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

		consumer(throwingConsumer).accept(t);
	}

	// ----------------------------------------------------------------------------------------------------------------

	public static <T, U> BiConsumer<T, U> biConsumer(ThrowingBiConsumer<T, U, ? extends Throwable> throwingBiConsumer) {

		return (t, u) -> {

			try {

				throwingBiConsumer.accept(t, u);

			} catch (Throwable e) {

				throw runtimeException(e);
			}
		};
	}

	public static <T, U> void biConsume(ThrowingBiConsumer<T, U, ? extends Throwable> throwingBiConsumer, T t, U u) {

		biConsumer(throwingBiConsumer).accept(t, u);
	}

	// ----------------------------------------------------------------------------------------------------------------

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

		return supplier(throwingSupplier).get();
	}

	// ----------------------------------------------------------------------------------------------------------------

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

		return function(throwingFunction).apply(t);
	}

	// ----------------------------------------------------------------------------------------------------------------

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

		return biFunction(throwingBiFunction).apply(t, u);
	}
}
