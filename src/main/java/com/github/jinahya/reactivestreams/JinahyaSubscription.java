package com.github.jinahya.reactivestreams;

import org.reactivestreams.Subscription;

import java.util.function.Consumer;
import java.util.function.LongConsumer;

import static java.util.Objects.requireNonNull;

public class JinahyaSubscription implements Subscription {

    // -----------------------------------------------------------------------------------------------------------------
    public JinahyaSubscription(final LongConsumer requestConsumer, final Consumer<Void> cancellationConsumer) {
        super();
        this.requestConsumer = requireNonNull(requestConsumer, "requestConsumer is null");
        this.cancellationConsumer = requireNonNull(cancellationConsumer, "cancellationConsumer is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void request(final long n) {
        if (cancelled) {
            throw new IllegalStateException("cancelled");
        }
        requestConsumer.accept(n);
    }

    @Override
    public void cancel() {
        if (cancelled) {
            throw new IllegalStateException("already cancelled");
        }
        cancelled = true;
        cancellationConsumer.accept(null);
    }

    // -----------------------------------------------------------------------------------------------------------------
    public boolean isCancelled() {
        return cancelled;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final LongConsumer requestConsumer;

    // -----------------------------------------------------------------------------------------------------------------
    private transient boolean cancelled;

    private final Consumer<Void> cancellationConsumer;
}
