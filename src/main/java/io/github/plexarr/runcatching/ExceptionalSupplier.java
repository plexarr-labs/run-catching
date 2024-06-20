package io.github.plexarr.runcatching;

@FunctionalInterface
public interface ExceptionalSupplier<T> {
    T get() throws Exception;
}
