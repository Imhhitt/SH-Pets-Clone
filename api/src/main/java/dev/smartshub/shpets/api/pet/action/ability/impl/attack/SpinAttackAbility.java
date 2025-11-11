package dev.smartshub.shpets.api.pet.action.ability.impl.attack;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import dev.smartshub.shpets.api.service.context.PetContextService;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

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

        if(!(entity instanceof LivingEntity target)) {
            return;
        }

        petLocation.getWorld().spawnParticle(particle, petLocation, 50, 0.5, 0.5, 0.5, 0.1);
        petLocation.getWorld().playSound(petLocation, sound, 1f, 1f);

        //TODO: Implement spin attack
    }

    public static SpinAttackAbility fromConfig(ConfigurationSection section) {
        double damage = section.getDouble("damage", 4.0);
        double radius = section.getDouble("radius", 5.0);
        Particle particle = Particle.valueOf(section.getString("particle", "SWEEP_ATTACK"));
        Sound sound = Sound.valueOf(section.getString("sound", "ENTITY_PLAYER_ATTACK_SWEEP"));
        return new SpinAttackAbility(damage, radius, particle, sound);
    }

    @Override
    public String getName() {
        return "spin-attack";
    }
}
