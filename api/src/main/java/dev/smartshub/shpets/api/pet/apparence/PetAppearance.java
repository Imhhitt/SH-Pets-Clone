package dev.smartshub.shpets.api.pet.apparence;

import dev.smartshub.shpets.api.pet.template.EquipmentData;
import dev.smartshub.shpets.api.pet.template.GlowData;

public record PetAppearance(
        String displayName,
        boolean showNameTag,
        GlowData glow,
        EquipmentData equipment
) {}