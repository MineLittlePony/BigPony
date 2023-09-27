package com.minelittlepony.bigpony.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.util.Util;

public interface FutureUtils {
    Executor DELAYED_EXECUTOR = CompletableFuture.delayedExecutor(30, TimeUnit.SECONDS);

    static <T> CompletableFuture<T> waitFor(Consumer<Consumer<T>> action, Supplier<T> fallback) {
        CompletableFuture<T> waiter = CompletableFuture.supplyAsync(fallback, DELAYED_EXECUTOR);
        CompletableFuture.runAsync(() -> action.accept(waiter::complete), Util.getMainWorkerExecutor());
        return waiter;
    }

    @SuppressWarnings("unchecked")
    static <T> CompletableFuture<T> either(CompletableFuture<T> incomplete, Supplier<T> fallback) {
        return (CompletableFuture<T>)CompletableFuture.anyOf(
                incomplete,
                CompletableFuture.supplyAsync(fallback, DELAYED_EXECUTOR)
        );
    }
}
