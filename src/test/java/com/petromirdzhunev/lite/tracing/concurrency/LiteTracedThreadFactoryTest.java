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
 * Tests for {@link LiteTracedThreadFactory}.
 */
class LiteTracedThreadFactoryTest {

	private static final String CORRELATION_ID = "correlation-id-123";

	@AfterEach
	void tearDown() {
		LiteTracingContext.clear();
	}

	@Test
	void ofVirtualShouldCreateVirtualThreads() {
		final Thread thread = LiteTracedThreadFactory.ofVirtual()
		                                             .newThread(() -> { });

		assertThat(thread.isVirtual()).isTrue();
	}

	@Test
	void ofPlatformShouldCreatePlatformThreads() {
		final Thread thread = LiteTracedThreadFactory.ofPlatform()
		                                             .newThread(() -> { });

		assertThat(thread.isVirtual()).isFalse();
	}

	@Test
	void newThreadShouldPropagateTheCorrelationIdOfTheCreatingThread() throws InterruptedException {
		LiteTracingContext.set(CORRELATION_ID);
		final AtomicBoolean propagated = new AtomicBoolean(false);

		final Thread thread = LiteTracedThreadFactory.ofVirtual()
			.newThread(() -> propagated.set(CORRELATION_ID.equals(LiteTracingContext.get())));
		thread.start();
		thread.join();

		assertThat(propagated).isTrue();
	}

	@Test
	void newThreadShouldNotFailWithoutCorrelationId() throws InterruptedException {
		final AtomicBoolean executed = new AtomicBoolean(false);

		final Thread thread = LiteTracedThreadFactory.ofPlatform()
			.newThread(() -> executed.set(true));
		thread.start();
		thread.join();

		assertThat(executed).isTrue();
	}

	@Test
	void newThreadWithoutCorrelationIdShouldSeeNoValue() throws InterruptedException {
		final AtomicBoolean observed = new AtomicBoolean(true);

		final Thread thread = LiteTracedThreadFactory.ofVirtual()
			.newThread(() -> observed.set(LiteTracingContext.hasValue()));
		thread.start();
		thread.join();

		assertThat(observed).isFalse();
	}

	@Test
	void newThreadShouldCaptureTheCorrelationIdAtCreationTime() throws InterruptedException {
		LiteTracingContext.set(CORRELATION_ID);
		final AtomicReference<String> observed = new AtomicReference<>();

		final Thread thread = LiteTracedThreadFactory.ofVirtual()
			.newThread(() -> observed.set(LiteTracingContext.get()));
		LiteTracingContext.set("another-correlation-id");
		thread.start();
		thread.join();

		assertThat(observed).hasValue(CORRELATION_ID);
	}

	@Test
	void executorWithVirtualFactoryShouldPropagateTheCorrelationId() throws Exception {
		LiteTracingContext.set(CORRELATION_ID);

		try (ExecutorService executor = Executors.newThreadPerTaskExecutor(LiteTracedThreadFactory.ofVirtual())) {
			assertThat(executor.submit(LiteTracingContext::get).get()).isEqualTo(CORRELATION_ID);
		}
	}

	@Test
	void executorWithPlatformFactoryShouldPropagateTheCorrelationId() throws Exception {
		LiteTracingContext.set(CORRELATION_ID);

		try (ExecutorService executor = Executors.newCachedThreadPool(LiteTracedThreadFactory.ofPlatform())) {
			assertThat(executor.submit(LiteTracingContext::get).get()).isEqualTo(CORRELATION_ID);
		}
	}
}
