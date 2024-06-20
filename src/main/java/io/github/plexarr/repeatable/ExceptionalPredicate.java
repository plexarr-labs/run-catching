package io.github.plexarr.repeatable;

public interface ExceptionalPredicate<T, E extends Exception> {
    boolean test(T value, E exception);
}
