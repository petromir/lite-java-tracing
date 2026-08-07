package com.petromirdzhunev.lite.tracing.concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.petromirdzhunev.lite.tracing.context.LiteTracingContext;

/**
 * Tests for {@link LiteTracedExecutorService}.
 */
class LiteTracedExecutorServiceTest {

	private static final String CORRELATION_ID = "correlation-id-123";

	@AfterEach
	void tearDown() {
		LiteTracingContext.clear();
	}

	@Test
	void wrappedExecutorShouldPropagateTheSubmittersCorrelationId() throws Exception {
		LiteTracingContext.set(CORRELATION_ID);

		try (ExecutorService executor = new LiteTracedExecutorService(Executors.newCachedThreadPool())) {
			assertThat(executor.submit(LiteTracingContext::get)
				.get()).isEqualTo(CORRELATION_ID);
		}
	}

	@Test
	void wrappedExecutorShouldCaptureTheIdPerTaskOnReusedThreads() throws Exception {
		try (ExecutorService executor = new LiteTracedExecutorService(Executors.newSingleThreadExecutor())) {
			LiteTracingContext.set("first");
			assertThat(executor.submit(LiteTracingContext::get)
				.get()).isEqualTo("first");

			LiteTracingContext.set("second");
			assertThat(executor.submit(LiteTracingContext::get)
				.get()).isEqualTo("second");
		}
	}

	@Test
	void wrappedExecutorShouldPropagateTheIdToExecuteCalls() {
		LiteTracingContext.set(CORRELATION_ID);
		final AtomicReference<String> observed = new AtomicReference<>();

		try (ExecutorService executor = new LiteTracedExecutorService(Executors.newCachedThreadPool())) {
			executor.execute(() -> observed.set(LiteTracingContext.get()));
		}

		assertThat(observed).hasValue(CORRELATION_ID);
	}

	@Test
	void wrappedExecutorShouldNotFailWhenSubmittedWithoutCorrelationId() {
		final AtomicBoolean observed = new AtomicBoolean(true);

		try (ExecutorService executor = new LiteTracedExecutorService(Executors.newCachedThreadPool())) {
			executor.execute(() -> observed.set(LiteTracingContext.hasValue()));
		}

		assertThat(observed).isFalse();
	}

	@Test
	void wrappedExecutorShouldNotLeakTheIdToThePoolThread() throws Exception {
		try (ExecutorService executor = new LiteTracedExecutorService(Executors.newSingleThreadExecutor())) {
			LiteTracingContext.set(CORRELATION_ID);
			executor.submit(() -> { })
				.get();

			LiteTracingContext.clear();
			final AtomicBoolean observed = new AtomicBoolean(true);
			executor.submit(() -> observed.set(LiteTracingContext.hasValue()))
				.get();

			assertThat(observed).isFalse();
		}
	}

	@Test
	void wrappedExecutorShouldDelegateLifecycleOperations() {
		final ExecutorService delegate = Executors.newCachedThreadPool();
		final ExecutorService executor = new LiteTracedExecutorService(delegate);

		assertThat(executor.isShutdown()).isFalse();
		assertThat(executor.isTerminated()).isFalse();

		executor.shutdown();

		assertThat(executor.isShutdown()).isTrue();
		assertThat(delegate.isShutdown()).isTrue();
	}
}
