package com.petromirdzhunev.lite.tracing;

import java.util.concurrent.Callable;

import com.petromirdzhunev.lite.tracing.context.LiteTracingContext;

import lombok.SneakyThrows;

/**
 * Executes actions and callables within a given correlation id,
 * guaranteeing that the {@link LiteTracingContext} is cleared afterward.
 */
public final class LiteTracedAction {

	private LiteTracedAction() { }

	/**
	 * Executes the given action with the given correlation id set in the {@link LiteTracingContext},
	 * clearing the context afterward.
	 */
	static void execute(final String correlationId, final Runnable action) {
		try {
			LiteTracingContext.set(correlationId);
			action.run();
		} finally {
			LiteTracingContext.clear();
		}
	}

	/**
	 * Calls the given callable with the given correlation id set in the {@link LiteTracingContext},
	 * clearing the context afterward.
	 */
	@SneakyThrows
	static <T> T execute(final String correlationId, final Callable<T> callable) {
		try {
			LiteTracingContext.set(correlationId);
			return callable.call();
		} finally {
			LiteTracingContext.clear();
		}
	}
}
