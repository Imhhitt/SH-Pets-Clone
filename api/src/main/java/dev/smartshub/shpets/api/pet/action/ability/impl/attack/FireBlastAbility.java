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
import org.bukkit.scheduler.BukkitRunnable;

public class FireBlastAbility extends PetAbility {

    private final double damage;
    private final double range;
    private final Particle particle;

    public FireBlastAbility(double damage, double range, Particle particle) {
        this.damage = damage;
        this.range = range;
        this.particle = particle;
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        var petLocation = PetContextService.getPetLocation(petData.getUniqueId());
        var entity = Bukkit.getEntity(PetContextService.getPetTarget(petData.getUniqueId()));

        if(!(entity instanceof LivingEntity target)) {
            return;
        }

        PathTracker tracker = PathTracker.createDynamicProjectileTracker(
                petLocation,
                target::getLocation
        );

        PetsAPI.getInstance().taskScheduler().runSyncRepeating(() -> {
            if (!tracker.tick()) {
                return false;
            }

            Location current = tracker.getCurrentLocation();
            current.getWorld().spawnParticle(particle, current, 4, 0.1, 0.1, 0.1, 0.05);

            boolean didHit = tracker.consumeHit();
            if (!didHit) {
                if (current.getWorld() == target.getWorld()) {
                    if (current.distanceSquared(target.getLocation()) <= 0.36) {
                        didHit = true;
                    }
                }
            }

            if (didHit) {
                target.damage(damage);
                target.setFireTicks(80);
                current.getWorld().playSound(current, Sound.ITEM_FIRECHARGE_USE, 1, 1);
                tracker.forceComplete();
            }

            return true;
        }, 0L, 1L);
    }

    public static FireBlastAbility fromConfig(ConfigurationSection section) {
        double damage = section.getDouble("damage", 6.0);
        double range = section.getDouble("range", 14.0);
        Particle particle = Particle.valueOf(section.getString("particle", "FLAME"));
        return new FireBlastAbility(damage, range, particle);
    }

    @Override
    public String getName() {
        return "fire-blast";
    }
}
