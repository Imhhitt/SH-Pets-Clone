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

public class ThunderStrikeAbility extends PetAbility {

    private final double damage;
    private final Particle particle;
    private final Sound sound;

    public ThunderStrikeAbility(double damage, Particle particle, Sound sound) {
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

        petLocation.getWorld().playSound(petLocation, sound, 0.7f, 1.2f);

        PathTracker tracker = PathTracker.createDynamicProjectileTracker(
                petLocation,
                target::getLocation
        );

        PetsAPI.getInstance().taskScheduler().runSyncRepeating(() -> {
            if (!tracker.tick()) {
                return false;
            }

            Location current = tracker.getCurrentLocation();
            current.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, current, 8, 0.1, 0.1, 0.1, 0.02);
            current.getWorld().spawnParticle(particle, current, 4, 0.1, 0.1, 0.1, 0.01);

            boolean didHit = tracker.consumeHit();
            if (!didHit) {
                if (current.getWorld() == target.getWorld()) {
                    if (current.distanceSquared(target.getLocation()) <= 0.36) {
                        didHit = true;
                    }
                }
            }

            if (didHit) {
                current.getWorld().strikeLightningEffect(current);
                current.getWorld().playSound(current, sound, 1f, 1f);
                target.damage(damage);
                tracker.forceComplete();
            }

            return true;
        }, 0L, 1L);
    }

    public static ThunderStrikeAbility fromConfig(ConfigurationSection section) {
        double damage = section.getDouble("damage", 8.0);
        Particle particle = Particle.valueOf(section.getString("particle", "ELECTRIC_SPARK"));
        Sound sound = Sound.valueOf(section.getString("sound", "ENTITY_LIGHTNING_BOLT_THUNDER"));
        return new ThunderStrikeAbility(damage, particle, sound);
    }

    @Override
    public String getName() {
        return "thunder-strike";
    }
}
