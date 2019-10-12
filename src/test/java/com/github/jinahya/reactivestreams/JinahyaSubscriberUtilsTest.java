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
        final Publisher<String> publisher = s -> s.onSubscribe(newSubscriptionFrom(
                n -> {
                    log.debug("n: {}", n);
                    for (long i = 0; i < n; i++) {
                        if (current().nextBoolean()) {
                            s.onError(new RuntimeException());
                            assertThrows(IllegalStateException.class, () -> s.onError(new RuntimeException()));
                            break;
                        }
                        if (current().nextBoolean()) {
                            s.onComplete();
                            assertThrows(IllegalStateException.class, s::onComplete);
                            break;
                        }
                        s.onNext(Long.toString(i));
                    }
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
