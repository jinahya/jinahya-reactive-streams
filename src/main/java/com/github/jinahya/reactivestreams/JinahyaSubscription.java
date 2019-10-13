package com.github.jinahya.reactivestreams;

import org.reactivestreams.Subscription;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

import static java.util.Objects.requireNonNull;

class JinahyaSubscription implements Subscription {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance with specified consumers.
     *
     * @param subscriptionContext  a subscription context.
     * @param requestConsumer      a consumer to be accepted with the argument of {@link Subscription#request(long)}.
     * @param cancellationConsumer a consumer to be notified when {@link Subscription#cancel()} is invoked.
     */
    JinahyaSubscription(final SubscriptionContext subscriptionContext, final LongConsumer requestConsumer,
                        final Consumer<Void> cancellationConsumer) {
        super();
        this.subscriptionContext = requireNonNull(subscriptionContext, "subscriptionContext is null");
        this.requestConsumer = requireNonNull(requestConsumer, "requestConsumer is null");
        this.cancellationConsumer = requireNonNull(cancellationConsumer, "cancellationConsumer is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Requests specified number of elements to the upstream {@link org.reactivestreams.Publisher}. The {@code
     * #request(long)} method of {@code JinahyaSubscription} class invokes {@link Consumer#accept(Object)} on {@code
     * requestConsumer} with given {@code n}.
     *
     * @param n the number of elements to request.
     */
    @Override
    public void request(final long n) {
        if (n <= 0) {
            subscriptionContext.signalError(new IllegalArgumentException("n(" + n + ") <= 0"));
            return;
        }
        cancellationLock.lock();
        try {
            if (cancelled) {
                subscriptionContext.signalError(new IllegalStateException("already cancelled"));
                return;
            }
            requestConsumer.accept(n);
        } finally {
            cancellationLock.unlock();
        }
    }

    /**
     * Requests the {@link org.reactivestreams.Publisher} to stop sending data and clean up resources. The {@code
     * #cancel()} method of {@code JinahyaSubscription} class invokes {@link Consumer#accept(Object)} on {@code
     * cancellationConsumer} with {@code null}.
     */
    @Override
    public void cancel() {
        cancellationLock.lock();
        try {
            if (cancelled) { // idempotent
                return;
            }
            cancelled = true;
            cancellationConsumer.accept(null);
        } finally {
            cancellationLock.unlock();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final SubscriptionContext subscriptionContext;

    // -----------------------------------------------------------------------------------------------------------------
    private final LongConsumer requestConsumer;

    // -----------------------------------------------------------------------------------------------------------------
    private final Consumer<Void> cancellationConsumer;

    private volatile boolean cancelled;

    private final Lock cancellationLock = new ReentrantLock();
}
