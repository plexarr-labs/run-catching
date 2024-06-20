package io.github.plexarr.repeatable;

import java.util.function.IntFunction;
import java.util.function.Supplier;

public abstract class Repeatable<T, E extends Exception>{
    protected int times;
    protected IntFunction<T> function;
    protected long delay;
    protected ExceptionalPredicate<T, E> condition;
    protected Supplier<T> orElse;

    public abstract T run();
}