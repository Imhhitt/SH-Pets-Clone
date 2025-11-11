package dev.smartshub.shpets.api;

import dev.smartshub.shpets.api.registry.PetInstanceRegistry;
import dev.smartshub.shpets.api.registry.PetTemplateRegistry;
import dev.smartshub.shpets.api.registry.Registry;
import dev.smartshub.shpets.api.service.glow.GlowService;
import dev.smartshub.shpets.api.service.placeholder.PlaceholderService;
import dev.smartshub.shpets.api.service.scheduler.TaskScheduler;
import dev.smartshub.shpets.api.service.skull.SkullService;
import dev.smartshub.shpets.api.service.particle.ParticleService;

public record PetsAPI(
        PlaceholderService placeholderService,
        SkullService skullService,
        GlowService glowService,
        ParticleService particleService,
        TaskScheduler taskScheduler,

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

    public static void runAsync(Runnable runnable, long delay, long period) {
        if (instance == null || instance.taskScheduler == null) {
            throw new IllegalStateException("PetsAPI not initialized");
        }
        instance.taskScheduler.runAsync(runnable, delay, period);
    }

    public static void runSync(Runnable runnable, long delay, long period) {
        if (instance == null || instance.taskScheduler == null) {
            throw new IllegalStateException("PetsAPI not initialized");
        }
        instance.taskScheduler.runSync(runnable, delay, period);
    }
}