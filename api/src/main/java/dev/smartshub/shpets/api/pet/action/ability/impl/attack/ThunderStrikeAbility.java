package dev.smartshub.shpets.api.pet.action.ability.impl.attack;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import dev.smartshub.shpets.api.service.context.PetContextService;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ThunderStrikeAbility extends PetAbility {

    private final double damage;
    private final Particle particle;
    private final Sound sound;

    public ThunderStrikeAbility(double damage, Particle particle, Sound sound) {
        this.damage = damage;
        this.particle = particle;
        this.sound = sound;
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        var entity = Bukkit.getEntity(PetContextService.getPetTarget(petData.getUniqueId()));

        if(!(entity instanceof LivingEntity target)) {
            return;
        }

        Location loc = target.getLocation();
        loc.getWorld().spawnParticle(particle, loc, 20, 0.5, 0.5, 0.5, 0);
        loc.getWorld().playSound(loc, sound, 1f, 1f);
        loc.getWorld().strikeLightningEffect(loc);
        target.damage(damage);
    }

    public static ThunderStrikeAbility fromConfig(ConfigurationSection section) {
        double damage = section.getDouble("damage", 8.0);
        Particle particle = Particle.valueOf(section.getString("particle", "ELECTRIC_SPARK"));
        Sound sound = Sound.valueOf(section.getString("sound", "ENTITY_LIGHTNING_BOLT_THUNDER"));
        return new ThunderStrikeAbility(damage, particle, sound);
    }

    @Override
    public String getName() {
        return "thunder-strike";
    }
}
