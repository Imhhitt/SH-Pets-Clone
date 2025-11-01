package dev.smartshub.shpets.api.pet;

import dev.smartshub.shpets.api.pet.action.ability.Ability;
import dev.smartshub.shpets.api.pet.template.PetTemplate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PetData {
    private final @NotNull Player owner;
    private final @NotNull PetTemplate template;
    private @Nullable UUID uniqueId;
    private @NotNull PetState state;
    private final Map<Ability, Long> abilityCooldowns = new HashMap<>();

    public PetData(final @NotNull Player owner, final @NotNull PetTemplate template) {
        this.owner = owner;
        this.template = template;
        this.uniqueId = UUID.randomUUID();
        this.state = PetState.STORED;
    }

    public @NotNull Player getOwner() {
        return owner;
    }

    public @NotNull PetTemplate getTemplate() {
        return template;
    }

    public @Nullable UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(@NotNull final UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public @NotNull PetState getState() {
        return state;
    }

    public void setState(@NotNull final PetState state) {
        this.state = state;
    }

    public Map<Ability, Long> getAbilityCooldowns() {
        return abilityCooldowns;
    }

}