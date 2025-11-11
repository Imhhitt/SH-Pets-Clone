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
import org.bukkit.util.Vector;

public class FireballAttackAbility extends PetAbility {

    private final double damage;
    private final Particle particle;
    private final Sound sound;

    public FireballAttackAbility(double damage, Particle particle, Sound sound) {
        this.damage = damage;
        this.particle = particle;
        this.sound = sound;
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        var petLocation = PetContextService.getPetLocation(petData.getUniqueId());
        var entity = Bukkit.getEntity(PetContextService.getPetTarget(petData.getUniqueId()));

        if (petLocation == null || !(entity instanceof LivingEntity target)) {
            return;
        }

        petLocation.getWorld().spawnParticle(particle, petLocation, 10, 0.2, 0.2, 0.2, 0);
        petLocation.getWorld().playSound(petLocation, sound, 1f, 1f);

        PathTracker tracker = PathTracker.createDynamicProjectileTracker(
                petLocation,
                target::getLocation
        );

        PetsAPI.getInstance().taskScheduler().runSyncRepeating(() -> {
            if (!tracker.tick()) {
                return false;
            }

            Location current = tracker.getCurrentLocation();

            current.getWorld().spawnParticle(Particle.FLAME, current, 5, 0.1, 0.1, 0.1, 0.02);
            current.getWorld().spawnParticle(Particle.SMOKE_NORMAL, current, 2, 0.1, 0.1, 0.1, 0.01);
            current.getWorld().spawnParticle(particle, current, 3, 0.15, 0.15, 0.15, 0.03);

            boolean didHit = tracker.consumeHit();
            if (!didHit) {
                // Fallback proximity hit check for moving/offset targets (e.g., sheep head bobbing)
                if (current.getWorld() == target.getWorld()) {
                    if (current.distanceSquared(target.getLocation()) <= 0.36) { // within 0.6 blocks
                        didHit = true;
                    }
                }
            }

            if (didHit) {
                current.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, current, 1);
                current.getWorld().spawnParticle(Particle.FLAME, current, 30, 0.5, 0.5, 0.5, 0.1);
                current.getWorld().playSound(current, Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 1.2f);

                target.damage(damage);
                target.setFireTicks(60);
                tracker.forceComplete();
            }

            return true;
        }, 0L, 1L);
    }

    public static FireballAttackAbility fromConfig(ConfigurationSection section) {
        double damage = section.getDouble("damage", 5.0);
        Particle particle = Particle.valueOf(section.getString("particle", "FLAME"));
        Sound sound = Sound.valueOf(section.getString("sound", "ENTITY_GHAST_SHOOT"));
        return new FireballAttackAbility(damage, particle, sound);
    }

    @Override
    public String getName() {
        return "fireball-attack";
    }
}