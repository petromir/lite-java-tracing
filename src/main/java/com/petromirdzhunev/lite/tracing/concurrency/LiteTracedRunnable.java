package com.petromirdzhunev.lite.tracing.concurrency;

import com.petromirdzhunev.lite.tracing.context.LiteTracingContext;

import lombok.RequiredArgsConstructor;

/**
 * A {@link Runnable} decorator which restores a given correlation id
 * into the {@link LiteTracingContext} of the executing thread before delegating to the wrapped task.
 */
@RequiredArgsConstructor
public class LiteTracedRunnable implements Runnable {
	private final String correlationId;
	private final Runnable task;

	/**
	 * Sets the correlation id into the current thread's {@link LiteTracingContext}, executes the wrapped task
	 * and restores the previous context afterward.
	 */
	@Override
	public void run() {
		final String previous = LiteTracingContext.get();
		try {
			LiteTracingContext.set(correlationId);
			task.run();
		} finally {
			LiteTracingContext.set(previous);
		}
	}
}