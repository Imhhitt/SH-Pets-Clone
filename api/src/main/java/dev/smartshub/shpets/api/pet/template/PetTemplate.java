package dev.smartshub.shpets.api.pet.template;

import dev.smartshub.shpets.api.pet.action.trigger.PetActions;
import dev.smartshub.shpets.api.pet.apparence.PetAppearance;
import dev.smartshub.shpets.api.pet.behavior.PetBehavior;
import org.jetbrains.annotations.NotNull;

public record PetTemplate(
        @NotNull String id,
        @NotNull String displayName,
        @NotNull String permission,
        @NotNull String entityType,
        @NotNull EntityData entityData,
        @NotNull PetBehavior behavior,
        @NotNull PetAppearance appearance,
        @NotNull PetActions actions
) {}