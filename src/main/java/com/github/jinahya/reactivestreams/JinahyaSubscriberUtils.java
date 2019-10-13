package com.github.jinahya.reactivestreams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A utility class for {@link Subscriber}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JinahyaSubscriberUtils {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance of {@link Subscriber} from specified arguments.
     *
     * @param subscriptionConsumer a consumer accepted with the subscription when {@link Subscriber#onSubscribe(Subscription)}
     *                             method is invoked.
     * @param dataConsumer         a consumer accepted with the data when {@link Subscriber#onNext(Object)} method is
     *                             invoked.
     * @param errorConsumer        a consumer accepted with an error when {@link Subscriber#onError(Throwable)} method
     *                             is invoked.
     * @param completionConsumer   a consumer accepted with an instance of {@link Void} when {@link
     *                             Subscriber#onComplete()} method is invoked.
     * @param <T>                  data type parameter
     * @return an instance of {@link Subscriber}.
     * @see #newSubscriberFrom(Consumer, BiConsumer, Consumer, Runnable)
     */
    public static <T> Subscriber<T> newSubscriberFrom(final Consumer<? super Subscription> subscriptionConsumer,
                                                      final BiConsumer<? super Subscription, ? super T> dataConsumer,
                                                      final Consumer<? super Throwable> errorConsumer,
                                                      final Consumer<? super Void> completionConsumer) {
        return new JinahyaSubscriber<>(subscriptionConsumer, dataConsumer, errorConsumer, completionConsumer);
    }

    /**
     * Creates a new instance of {@link Subscriber} from specified arguments.
     *
     * @param subscriptionConsumer a consumer accepted with the subscription when {@link Subscriber#onSubscribe(Subscription)}
     *                             method is invoked.
     * @param dataConsumer         a consumer accepted with the data when {@link Subscriber#onNext(Object)} method is
     *                             invoked.
     * @param errorConsumer        a consumer accepted with an error when {@link Subscriber#onError(Throwable)} method
     *                             is invoked.
     * @param completionRunner     a runnable run when {@link Subscriber#onComplete()} method is invoked.
     * @param <T>                  data type parameter
     * @return an instance of {@link Subscriber}.
     * @see #newSubscriberFrom(Consumer, BiConsumer, Consumer, Consumer)
     */
    public static <T> Subscriber<T> newSubscriberFrom(final Consumer<? super Subscription> subscriptionConsumer,
                                                      final BiConsumer<? super Subscription, ? super T> dataConsumer,
                                                      final Consumer<? super Throwable> errorConsumer,
                                                      final Runnable completionRunner) {
        return newSubscriberFrom(subscriptionConsumer, dataConsumer, errorConsumer, v -> completionRunner.run());
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    private JinahyaSubscriberUtils() {
        super();
    }
}
