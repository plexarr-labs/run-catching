package io.github.plexarr.repeatable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

public class Retry<T, E extends Exception> extends Repeatable<T, E> {

    private Function<E, T> orElse;
    private Consumer<E> onFailure;
    private Consumer<T> onSuccess;

    private Retry(int maxAttempts, IntFunction<T> function) {
        this.times = maxAttempts;
        this.function = function;
        this.delay = 0;
        this.onFailure = (ex) -> {};
        this.onSuccess = (value) -> {};
        this.orElse = null;
    }

    public static <T, E extends Exception> Retry<T, E> of(int maxAttempts, IntFunction<T> function) {
        return new Retry<>(maxAttempts, function);
    }

    public Retry<T, E> delay(long delayMillis) {
        this.delay = delayMillis;
        return this;
    }

    public Retry<T, E> orElse(Function<E, T> orElse) {
        this.orElse = orElse;
        return this;
    }

    public Retry<T, E> onFailure(Consumer<E> errorHandler) {
        this.onFailure = errorHandler;
        return this;
    }

    public Retry<T, E> onSuccess(Consumer<T> successHandler) {
        this.onSuccess = successHandler;
        return this;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public T run() {
        T value = null;
        int attempts = 0;

        while (attempts < times) {
            try {
                value = function.apply(attempts);
                onSuccess.accept(value);
                break;
            } catch (Exception ex) {
                onFailure.accept((E) ex);
                if (attempts == times-1) {
                    if (orElse != null) {
                        return orElse.apply((E) ex);
                    }
                    throw ex;
                }
            }
            attempts++;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }

        return value;
    }

}
