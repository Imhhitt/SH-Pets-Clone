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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class IceBeamAttackAbility extends PetAbility {

    private final double damage;
    private final double healAmount;
    private final Particle particle;
    private final Sound sound;

    public IceBeamAttackAbility(double damage, double healAmount, Particle particle, Sound sound) {
        this.damage = damage;
        this.healAmount = healAmount;
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

        petLocation.getWorld().playSound(petLocation, sound, 0.9f, 1.1f);

        PathTracker tracker = PathTracker.createDynamicProjectileTracker(
                petLocation,
                target::getLocation
        );

        PetsAPI.getInstance().taskScheduler().runSyncRepeating(() -> {
            if (!tracker.tick()) {
                return false;
            }

            Location current = tracker.getCurrentLocation();
            current.getWorld().spawnParticle(Particle.SNOWFLAKE, current, 4, 0.1, 0.1, 0.1, 0.02);
            current.getWorld().spawnParticle(particle, current, 2, 0.1, 0.1, 0.1, 0.01);

            boolean didHit = tracker.consumeHit();
            if (!didHit) {
                if (current.getWorld() == target.getWorld()) {
                    if (current.distanceSquared(target.getLocation()) <= 0.36) {
                        didHit = true;
                    }
                }
            }

            if (didHit) {
                current.getWorld().playSound(current, Sound.BLOCK_GLASS_BREAK, 1f, 1.4f);
                current.getWorld().spawnParticle(Particle.SNOWBALL, current, 10, 0.3, 0.3, 0.3, 0.05);
                target.damage(damage);
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));

                // heal pet owner
                Player owner = petData.getOwner();
                if (owner != null && owner.isOnline()) {
                    double newHealth = Math.min(owner.getHealth() + healAmount, owner.getMaxHealth());
                    owner.setHealth(newHealth);
                    owner.getWorld().spawnParticle(Particle.HEART, owner.getLocation().add(0, 1, 0), 5, 0.3, 0.3, 0.3, 0.02);
                    owner.playSound(owner.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 1.6f);
                }
                tracker.forceComplete();
            }

            return true;
        }, 1L, 1L);
    }

    public static IceBeamAttackAbility fromConfig(ConfigurationSection section) {
        double damage = section.getDouble("damage", 5.0);
        double heal = section.getDouble("heal", 2.0);
        Particle particle = Particle.valueOf(section.getString("particle", "SNOWFLAKE"));
        Sound sound = Sound.valueOf(section.getString("sound", "BLOCK_GLASS_BREAK"));
        return new IceBeamAttackAbility(damage, heal, particle, sound);
    }

    @Override
    public String getName() {
        return "ice-beam-attack";
    }
}
