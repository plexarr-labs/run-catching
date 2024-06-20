package io.github.plexarr.repeatable;

import java.util.function.IntFunction;
import java.util.function.Supplier;

public class Repeat <T, E extends Exception> extends Repeatable<T, E> {

    private Repeat(int times, IntFunction<T> function) {
        this.times = times;
        this.function = function;
        this.delay = 0;
        this.condition = (value, exception) -> true;
        this.orElse = () -> null;
    }

    public static <T, E extends Exception> Repeat<T, E> of(int times, IntFunction<T> function) {
        return new Repeat<>(times, function);
    }

    public Repeat<T, E> repeatWhile(ExceptionalPredicate<T, E> condition) {
        if (condition != null) {
            this.condition = condition;
        }
        return this;
    }

    public Repeat<T, E> delay(long delayMillis) {
        this.delay = delayMillis;
        return this;
    }

    public Repeat<T, E> orElse(Supplier<T> orElse) {
        this.orElse = orElse;
        return this;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public T run() {
        T value = null;
        int attempts = 0;

        while (attempts < times) {
            E exception = null;
            try {
                value = function.apply(attempts);
            } catch (Exception ex) {
                exception = (E) ex;
            }
            if (!condition.test(value, exception)) {
                break;
            }
            attempts++;
            if (attempts < times) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        return value != null ? value : orElse.get();
    }
}
