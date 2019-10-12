package com.github.jinahya.reactivestreams;

import org.reactivestreams.Subscription;

import java.util.function.Consumer;
import java.util.function.LongConsumer;

import static java.util.Objects.requireNonNull;

public class JinahyaSubscription implements Subscription {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance with specified consumers.
     *
     * @param requestConsumer      a consumer to be accepted with the argument of {@link Subscription#request(long)}.
     * @param cancellationConsumer a consumer to be notified when {@link Subscription#cancel()} is invoked.
     */
    public JinahyaSubscription(final LongConsumer requestConsumer, final Consumer<Void> cancellationConsumer) {
        super();
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
            throw new IllegalArgumentException("n(" + n + ") <= 0");
        }
        if (cancelled) {
            throw new IllegalStateException("already cancelled");
        }
        requestConsumer.accept(n);
    }

    /**
     * Requests the {@link org.reactivestreams.Publisher} to stop sending data and clean up resources. The {@code
     * #cancel()} method of {@code JinahyaSubscription} class invokes {@link Consumer#accept(Object)} on {@code
     * cancellationConsumer} with {@code null}.
     */
    @Override
    public void cancel() {
        if (cancelled) {
            throw new IllegalStateException("already cancelled");
        }
        cancelled = true;
        cancellationConsumer.accept(null);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Indicates whether this subscription is already cancelled or not.
     *
     * @return {@code} true if this subscription is cancelled; {@code false} otherwise.
     */
    protected boolean isCancelled() {
        return cancelled;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final LongConsumer requestConsumer;

    // -----------------------------------------------------------------------------------------------------------------
    private transient boolean cancelled;

    private final Consumer<Void> cancellationConsumer;
}
