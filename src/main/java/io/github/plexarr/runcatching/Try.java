package io.github.plexarr.runcatching;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a functional implementation of try-catch in Java, providing a concise
 * and expressive way to handle exceptions and compose operations that may throw exceptions.
 * This class allows wrapping potentially throwing operations and provides methods for
 * handling both successful and failed computations in a functional style.
 */
public class Try<T> {
    private final T value;
    private final Exception exception;

    private Try(T value, Exception exception) {
        this.value = value;
        this.exception = exception;
    }

    public static <T> Try<T> runCatching(ExceptionalSupplier<T> supplier) {
        try {
            T result = supplier.get();
            return new Try<>(result, null);
        } catch (Exception e) {
            return new Try<>(null, e);
        }
    }

    public static Try<Void> runCatching(ExceptionalRunnable runnable) {
        try {
            runnable.run();
            return new Try<>(null, null);
        } catch (Exception ex) {
            return new Try<>(null, ex);
        }
    }

    public static <T> T runCatchingOrElse(ExceptionalSupplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static <T, E extends Exception> T runCatchingOrThrow(
            ExceptionalSupplier<T> supplier, Function<? super Throwable, E> exceptionProvider) throws E {
        try {
            return supplier.get();
        } catch (Exception ex) {
            throw exceptionProvider.apply(ex);
        }
    }

    public static <E extends Exception> void runCatchingOrThrow(
            ExceptionalRunnable runnable, Function<? super Throwable, E> exceptionProvider) throws E {
        try {
            runnable.run();
        } catch (Exception ex) {
            throw exceptionProvider.apply(ex);
        }
    }

    public boolean isSuccess() {
        return this.exception == null;
    }

    public boolean isFailure() {
        return this.exception != null;
    }

    public Try<T> onFailure(Consumer<Exception> errorHandler) {
        if (isFailure()) {
            errorHandler.accept(this.exception);
        }
        return this;
    }

    public Try<T> onSuccess(Consumer<T> successHandler) {
        if (isSuccess()) {
            successHandler.accept(this.value);
        }
        return this;
    }

    public T get() {
        if (this.value == null) {
            throw new NoSuchElementException("No value present");
        }
        return this.value;
    }

    public T getOrNull() {
        return isSuccess() ? this.value : null;
    }

    public T getOrDefault(T defaultValue) {
        return isSuccess() ? this.value : defaultValue;
    }

    public T getOrElse(Function<? super Throwable, ? extends T> function) {
        Objects.requireNonNull(function, "other is null");
        return isSuccess() ? this.value : function.apply(this.exception);
    }

    public T getOrElse(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier, "supplier is null");
        return isSuccess() ? this.value : supplier.get();
    }

    public <E extends Exception> T getOrThrow(Function<? super Throwable, E> exceptionProvider) throws E {
        Objects.requireNonNull(exceptionProvider, "exceptionProvider is null");
        if (isSuccess()) {
            return this.value;
        } else {
            throw exceptionProvider.apply(this.exception);
        }
    }
}