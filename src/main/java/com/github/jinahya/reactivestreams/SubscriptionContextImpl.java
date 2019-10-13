package com.github.jinahya.reactivestreams;

import org.reactivestreams.Subscriber;

import static java.util.Objects.requireNonNull;

class SubscriptionContextImpl implements SubscriptionContext {

    // -----------------------------------------------------------------------------------------------------------------
    SubscriptionContextImpl(final Subscriber<?> subscriber) {
        super();
        this.subscriber = requireNonNull(subscriber, "subscriber is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void signalError(final Throwable t) {
        if (t == null) {
            throw new NullPointerException("t is null");
        }
        subscriber.onError(t);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final Subscriber<?> subscriber;
}
