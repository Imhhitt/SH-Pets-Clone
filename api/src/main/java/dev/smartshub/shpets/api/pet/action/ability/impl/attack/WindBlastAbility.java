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

public class WindBlastAbility extends PetAbility {

    private final double knockback;
    private final Particle particle;
    private final Sound sound;

    public WindBlastAbility(double knockback, Particle particle, Sound sound) {
        this.knockback = knockback;
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

        petLocation.getWorld().playSound(petLocation, sound, 0.8f, 1.3f);

        PathTracker tracker = PathTracker.createDynamicProjectileTracker(
                petLocation,
                target::getLocation
        );

        PetsAPI.getInstance().taskScheduler().runSyncRepeating(() -> {
            if (!tracker.tick()) {
                return false;
            }

            Location current = tracker.getCurrentLocation();
            current.getWorld().spawnParticle(Particle.CLOUD, current, 6, 0.15, 0.15, 0.15, 0.02);
            current.getWorld().spawnParticle(particle, current, 3, 0.12, 0.12, 0.12, 0.01);

            if (tracker.getCurrentPhase() == PathTracker.PathPhase.RETURNING && tracker.getTickCount() == 1) {
                Vector push = target.getLocation().toVector().subtract(petLocation.toVector()).normalize().multiply(knockback);
                target.setVelocity(push);
                current.getWorld().playSound(current, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.8f, 1.6f);
            }

            return true;
        }, 0L, 1L);
    }

    public static WindBlastAbility fromConfig(ConfigurationSection section) {
        double knockback = section.getDouble("knockback", 1.2);
        Particle particle = Particle.valueOf(section.getString("particle", "CLOUD"));
        Sound sound = Sound.valueOf(section.getString("sound", "ENTITY_ENDER_DRAGON_FLAP"));
        return new WindBlastAbility(knockback, particle, sound);
    }

    @Override
    public String getName() {
        return "wind-blast";
    }
}
