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

public class SpinAttackAbility extends PetAbility {

    private final double damage;
    private final double radius;
    private final Particle particle;
    private final Sound sound;

    public SpinAttackAbility(double damage, double radius, Particle particle, Sound sound) {
        this.damage = damage;
        this.radius = radius;
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

        petLocation.getWorld().playSound(petLocation, sound, 1f, 1.2f);

        PathTracker tracker = PathTracker.createDynamicMeleeTracker(
                petLocation,
                target::getLocation,
                petLocation.clone()
        );

        PetsAPI.getInstance().taskScheduler().runSyncRepeating(() -> {
            if (!tracker.tick()) {
                return false;
            }

            Location current = tracker.getCurrentLocation();
            // Spin arc visuals around current point
            current.getWorld().spawnParticle(Particle.SWEEP_ATTACK, current, 1, 0, 0, 0, 0);
            current.getWorld().spawnParticle(particle, current, 6, 0.3, 0.15, 0.3, 0.02);

            boolean didHit = tracker.consumeHit();
            if (!didHit) {
                if (current.getWorld() == target.getWorld()) {
                    if (current.distanceSquared(target.getLocation()) <= 0.36) {
                        didHit = true;
                    }
                }
            }

            if (didHit) {
                current.getWorld().playSound(current, sound, 1f, 1f);
                // Damage target and optionally nearby enemies within radius
                for (var entityNearby : current.getWorld().getNearbyEntities(current, radius, radius / 2, radius)) {
                    if (entityNearby instanceof LivingEntity living) {
                        living.damage(damage);
                    }
                }
            }

            return true;
        }, 0L, 1L);
    }

    public static SpinAttackAbility fromConfig(ConfigurationSection section) {
        double damage = section.getDouble("damage", 4.0);
        double radius = section.getDouble("radius", 3.0);
        Particle particle = Particle.valueOf(section.getString("particle", "SWEEP_ATTACK"));
        Sound sound = Sound.valueOf(section.getString("sound", "ENTITY_PLAYER_ATTACK_SWEEP"));
        return new SpinAttackAbility(damage, radius, particle, sound);
    }

    @Override
    public String getName() {
        return "spin-attack";
    }
}
