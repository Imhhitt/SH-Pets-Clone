package dev.smartshub.shpets.api.pet.action.ability.impl.attack;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import dev.smartshub.shpets.api.service.context.PetContextService;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ExplosionAttackAbility extends PetAbility {

    private final double power;
    private final Particle particle;
    private final Sound sound;

    public ExplosionAttackAbility(double power, Particle particle, Sound sound) {
        this.power = power;
        this.particle = particle;
        this.sound = sound;
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        var petLocation = PetContextService.getPetLocation(petData.getUniqueId());

        if (petLocation == null) return;

        World world = petLocation.getWorld();
        world.spawnParticle(particle, petLocation, 40, 0.5, 0.5, 0.5, 0.1);
        world.playSound(petLocation, sound, 1f, 0.8f);
        world.createExplosion(petLocation, (float) power, false, false);
    }

    public static ExplosionAttackAbility fromConfig(ConfigurationSection section) {
        double power = section.getDouble("power", 2.0);
        Particle particle = Particle.valueOf(section.getString("particle", "EXPLOSION_LARGE"));
        Sound sound = Sound.valueOf(section.getString("sound", "ENTITY_GENERIC_EXPLODE"));
        return new ExplosionAttackAbility(power, particle, sound);
    }

    @Override
    public String getName() {
        return "explosion-attack";
    }
}
