package dev.smartshub.shpets.api.registry;

import dev.smartshub.shpets.api.pet.template.PetTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for pet templates loaded from configuration
 */
public class PetTemplateRegistry implements Registry<PetTemplate, String> {

    private final Map<String, PetTemplate> templates = new ConcurrentHashMap<>();

    @Override
    public void register(@NotNull PetTemplate template) {
        templates.put(template.id().toLowerCase(), template);
    }

    @Override
    public void unregister(@NotNull PetTemplate template) {
        templates.remove(template.id().toLowerCase());
    }

    @Override
    @Nullable
    public PetTemplate get(@NotNull String id) {
        return templates.get(id.toLowerCase());
    }

    @Override
    @NotNull
    public Collection<PetTemplate> getAll() {
        return templates.values();
    }

    @Override
    public boolean exists(@NotNull String id) {
        return templates.containsKey(id.toLowerCase());
    }

    @Override
    public void clear() {
        templates.clear();
    }

    public int size() {
        return templates.size();
    }
}