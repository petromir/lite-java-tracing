package com.petromirdzhunev.lite.tracing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.petromirdzhunev.lite.tracing.context.LiteTracingContext;

/**
 * Tests for {@link LiteTracedAction}.
 */
class LiteTracedActionTest {

	private static final String CORRELATION_ID = "correlation-id-123";

	@AfterEach
	void tearDown() {
		LiteTracingContext.clear();
	}

	@Test
	void executeRunnableShouldSetTheCorrelationIdForTheDurationOfTheAction() {
		final AtomicReference<String> observed = new AtomicReference<>();

		LiteTracedAction.execute(CORRELATION_ID, () -> observed.set(LiteTracingContext.get()));

		assertThat(observed).hasValue(CORRELATION_ID);
	}

	@Test
	void executeRunnableShouldClearTheContextAfterwards() {
		LiteTracedAction.execute(CORRELATION_ID, () -> { });

		assertThat(LiteTracingContext.hasValue()).isFalse();
	}

	@Test
	void executeRunnableShouldClearTheContextOnException() {
		assertThatThrownBy(() -> LiteTracedAction.execute(CORRELATION_ID, () -> {
			throw new IllegalStateException("boom");
		})).isInstanceOf(IllegalStateException.class);

		assertThat(LiteTracingContext.hasValue()).isFalse();
	}

	@Test
	void executeCallableShouldReturnTheCallableResult() {
		final Callable<String> callable = LiteTracingContext::get;

		final String result = LiteTracedAction.execute(CORRELATION_ID, callable);

		assertThat(result).isEqualTo(CORRELATION_ID);
		assertThat(LiteTracingContext.hasValue()).isFalse();
	}

	@Test
	void executeCallableShouldClearTheContextOnException() {
		final Callable<String> callable = () -> {
			throw new IllegalStateException("boom");
		};

		assertThatThrownBy(() -> LiteTracedAction.execute(CORRELATION_ID, callable))
				.isInstanceOf(IllegalStateException.class);

		assertThat(LiteTracingContext.hasValue()).isFalse();
	}

	@Test
	void executeRunnableShouldClearAPreExistingContextInsteadOfRestoringIt() {
		LiteTracingContext.set("pre-existing");

		LiteTracedAction.execute(CORRELATION_ID, () -> { });

		assertThat(LiteTracingContext.hasValue()).isFalse();
	}

	@Test
	void executeCallableShouldClearAPreExistingContextInsteadOfRestoringIt() {
		LiteTracingContext.set("pre-existing");

		LiteTracedAction.execute(CORRELATION_ID, () -> "result");

		assertThat(LiteTracingContext.hasValue()).isFalse();
	}
}
