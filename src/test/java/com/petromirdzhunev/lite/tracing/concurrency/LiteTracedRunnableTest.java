package com.petromirdzhunev.lite.tracing.concurrency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.petromirdzhunev.lite.tracing.context.LiteTracingContext;

/**
 * Tests for {@link LiteTracedRunnable}.
 */
class LiteTracedRunnableTest {

	private static final String CORRELATION_ID = "correlation-id-123";

	@AfterEach
	void tearDown() {
		LiteTracingContext.clear();
	}

	@Test
	void runShouldSetTheCorrelationIdBeforeExecutingTheTask() {
		final AtomicReference<String> observed = new AtomicReference<>();
		final LiteTracedRunnable runnable = new LiteTracedRunnable(CORRELATION_ID, () -> observed.set(LiteTracingContext.get()));

		runnable.run();

		assertThat(observed).hasValue(CORRELATION_ID);
	}

	@Test
	void runShouldOverwriteAnyExistingCorrelationId() {
		LiteTracingContext.set("existing");
		final AtomicReference<String> observed = new AtomicReference<>();
		final LiteTracedRunnable runnable = new LiteTracedRunnable(CORRELATION_ID, () -> observed.set(LiteTracingContext.get()));

		runnable.run();

		assertThat(observed).hasValue(CORRELATION_ID);
	}

	@Test
	void runWithNullCorrelationIdShouldLeaveTheContextWithoutValue() {
		final AtomicBoolean observed = new AtomicBoolean();
		final LiteTracedRunnable runnable = new LiteTracedRunnable(null, () -> observed.set(LiteTracingContext.hasValue()));

		runnable.run();

		assertThat(observed).isFalse();
	}

	@Test
	void runShouldPropagateExceptionsFromTheTask() {
		final LiteTracedRunnable runnable = new LiteTracedRunnable(CORRELATION_ID, () -> {
			throw new IllegalStateException("boom");
		});

		assertThatThrownBy(runnable::run)
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("boom");
	}

	@Test
	void runShouldClearTheContextAfterExecutionWhenThereWasNoPreviousValue() {
		final LiteTracedRunnable runnable = new LiteTracedRunnable(CORRELATION_ID, () -> { });

		runnable.run();

		assertThat(LiteTracingContext.hasValue()).isFalse();
	}

	@Test
	void runShouldRestoreThePreviousCorrelationIdAfterExecution() {
		LiteTracingContext.set("previous");
		final LiteTracedRunnable runnable = new LiteTracedRunnable(CORRELATION_ID, () -> { });

		runnable.run();

		assertThat(LiteTracingContext.get()).isEqualTo("previous");
	}

	@Test
	void runShouldRestoreThePreviousCorrelationIdWhenTheTaskThrows() {
		LiteTracingContext.set("previous");
		final LiteTracedRunnable runnable = new LiteTracedRunnable(CORRELATION_ID, () -> {
			throw new IllegalStateException("boom");
		});

		assertThatThrownBy(runnable::run).isInstanceOf(IllegalStateException.class);

		assertThat(LiteTracingContext.get()).isEqualTo("previous");
	}

	@Test
	void runOnAnotherThreadShouldSetTheCorrelationIdThere() throws InterruptedException {
		final AtomicReference<String> observed = new AtomicReference<>();
		final LiteTracedRunnable runnable = new LiteTracedRunnable(CORRELATION_ID, () -> observed.set(LiteTracingContext.get()));

		final Thread thread = new Thread(runnable);
		thread.start();
		thread.join();

		assertThat(observed).hasValue(CORRELATION_ID);
		assertThat(LiteTracingContext.hasValue()).isFalse();
	}
}
