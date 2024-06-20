package io.github.plexarr.runcatching;

@FunctionalInterface
public interface ExceptionalRunnable {
    void run() throws Exception;
}
