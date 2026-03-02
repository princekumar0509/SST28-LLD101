package com.example.metrics;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe, lazy-initialized Singleton for global metrics.
 *
 * Uses the "initialization-on-demand holder" (Holder) idiom which gives
 * lazy, thread-safe initialization without any explicit synchronization on
 * getInstance().
 *
 * Additional safeguards:
 *  - Reflection guard: throws if the constructor is ever called a second time.
 *  - readResolve: deserialization always returns the existing singleton.
 */
public class MetricsRegistry implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ConcurrentHashMap so individual counter ops don't need a lock on the map itself.
    // We still use synchronized wrappers on the public methods to keep the
    // read-modify-write of increment() atomic.
    private final Map<String, Long> counters = new ConcurrentHashMap<>();

    // Private constructor with a reflection guard.
    private MetricsRegistry() {
        // If someone tries to call this via reflection after the singleton
        // exists, blow up immediately.
        if (Holder.INSTANCE != null) {
            throw new IllegalStateException(
                "Singleton already created – use MetricsRegistry.getInstance()");
        }
    }

    // ------------------------------------------------------------------ //
    // Holder idiom: the JVM loads Holder only on the first call to
    // getInstance(), guaranteeing thread-safe, lazy initialization
    // with zero synchronization cost on the hot path.
    // ------------------------------------------------------------------ //
    private static final class Holder {
        static final MetricsRegistry INSTANCE = new MetricsRegistry();
    }

    /** Returns the single global instance. Thread-safe, lazy. */
    public static MetricsRegistry getInstance() {
        return Holder.INSTANCE;
    }

    // ------------------------------------------------------------------ //
    // Public API
    // ------------------------------------------------------------------ //

    public synchronized void setCount(String key, long value) {
        counters.put(key, value);
    }

    public synchronized void increment(String key) {
        counters.put(key, getCount(key) + 1);
    }

    public synchronized long getCount(String key) {
        return counters.getOrDefault(key, 0L);
    }

    public synchronized Map<String, Long> getAll() {
        return Collections.unmodifiableMap(new HashMap<>(counters));
    }

    // ------------------------------------------------------------------ //
    // Serialization hook – always return the existing singleton so that
    // deserialization never creates a second instance.
    // ------------------------------------------------------------------ //
    @Serial
    private Object readResolve() {
        return Holder.INSTANCE;
    }
}
