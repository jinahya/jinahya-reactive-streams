package com.github.jinahya.reactivestreams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public class JinahyaSubscriber<T> implements Subscriber<T> {

    // -----------------------------------------------------------------------------------------------------------------
    JinahyaSubscriber(final Consumer<? super Subscription> subscriptionConsumer,
                      final BiConsumer<? super Subscription, ? super T> dataConsumer,
                      final Consumer<? super Throwable> errorConsumer,
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
        subscriptionLock.lock();
        try {
            // > A `Subscriber` MUST call `Subscription.cancel()` on the given `Subscription`
            // > after an `onSubscribe` signal if it already has an active `Subscription`.
            if (subscription != null) {
                s.cancel();
                return;
            }
            subscription = s;
            subscriptionConsumer.accept(subscription);
        } finally {
            subscriptionLock.unlock();
        }
    }

    /**
     * Notifies this subscriber with specified data.
     *
     * @param t the data.
     */
    @Override
    public void onNext(final T t) {
        dataConsumer.accept(subscription, t);
    }

    /**
     * Notifies this subscriber that an error occurred.
     *
     * @param t a throwable represents the error.
     */
    @Override
    public void onError(final Throwable t) {
        assert !completed;
        assert error == null;
        error = t;
        errorConsumer.accept(error);
        subscription = null;
    }

    /**
     * Notifies this subscriber that there is no more data to publish.
     */
    @Override
    public void onComplete() {
        assert !completed;
        assert error == null;
        completed = true;
        completionConsumer.accept(null);
        subscription = null;
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected void acceptSubscription(final Consumer<? super Subscription> consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer is null");
        }
        lock.lock();
        try {
            if (subscription == null) {
                throw new IllegalStateException("not subscribed yet");
            }
        } finally {
            lock.unlock();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final Lock lock = new ReentrantLock();

    // -----------------------------------------------------------------------------------------------------------------
    private final Consumer<? super Subscription> subscriptionConsumer;

    private final Lock subscriptionLock = new ReentrantLock();

    private volatile Subscription subscription;

    // -----------------------------------------------------------------------------------------------------------------
    private final BiConsumer<? super Subscription, ? super T> dataConsumer;

    // -----------------------------------------------------------------------------------------------------------------
    private volatile Throwable error;

    private final Consumer<? super Throwable> errorConsumer;

    // -----------------------------------------------------------------------------------------------------------------
    private volatile boolean completed;

    private final Consumer<? super Void> completionConsumer;
}
