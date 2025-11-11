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

        petLocation.getWorld().spawnParticle(particle, petLocation, 60, 1, 0.5, 1, 0.05);
        petLocation.getWorld().playSound(petLocation, sound, 1f, 1f);

        //TODO: Implement healing nearby allies
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
