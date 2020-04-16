package org.lxz.utils.rxjava;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by Lin on 2017/4/28.
 */

public class RetryWithDelay implements Function<Observable<Throwable>, ObservableSource<?>>{

    private final int maxRetries;
    private final int retryDelayMillis;
    private int retryCount;
        public RetryWithDelay(int maxRetries, int retryDelayMillis) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
    }

    @Override
    public ObservableSource<?> apply(Observable<Throwable> attempts) throws Exception {
        return attempts.flatMap(new Function<Object, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Object o) throws Exception {
                if (++retryCount <= maxRetries) {
                    // When this Observable calls onNext, the original Observable will be retried (i.e. re-subscribed).
                    return Observable.timer(retryDelayMillis,
                            TimeUnit.MILLISECONDS);
                }
                // Max retries hit. Just pass the error along.
                return Observable.error((Throwable) o);
            }
        });
    }
}
