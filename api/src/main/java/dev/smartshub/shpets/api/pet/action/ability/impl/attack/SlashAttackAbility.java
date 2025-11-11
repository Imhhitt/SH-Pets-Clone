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

public class SlashAttackAbility extends PetAbility {

    private final double damage;
    private final double range;
    private final Particle particle;
    private final Sound sound;

    public SlashAttackAbility(double damage, double range, Particle particle, Sound sound) {
        this.damage = damage;
        this.range = range;
        this.particle = particle;
        this.sound = sound;
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        var petLocation = PetContextService.getPetLocation(petData.getUniqueId());
        var entity = Bukkit.getEntity(PetContextService.getPetTarget(petData.getUniqueId()));

        if(!(entity instanceof LivingEntity target)) {
            return;
        }

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

            current.getWorld().spawnParticle(particle, current, 4, 0.2, 0.2, 0.2, 0);
            current.getWorld().playSound(current, sound, 1f, 1f);

            if (tracker.getCurrentPhase() == PathTracker.PathPhase.RETURNING &&
                    tracker.getTickCount() == 1) {
                target.damage(damage);
            }

            return true;
        }, 0L, 1L);
    }

    public static SlashAttackAbility fromConfig(ConfigurationSection section) {
        double damage = section.getDouble("damage", 6.0);
        double range = section.getDouble("range", 8.0);
        Particle particle = Particle.valueOf(section.getString("particle", "SWEEP_ATTACK"));
        Sound sound = Sound.valueOf(section.getString("sound", "ENTITY_PLAYER_ATTACK_SWEEP"));
        return new SlashAttackAbility(damage, range, particle, sound);
    }

    @Override
    public String getName() {
        return "slash-attack";
    }
}
