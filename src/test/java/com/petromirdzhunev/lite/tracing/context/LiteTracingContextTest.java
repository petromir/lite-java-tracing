package com.petromirdzhunev.lite.tracing.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LiteTracingContext}.
 */
class LiteTracingContextTest {

	private static final String CORRELATION_ID = "correlation-id-123";

	@AfterEach
	void tearDown() {
		LiteTracingContext.clear();
	}

	@Test
	void setAndGetShouldRoundTrip() {
		LiteTracingContext.set(CORRELATION_ID);

		assertThat(LiteTracingContext.get()).isEqualTo(CORRELATION_ID);
	}

	@Test
	void getShouldReturnNullWhenNoValueIsSet() {
		assertThat(LiteTracingContext.get()).isNull();
	}

	@Test
	void setShouldOverwriteThePreviousValue() {
		LiteTracingContext.set(CORRELATION_ID);
		LiteTracingContext.set("another-correlation-id");

		assertThat(LiteTracingContext.get()).isEqualTo("another-correlation-id");
	}

	@Test
	void hasValueShouldBeFalseWhenNothingIsSet() {
		assertThat(LiteTracingContext.hasValue()).isFalse();
	}

	@Test
	void hasValueShouldBeFalseWhenBlankValueIsSet() {
		LiteTracingContext.set("   ");

		assertThat(LiteTracingContext.hasValue()).isFalse();
	}

	@Test
	void hasValueShouldBeTrueWhenValueIsSet() {
		LiteTracingContext.set(CORRELATION_ID);

		assertThat(LiteTracingContext.hasValue()).isTrue();
	}

	@Test
	void clearShouldRemoveTheValue() {
		LiteTracingContext.set(CORRELATION_ID);
		LiteTracingContext.clear();

		assertThat(LiteTracingContext.hasValue()).isFalse();
		assertThat(LiteTracingContext.get()).isNull();
	}

	@Test
	void clearShouldNotFailWhenNothingIsSet() {
		assertThatCode(LiteTracingContext::clear).doesNotThrowAnyException();
	}

	@Test
	void valueShouldPropagateToChildThreads() throws InterruptedException {
		LiteTracingContext.set(CORRELATION_ID);
		final AtomicReference<String> observed = new AtomicReference<>();

		final Thread child = new Thread(() -> observed.set(LiteTracingContext.get()));
		child.start();
		child.join();

		assertThat(observed).hasValue(CORRELATION_ID);
	}

	@Test
	void valueShouldNotPropagateToThreadsCreatedBeforeItWasSet() throws InterruptedException {
		final AtomicBoolean observed = new AtomicBoolean();

		final Thread child = new Thread(() -> observed.set(LiteTracingContext.hasValue()));
		child.start();
		LiteTracingContext.set(CORRELATION_ID);
		child.join();

		assertThat(observed).isFalse();
	}
}
