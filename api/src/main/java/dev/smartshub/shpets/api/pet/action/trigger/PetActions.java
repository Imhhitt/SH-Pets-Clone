package dev.smartshub.shpets.api.pet.action.trigger;

import org.jetbrains.annotations.NotNull;

public record PetActions(
        @NotNull PetAction spawn,
        @NotNull PetAction despawn,
        @NotNull PetAction periodic,
        int periodicDelay,
        @NotNull PetAction interact,
        @NotNull PetAction hurt,
        @NotNull PetAction attack
) {

    public PetAction get(final TriggerType type) {
        return switch (type) {
            case ON_SPAWN -> spawn;
            case ON_DESPAWN -> despawn;
            case PERIODIC -> periodic;
            case ON_INTERACT -> interact;
            case ON_HURT -> hurt;
            case ON_ATTACK -> attack;
        };
    }
}
