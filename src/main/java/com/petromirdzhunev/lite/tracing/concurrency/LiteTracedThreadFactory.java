package com.petromirdzhunev.lite.tracing.concurrency;

import java.util.concurrent.ThreadFactory;

import com.petromirdzhunev.lite.tracing.context.LiteTracingContext;

/**
 * A {@link ThreadFactory} which wraps every submitted task in a {@link LiteTracedRunnable},
 * propagating the {@link LiteTracingContext} of the creating thread to the created thread.
 * Supports creation of both platform and virtual threads.
 *
 * <p>The correlation id is captured when the thread is <em>created</em>, which is correct for
 * thread-per-task executors (e.g. virtual threads). For pooled executors reused across tasks
 * submitted from different contexts, wrap the executor in a {@link LiteTracedExecutorService}
 * instead — it captures the id per task at submission time.
 *
 * <p>A per-task executor over virtual threads can be created with:
 * <pre>{@code
 * ExecutorService executor = Executors.newThreadPerTaskExecutor(LiteTracedThreadFactory.ofVirtual());
 * }</pre>
 */
public final class LiteTracedThreadFactory implements ThreadFactory {

	private final boolean virtual;

	private LiteTracedThreadFactory(final boolean virtual) {
		this.virtual = virtual;
	}

	/**
	 * Creates a factory producing platform threads.
	 *
	 * @return a platform-thread factory
	 */
	public static LiteTracedThreadFactory ofPlatform() {
		return new LiteTracedThreadFactory(false);
	}

	/**
	 * Creates a factory producing virtual threads.
	 *
	 * @return a virtual-thread factory
	 */
	public static LiteTracedThreadFactory ofVirtual() {
		return new LiteTracedThreadFactory(true);
	}

	@Override
	public Thread newThread(final Runnable runnable) {
		final String correlationId = LiteTracingContext.get();
		final Runnable tracingTask = new LiteTracedRunnable(correlationId, runnable);
		final Thread.Builder builder = virtual ? Thread.ofVirtual() : Thread.ofPlatform();
		return builder.unstarted(tracingTask);
	}
}
