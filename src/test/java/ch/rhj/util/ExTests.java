package ch.rhj.util;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ch.rhj.util.function.ThrowingBiConsumer;
import ch.rhj.util.function.ThrowingBiFunction;
import ch.rhj.util.function.ThrowingConsumer;
import ch.rhj.util.function.ThrowingFunction;
import ch.rhj.util.function.ThrowingRunnable;
import ch.rhj.util.function.ThrowingSupplier;

@ExtendWith(MockitoExtension.class)
public class ExTests {

	@Test
	public void testRuntime() {

		Throwable t1 = new RuntimeException();
		Throwable t2 = new Exception();

		assertTrue(Ex.runtimeException(t1) == t1);
		assertTrue(Ex.runtimeException(t2).getCause() == t2);
	}

	@Mock
	public ThrowingRunnable<Exception> throwingRunnable;

	@Test
	public void testRunnable() throws Exception {

		Ex.runnable(throwingRunnable).run();
		Ex.run(throwingRunnable);
		verify(throwingRunnable, times(2)).run();

		doThrow(Exception.class).when(throwingRunnable).run();
		assertThrows(RuntimeException.class, () -> Ex.runnable(throwingRunnable).run());
		assertThrows(RuntimeException.class, () -> Ex.run(throwingRunnable));
	}

	@Mock
	public ThrowingConsumer<String, Exception> throwingConsumer;

	@Test
	public void testConsumer() throws Exception {

		Ex.consumer(throwingConsumer).accept("a");
		Ex.consume(throwingConsumer, "a");
		verify(throwingConsumer, times(2)).accept("a");

		doThrow(Exception.class).when(throwingConsumer).accept("a");
		assertThrows(RuntimeException.class, () -> Ex.consumer(throwingConsumer).accept("a"));
		assertThrows(RuntimeException.class, () -> Ex.consume(throwingConsumer, "a"));
	}

	@Mock
	public ThrowingBiConsumer<String, String, Exception> throwingBiConsumer;

	@Test
	public void testBiConsumer() throws Exception {

		Ex.biConsume(throwingBiConsumer, "a", "b");
		verify(throwingBiConsumer, times(1)).accept("a", "b");

		doThrow(Exception.class).when(throwingBiConsumer).accept("a", "b");
		assertThrows(RuntimeException.class, () -> Ex.biConsume(throwingBiConsumer, "a", "b"));
	}

	@Mock
	public ThrowingSupplier<String, Exception> throwingSupplier;

	@Test
	public void testSupplier() throws Exception {

		Ex.supplier(throwingSupplier).get();
		Ex.supply(throwingSupplier);
		verify(throwingSupplier, times(2)).get();

		doThrow(Exception.class).when(throwingSupplier).get();
		assertThrows(RuntimeException.class, () -> Ex.supplier(throwingSupplier).get());
		assertThrows(RuntimeException.class, () -> Ex.supply(throwingSupplier));
	}

	@Mock
	public ThrowingFunction<String, String, Exception> throwingFunction;

	@Test
	public void testFunction() throws Exception {

		Ex.function(throwingFunction).apply("a");
		Ex.apply(throwingFunction, "a");
		verify(throwingFunction, times(2)).apply("a");

		doThrow(Exception.class).when(throwingFunction).apply("a");
		assertThrows(RuntimeException.class, () -> Ex.function(throwingFunction).apply("a"));
		assertThrows(RuntimeException.class, () -> Ex.apply(throwingFunction, "a"));
	}

	@Mock
	public ThrowingBiFunction<String, String, String, Exception> throwingBiFunction;

	@Test
	public void testBiFunction() throws Exception {

		Ex.biFunction(throwingBiFunction).apply("a", "b");
		Ex.biApply(throwingBiFunction, "a", "b");
		verify(throwingBiFunction, times(2)).apply("a", "b");

		doThrow(Exception.class).when(throwingBiFunction).apply("a", "b");
		assertThrows(RuntimeException.class, () -> Ex.biFunction(throwingBiFunction).apply("a", "b"));
		assertThrows(RuntimeException.class, () -> Ex.biApply(throwingBiFunction, "a", "b"));
	}
}
