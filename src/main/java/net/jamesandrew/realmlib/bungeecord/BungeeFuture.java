package net.jamesandrew.realmlib.bungeecord;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Easier handling of {@link CompletableFuture}'s where the throwable
 * portion of the {@link BiConsumer} is handled at the beginning of the
 * consumer, then returned
 * @param <T> the type of {@link BungeeFuture}
 */
public final class BungeeFuture<T> {
    private final CompletableFuture<T> future;

    BungeeFuture() {
        this.future = new CompletableFuture<>();
    }

    synchronized void complete(T t) {
        future.complete(t);
    }


    synchronized void completeExceptionally(Throwable e) {
        future.completeExceptionally(e);
    }

    /**
     * If you wish to handle the {@link Throwable} yourself.
     * This is identical to {@link CompletableFuture}'s {@code #whenComplete()}
     *
     * @param bi the {@link BiConsumer} to perform on the {@link CompletableFuture}
     * @return the {@link CompletableFuture} associated with this object
     */
    public synchronized CompletableFuture<T> thenFuture(BiConsumer<T, Throwable> bi) {
        return future.whenComplete(bi);
    }

    /**
     * Identical to {@link CompletableFuture}'s {@code #whenComplete()},
     * but the {@link Throwable} is handled for you. The {@link CompletableFuture}
     * prints the stacktrace and then returns before accepting the {@link Consumer}
     *
     * @param consumer the consumer to accept
     * @return this {@link BungeeFuture}
     */
    public synchronized BungeeFuture<T> then(Consumer<T> consumer) {
        future.whenComplete((t, e) -> {
            if (e != null) {
                e.printStackTrace();
                return;
            }
            consumer.accept(t);
        });

        return this;
    }

    /**
     * Transforms the type of {@link BungeeFuture}
     * @param mapper the mapping function that applies the type
     * @param <U> the type to apply
     * @return this {@link BungeeFuture}
     */
    public synchronized <U> BungeeFuture<U> transform(Function<? super T, U> mapper) {
        BungeeFuture<U> bungeeFuture = new BungeeFuture<>();
        then(t -> bungeeFuture.complete(mapper.apply(t)));
        return bungeeFuture;
    }

}
