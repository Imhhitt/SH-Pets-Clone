package dev.smartshub.shpets.plugin.pet.registry;

import dev.smartshub.shpets.api.registry.Registry;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NMSEntityRegistry implements Registry<Entity, UUID> {

    private final Map<UUID, Entity> entityMap = new HashMap<>();

    @Override
    public void register(@NotNull Entity item) {
        entityMap.put(item.getUUID(), item);
    }

    @Override
    public void unregister(@NotNull Entity item) {
        entityMap.remove(item.getUUID());
    }

    @Override
    @Nullable
    public Entity get(@NotNull UUID id) {
        return entityMap.get(id);
    }

    @Override
    public @NotNull Optional<Entity> find(@NotNull UUID id) {
        return Optional.ofNullable(entityMap.get(id));
    }

    @Override
    public @NotNull Collection<Entity> getAll() {
        return entityMap.values();
    }

    @Override
    public boolean exists(@NotNull UUID id) {
        return entityMap.containsKey(id);
    }

    @Override
    public void clear() {
        entityMap.clear();
    }
}
