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
import org.bukkit.scheduler.BukkitRunnable;

public class FrostAttackAbility extends PetAbility {

    private final double damage;
    private final double range;
    private final Particle particle;

    public FrostAttackAbility(double damage, double range, Particle particle) {
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
            current.getWorld().spawnParticle(particle, current, 3, 0.1, 0.1, 0.1, 0.05);

            if (tracker.getCurrentPhase() == PathTracker.PathPhase.RETURNING &&
                    tracker.getTickCount() == 1) {
                target.damage(damage);
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
                current.getWorld().playSound(current, Sound.BLOCK_GLASS_BREAK, 1, 1);
            }

            return true;
        }, 0L, 1L);
    }

    public static FrostAttackAbility fromConfig(ConfigurationSection section) {
        double damage = section.getDouble("damage", 4.0);
        double range = section.getDouble("range", 12.0);
        Particle particle = Particle.valueOf(section.getString("particle", "SNOWFLAKE"));
        return new FrostAttackAbility(damage, range, particle);
    }

    @Override
    public String getName() {
        return "frost-attack";
    }
}
