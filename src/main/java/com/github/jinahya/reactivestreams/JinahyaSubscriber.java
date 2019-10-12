package com.github.jinahya.reactivestreams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public class JinahyaSubscriber<T> implements Subscriber<T> {

    // -----------------------------------------------------------------------------------------------------------------
    public JinahyaSubscriber(final Consumer<? super Subscription> subscriptionConsumer,
                             final Consumer<? super T> dataConsumer, final Consumer<? super Throwable> errorConsumer,
                             final Consumer<? super Void> completionConsumer) {
        super();
        this.subscriptionConsumer = requireNonNull(subscriptionConsumer, "subscriptionConsumer is null");
        this.dataConsumer = requireNonNull(dataConsumer, "dataConsumer is null");
        this.errorConsumer = requireNonNull(errorConsumer, "errorConsumer is null");
        this.completionConsumer = requireNonNull(completionConsumer, "completionConsumer is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Notifies this subscriber with specified subscription.
     *
     * @param s the subscription.
     */
    @Override
    public void onSubscribe(final Subscription s) {
        if (s == null) {
            throw new NullPointerException("s is null");
        }
        if (subscription != null) {
            throw new IllegalStateException("already subscribed: " + subscription);
        }
        subscription = s;
        error = null;
        completed = false;
        subscriptionConsumer.accept(s);
    }

    /**
     * Notifies this subscriber with specified data.
     *
     * @param t the data.
     */
    @Override
    public void onNext(final T t) {
        if (subscription == null) {
            throw new IllegalStateException("not subscribed yet");
        }
        if (error != null) {
            throw new IllegalStateException("already errored");
        }
        if (completed) {
            throw new IllegalStateException("already completed");
        }
        dataConsumer.accept(t);
    }

    /**
     * Notifies this subscriber that an error occurred.
     *
     * @param t a throwable represents the error.
     */
    @Override
    public void onError(final Throwable t) {
        if (t == null) {
            throw new NullPointerException("t is null");
        }
        if (subscription == null) {
            throw new IllegalStateException("not subscribed yet");
        }
        if (completed) {
            throw new IllegalStateException("already completed");
        }
        if (error != null) {
            throw new IllegalStateException("already errored: " + error);
        }
        subscription = null;
        error = t;
        errorConsumer.accept(error);
    }

    /**
     * Notifies this subscriber that there is not more data to publish.
     */
    @Override
    public void onComplete() {
        if (subscription == null) {
            throw new IllegalStateException("not subscribed yet");
        }
        if (error != null) {
            throw new IllegalStateException("already errored");
        }
        if (completed) {
            throw new IllegalStateException("already completed");
        }
        subscription = null;
        completed = true;
        completionConsumer.accept(null);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private transient Subscription subscription;

    private final Consumer<? super Subscription> subscriptionConsumer;

    // -----------------------------------------------------------------------------------------------------------------
    private final Consumer<? super T> dataConsumer;

    // -----------------------------------------------------------------------------------------------------------------
    private transient Throwable error;

    private final Consumer<? super Throwable> errorConsumer;

    // -----------------------------------------------------------------------------------------------------------------
    private transient boolean completed;

    private final Consumer<? super Void> completionConsumer;
}
