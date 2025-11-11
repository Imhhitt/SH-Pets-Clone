package dev.smartshub.shpets.api.pet.action.ability.impl.attack;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import dev.smartshub.shpets.api.service.context.PetContextService;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class LightningStrikeAbility extends PetAbility {

    private final double damage;
    private final Particle particle;

    public LightningStrikeAbility(double damage, Particle particle) {
        this.damage = damage;
        this.particle = particle;
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        var entity = Bukkit.getEntity(PetContextService.getPetTarget(petData.getUniqueId()));

        if(!(entity instanceof LivingEntity target)) {
            return;
        }

        Location strikeLoc = target.getLocation();
        strikeLoc.getWorld().strikeLightningEffect(strikeLoc);
        strikeLoc.getWorld().spawnParticle(particle, strikeLoc, 20, 0.5, 1, 0.5, 0.2);
        target.damage(damage);
        strikeLoc.getWorld().playSound(strikeLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
    }

    public static LightningStrikeAbility fromConfig(ConfigurationSection section) {
        double damage = section.getDouble("damage", 8.0);
        Particle particle = Particle.valueOf(section.getString("particle", "ELECTRIC_SPARK"));
        return new LightningStrikeAbility(damage, particle);
    }

    @Override
    public String getName() {
        return "lightning-strike";
    }
}
