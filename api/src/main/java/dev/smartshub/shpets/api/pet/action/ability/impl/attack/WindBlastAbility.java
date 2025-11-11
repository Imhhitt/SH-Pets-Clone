package dev.smartshub.shpets.api.pet.action.ability.impl.attack;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
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

        if(!(entity instanceof LivingEntity target)) {
            return;
        }

        Location loc = target.getLocation();
        loc.getWorld().spawnParticle(particle, loc, 20, 0.4, 0.4, 0.4, 0);
        loc.getWorld().playSound(loc, sound, 1f, 1.3f);

        Vector push = loc.toVector().subtract(petLocation.toVector()).normalize().multiply(knockback);
        target.setVelocity(push);
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
