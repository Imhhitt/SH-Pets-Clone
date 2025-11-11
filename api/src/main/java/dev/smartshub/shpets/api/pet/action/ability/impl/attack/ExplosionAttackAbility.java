package dev.smartshub.shpets.api.pet.action.ability.impl.attack;

import dev.smartshub.shpets.api.PetsAPI;
import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import dev.smartshub.shpets.api.pet.action.ability.path.PathTracker;
import dev.smartshub.shpets.api.service.context.PetContextService;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ExplosionAttackAbility extends PetAbility {

    private final double power;
    private final Particle particle;
    private final Sound sound;

    public ExplosionAttackAbility(double power, Particle particle, Sound sound) {
        this.power = power;
        this.particle = particle;
        this.sound = sound;
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        var petLocation = PetContextService.getPetLocation(petData.getUniqueId());
        var entity = Bukkit.getEntity(PetContextService.getPetTarget(petData.getUniqueId()));

        if (petLocation == null || !(entity instanceof LivingEntity target)) return;

        World world = petLocation.getWorld();
        world.playSound(petLocation, sound, 0.8f, 1.1f);

        PathTracker tracker = PathTracker.createDynamicProjectileTracker(
                petLocation,
                target::getLocation
        );

        PetsAPI.getInstance().taskScheduler().runSyncRepeating(() -> {
            if (!tracker.tick()) {
                return false;
            }

            Location current = tracker.getCurrentLocation();
            current.getWorld().spawnParticle(Particle.SMOKE_NORMAL, current, 3, 0.2, 0.2, 0.2, 0.01);
            current.getWorld().spawnParticle(particle, current, 1, 0, 0, 0, 0);

            if (tracker.getCurrentPhase() == PathTracker.PathPhase.RETURNING && tracker.getTickCount() == 1) {
                World w = current.getWorld();
                w.spawnParticle(Particle.EXPLOSION_NORMAL, current, 1);
                w.playSound(current, sound, 1f, 1f);
                w.createExplosion(current, (float) power, false, false);
            }

            return true;
        }, 0L, 1L);
    }

    public static ExplosionAttackAbility fromConfig(ConfigurationSection section) {
        double power = section.getDouble("power", 2.0);
        Particle particle = Particle.valueOf(section.getString("particle", "EXPLOSION_LARGE"));
        Sound sound = Sound.valueOf(section.getString("sound", "ENTITY_GENERIC_EXPLODE"));
        return new ExplosionAttackAbility(power, particle, sound);
    }

    @Override
    public String getName() {
        return "explosion-attack";
    }
}
