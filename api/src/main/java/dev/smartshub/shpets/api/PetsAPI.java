package dev.smartshub.shpets.api;

import dev.smartshub.shpets.api.registry.PetInstanceRegistry;
import dev.smartshub.shpets.api.registry.PetTemplateRegistry;
import dev.smartshub.shpets.api.registry.Registry;
import dev.smartshub.shpets.api.service.glow.GlowService;
import dev.smartshub.shpets.api.service.placeholder.PlaceholderService;
import dev.smartshub.shpets.api.service.skull.SkullService;
import dev.smartshub.shpets.api.service.particle.ParticleService;

public record PetsAPI(
    PlaceholderService placeholderService,
    SkullService skullService,
    GlowService glowService,
    ParticleService particleService,

    PetTemplateRegistry petTemplateRegistry,
    PetInstanceRegistry petInstanceRegistry,
    Registry nmsEntityRegistry
) {

    private static PetsAPI instance;

    public static PetsAPI getInstance() {
        return instance;
    }

    public static void setInstance(final PetsAPI instance) {
        PetsAPI.instance = instance;
    }
}
