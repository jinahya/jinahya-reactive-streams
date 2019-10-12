package com.github.jinahya.reactivestreams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public abstract class AbstractSubscriber<T> implements Subscriber<T> {

    // -----------------------------------------------------------------------------------------------------------------
    public AbstractSubscriber(final Consumer<Subscription> subscriptionConsumer,
                              final Consumer<? super T> dataConsumer, final Consumer<? super Throwable> errorConsumer,
                              final Consumer<Void> completionConsumer) {
        super();
        if (subscriptionConsumer == null) {
            throw new NullPointerException("subscriptionConsumer is null");
        }
        if (dataConsumer == null) {
            throw new NullPointerException("dataConsumer is null");
        }
        if (errorConsumer == null) {
            throw new NullPointerException("errorConsumer is null");
        }
        if (completionConsumer == null) {
            throw new NullPointerException("completionConsumer is null");
        }
        this.subscriptionConsumer = s -> {
            if (subscription != null) {
                throw new IllegalStateException("already subscribed: " + subscription);
            }
            subscription = s;
            error = null;
            completed = false;
            subscriptionConsumer.accept(subscription);
        };
        this.dataConsumer = requireNonNull(dataConsumer, "dataConsumer is null");
        this.errorConsumer = t -> {
            if (error != null) {
                throw new IllegalStateException("already errored: " + error);
            }
            error = t;
            errorConsumer.accept(t);
        };
        this.completionConsumer = v -> {
            if (completed) {
                throw new IllegalStateException("already completed");
            }
            completed = true;
            completionConsumer.accept(null);
        };
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void onSubscribe(final Subscription s) {
        subscriptionConsumer.accept(s);
    }

    @Override
    public void onNext(final T t) {
        dataConsumer.accept(t);
    }

    @Override
    public void onError(final Throwable t) {
        errorConsumer.accept(t);
    }

    @Override
    public void onComplete() {
        completionConsumer.accept(null);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private transient Subscription subscription;

    private final Consumer<Subscription> subscriptionConsumer;

    // -----------------------------------------------------------------------------------------------------------------
    private final Consumer<? super T> dataConsumer;

    // -----------------------------------------------------------------------------------------------------------------
    private transient Throwable error;

    private final Consumer<? super Throwable> errorConsumer;

    // -----------------------------------------------------------------------------------------------------------------
    private transient boolean completed;

    private final Consumer<Void> completionConsumer;
}
