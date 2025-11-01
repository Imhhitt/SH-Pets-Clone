package dev.smartshub.shpets.api.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public interface Registry<T, K> {
    void register(@NotNull T item);
    void unregister(@NotNull T item);

    @Nullable
    T get(@NotNull K k);

    @NotNull
    default Optional<T> find(@NotNull K k) {
        return Optional.ofNullable(get(k));
    }

    @NotNull
    Collection<T> getAll();

    boolean exists(@NotNull K k);

    void clear();
}