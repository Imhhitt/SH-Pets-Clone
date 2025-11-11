package dev.smartshub.shpets.api.pet.action.ability.impl.attack;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import dev.smartshub.shpets.api.service.context.PetContextService;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class HealingAuraAbility extends PetAbility {

    private final double healAmount;
    private final double radius;
    private final Particle particle;
    private final Sound sound;

    public HealingAuraAbility(double healAmount, double radius, Particle particle, Sound sound) {
        this.healAmount = healAmount;
        this.radius = radius;
        this.particle = particle;
        this.sound = sound;
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        var petLocation = PetContextService.getPetLocation(petData.getUniqueId());
        if (petLocation == null) return;

        World world = petLocation.getWorld();
        world.spawnParticle(particle, petLocation, 40, 0.8, 0.4, 0.8, 0.03);
        world.playSound(petLocation, sound, 1f, 1.2f);

        for (var nearby : world.getNearbyEntities(petLocation, radius, radius / 2, radius)) {
            if (nearby instanceof Player p) {
                double newHealth = Math.min(p.getHealth() + healAmount, p.getMaxHealth());
                p.setHealth(newHealth);
                world.spawnParticle(Particle.HEART, p.getLocation().add(0, 1, 0), 5, 0.3, 0.3, 0.3, 0.02);
            }
        }
    }

    public static HealingAuraAbility fromConfig(ConfigurationSection section) {
        double heal = section.getDouble("heal", 4.0);
        double radius = section.getDouble("radius", 5.0);
        Particle particle = Particle.valueOf(section.getString("particle", "HEART"));
        Sound sound = Sound.valueOf(section.getString("sound", "ENTITY_EXPERIENCE_ORB_PICKUP"));
        return new HealingAuraAbility(heal, radius, particle, sound);
    }

    @Override
    public String getName() {
        return "healing-aura";
    }
}
