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

public class RootGrabAbility extends PetAbility {

    private final double damage;
    private final int slowTicks;
    private final Particle particle;
    private final Sound sound;

    public RootGrabAbility(double damage, int slowTicks, Particle particle, Sound sound) {
        this.damage = damage;
        this.slowTicks = slowTicks;
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

        petLocation.getWorld().playSound(petLocation, sound, 0.8f, 0.8f);

        PathTracker tracker = PathTracker.createDynamicProjectileTracker(
                petLocation,
                target::getLocation
        );

        PetsAPI.getInstance().taskScheduler().runSyncRepeating(() -> {
            if (!tracker.tick()) {
                return false;
            }

            Location current = tracker.getCurrentLocation();
            current.getWorld().spawnParticle(particle, current, 5, 0.12, 0.12, 0.12, 0.02);
            current.getWorld().spawnParticle(Particle.BLOCK_CRACK, current, 3, 0.12, 0.12, 0.12, 0.01, Material.DIRT.createBlockData());

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
                current.getWorld().spawnParticle(Particle.BLOCK_CRACK, current, 20, 0.4, 0.1, 0.4, 0.05, Material.OAK_LEAVES.createBlockData());
                target.damage(damage);
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, slowTicks, 3));
                tracker.forceComplete();
            }

            return true;
        }, 1L, 1L);
    }

    public static RootGrabAbility fromConfig(ConfigurationSection section) {
        double damage = section.getDouble("damage", 4.0);
        int slowTicks = section.getInt("slow-ticks", 80);
        Particle particle = Particle.valueOf(section.getString("particle", "BLOCK_CRACK"));
        Sound sound = Sound.valueOf(section.getString("sound", "BLOCK_GRASS_BREAK"));
        return new RootGrabAbility(damage, slowTicks, particle, sound);
    }

    @Override
    public String getName() {
        return "root-grab";
    }
}
