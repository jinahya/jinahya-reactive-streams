package com.github.jinahya.reactivestreams;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;

import static com.github.jinahya.reactivestreams.JinahyaSubscriberUtils.newSubscriberFrom;
import static com.github.jinahya.reactivestreams.JinahyaSubscriptionUtils.newSubscriptionFrom;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class JinahyaSubscriberUtilsTest {

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    public void testNewSubscriberFrom() {
        final Publisher<String> publisher = s -> {
            s.onSubscribe(newSubscriptionFrom(
                    n -> {
                        log.debug("n: {}", n);
                        for (long i = 0; i < n; i++) {
                            s.onNext(Long.toString(i));
                        }
                    },
                    v -> log.debug("cancelled")
            ));
            assertThrows(IllegalStateException.class, () -> s.onSubscribe(newSubscriptionFrom(
                    n -> {
                    },
                    v -> {
                    }
            )));
            assertThrows(NullPointerException.class, () -> s.onSubscribe(null));
        };
        publisher.subscribe(newSubscriberFrom(
                s -> {
                    log.debug("subscription: {}", s);
                    s.request(current().nextLong(1, 64));
                    s.cancel();
                    assertThrows(IllegalStateException.class, s::cancel);
                },
                t -> log.debug("next: {}", t),
                t -> log.debug("error: {}", t.getMessage(), t),
                v -> log.debug("completed")
        ));
    }

    @Test
    public void testNewSubscriberFrom_error() {
        final Publisher<String> publisher = s -> s.onSubscribe(newSubscriptionFrom(
                n -> {
                    log.debug("n: {}", n);
                    s.onError(new RuntimeException());
                    assertThrows(IllegalStateException.class, () -> s.onError(new RuntimeException()));
                },
                v -> log.debug("cancelled")
        ));
        publisher.subscribe(newSubscriberFrom(
                s -> {
                    log.debug("subscription: {}", s);
                    s.request(current().nextLong(1, 128));
                },
                t -> log.debug("next: {}", t),
                t -> log.debug("error: {}", t.getMessage(), t),
                v -> log.debug("completed")
        ));
    }

    @Test
    public void testNewSubscriberFrom_complete() {
        final Publisher<String> publisher = s -> s.onSubscribe(newSubscriptionFrom(
                n -> {
                    log.debug("n: {}", n);
                    s.onComplete();
                    assertThrows(IllegalStateException.class, s::onComplete);
                },
                v -> log.debug("cancelled")
        ));
        publisher.subscribe(newSubscriberFrom(
                s -> {
                    log.debug("subscription: {}", s);
                    s.request(current().nextLong(1, 128));
                },
                t -> log.debug("next: {}", t),
                t -> log.debug("error: {}", t.getMessage(), t),
                v -> log.debug("completed")
        ));
    }
}
