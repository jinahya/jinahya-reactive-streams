package com.github.jinahya.reactivestreams;

import org.reactivestreams.Subscription;

import java.util.function.Consumer;
import java.util.function.LongConsumer;

/**
 * A utility class for {@link Subscription}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JinahyaSubscriptionUtils {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new subscription with specified consumers.
     *
     * @param requestConsumer      a consumer to be accepted with the argument of {@link Subscription#request(long)}.
     * @param cancellationConsumer a consumer to be notified when {@link Subscription#cancel()} is invoked.
     * @return a new instance of {@link Subscription}.
     */
    public static Subscription newSubscriptionFrom(final LongConsumer requestConsumer,
                                                   final Consumer<Void> cancellationConsumer) {
        return new JinahyaSubscription(requestConsumer, cancellationConsumer);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    private JinahyaSubscriptionUtils() {
        super();
    }
}
