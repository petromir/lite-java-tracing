package com.petromirdzhunev.lite.tracing.concurrency;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.petromirdzhunev.lite.tracing.context.LiteTracingContext;

/**
 * An {@link ExecutorService} decorator capturing the submitter's {@link LiteTracingContext}
 * at submission time and restoring it on the executing thread for every task.
 *
 * <p>Unlike {@link LiteTracedThreadFactory}, which binds the correlation id to a thread
 * at its creation, this decorator binds it to each individual task, making it safe
 * for pooled executors where threads are reused across tasks from different contexts.
 */
public final class LiteTracedExecutorService extends AbstractExecutorService {

	private final ExecutorService delegate;

	public LiteTracedExecutorService(final ExecutorService delegate) {
		this.delegate = delegate;
	}

	@Override
	public void execute(final Runnable command) {
		final String correlationId = LiteTracingContext.get();
		delegate.execute(new LiteTracedRunnable(correlationId, command));
	}

	@Override
	public void shutdown() {
		delegate.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		return delegate.shutdownNow();
	}

	@Override
	public boolean isShutdown() {
		return delegate.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return delegate.isTerminated();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws InterruptedException {@inheritDoc}
	 */
	@Override
	public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
		return delegate.awaitTermination(timeout, unit);
	}
}
