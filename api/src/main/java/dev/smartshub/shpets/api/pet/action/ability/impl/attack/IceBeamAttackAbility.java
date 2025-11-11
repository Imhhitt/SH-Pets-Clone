package dev.smartshub.shpets.api.pet.action.ability.impl.attack;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import dev.smartshub.shpets.api.service.context.PetContextService;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class IceBeamAttackAbility extends PetAbility {

    private final double damage;
    private final Particle particle;
    private final Sound sound;

    public IceBeamAttackAbility(double damage, Particle particle, Sound sound) {
        this.damage = damage;
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

        petLocation.getWorld().playSound(petLocation, sound, 1f, 1f);
        petLocation.getWorld().spawnParticle(particle, petLocation, 30, 0.2, 0.2, 0.2, 0.01);

        //TODO: Implement ice beam attack
    }

    public static IceBeamAttackAbility fromConfig(ConfigurationSection section) {
        double damage = section.getDouble("damage", 5.0);
        Particle particle = Particle.valueOf(section.getString("particle", "SNOWFLAKE"));
        Sound sound = Sound.valueOf(section.getString("sound", "BLOCK_GLASS_BREAK"));
        return new IceBeamAttackAbility(damage, particle, sound);
    }

    @Override
    public String getName() {
        return "ice-beam-attack";
    }
}
