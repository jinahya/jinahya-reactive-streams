package com.github.jinahya.reactivestreams;

import org.reactivestreams.Subscription;

import java.util.function.Consumer;
import java.util.function.LongConsumer;

import static java.util.Objects.requireNonNull;

public class JinahyaSubscription implements Subscription {

    // -----------------------------------------------------------------------------------------------------------------
    public JinahyaSubscription(final LongConsumer requestConsumer, final Consumer<Void> cancellationConsumer) {
        super();
        if (requestConsumer == null) {
            throw new NullPointerException("requestConsumer is null");
        }
        if (cancellationConsumer == null) {
            throw new NullPointerException("cancellationConsumer is null");
        }
        this.requestConsumer = n -> {
            if (cancelled) {
                throw new IllegalStateException("cancelled");
            }
            requestConsumer.accept(n);
        };
        this.cancellationConsumer = v -> {
            if (cancelled) {
                throw new IllegalStateException("already cancelled");
            }
            cancelled = true;
            cancellationConsumer.accept(null);
        };
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void request(final long n) {
        requestConsumer.accept(n);
    }

    @Override
    public void cancel() {
        cancellationConsumer.accept(null);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final LongConsumer requestConsumer;

    // -----------------------------------------------------------------------------------------------------------------
    private transient boolean cancelled;

    private final Consumer<Void> cancellationConsumer;
}
