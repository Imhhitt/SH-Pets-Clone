package dev.smartshub.shpets.api.pet.action.ability.impl.attack;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import dev.smartshub.shpets.api.pet.action.ability.path.PathTracker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ProyectileAttackAbility extends PetAbility {

    private final double damage;
    private final double range;

    public ProyectileAttackAbility(double damage, double range) {
        this.damage = damage;
        this.range = range;
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        Location petLocation = null;
        LivingEntity target = null;

        if (target == null) {
            return;
        }

        PathTracker tracker = PathTracker.createDynamicMeleeTracker(
                petLocation,
                target::getLocation,
                petLocation.clone()
        );

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!tracker.tick()) {
                    cancel();
                    return;
                }

                Location current = tracker.getCurrentLocation();

                current.getWorld().spawnParticle(
                        Particle.CRIT,
                        current,
                        2,
                        0.1, 0.1, 0.1,
                        0
                );

                if (tracker.getCurrentPhase() == PathTracker.PathPhase.RETURNING &&
                        tracker.getTickCount() == 1) {

                    target.damage(damage);
                    current.getWorld().playSound(current, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("SHPets"), 0L, 1L);
    }

    public static ProyectileAttackAbility fromConfig(ConfigurationSection section) {
        double damage = section.getDouble("damage", 5.0);
        double range = section.getDouble("range", 10.0);
        return new ProyectileAttackAbility(damage, range);
    }

    @Override
    public String getName() {
        return "MeleeAttack";
    }
}