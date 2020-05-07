package ch.rhj.util.function;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

public class ConsumersTests {

	@Test
	public void testEmpty() {

		Consumer<String> consumer = Consumers.empty();

		consumer.accept("a");
	}
}
