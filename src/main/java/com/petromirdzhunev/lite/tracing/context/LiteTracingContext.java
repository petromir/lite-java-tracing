package com.petromirdzhunev.lite.tracing.context;

/**
 * A context which holds tracing value (correlation-id) per thread.
 */
public final class LiteTracingContext {

	/**
	 * The key under which the correlation id is stored
	 */
	public static final String CORRELATION_ID_KEY = "Correlation-Id";
	/**
	 * Allows us to propagate the tracing value to children threads.
	 */
	private static final ThreadLocal<String> CURRENT_THREAD_CORRELATION_ID = new InheritableThreadLocal<>();

	private LiteTracingContext() { }

	/**
	 * Returns the correlation id of the current thread, or {@code null} if none is set.
	 */
	public static String get() {
		return CURRENT_THREAD_CORRELATION_ID.get();
	}

	/**
	 * Sets the correlation id for the current thread.
	 */
	public static void set(final String correlationId) {
		CURRENT_THREAD_CORRELATION_ID.set(correlationId);
	}

	/**
	 * Returns whether a non-blank correlation id is set for the current thread.
	 */
	public static boolean hasValue() {
		final String currentCorrelationId = CURRENT_THREAD_CORRELATION_ID.get();
		return currentCorrelationId != null && !currentCorrelationId.isBlank();
	}

	/**
	 * Removes the correlation id from the current thread.
	 */
	public static void clear() {
		CURRENT_THREAD_CORRELATION_ID.remove();
	}
}

