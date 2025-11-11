package dev.smartshub.shpets.api.pet.action.ability.impl.attack;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import dev.smartshub.shpets.api.service.context.PetContextService;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RootGrabAbility extends PetAbility {

    private final double damage;
    private final int slowTicks;
    private final Particle particle;
    private final Sound sound;

    public RootGrabAbility(double damage, int slowTicks, Particle particle, Sound sound) {
        this.damage = damage;
        this.slowTicks = slowTicks;
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
        loc.getWorld().spawnParticle(particle, loc, 15, 0.3, 0.3, 0.3, 0);
        loc.getWorld().playSound(loc, sound, 1f, 1f);

        target.damage(damage);
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, slowTicks, 3));
    }

    public static RootGrabAbility fromConfig(ConfigurationSection section) {
        double damage = section.getDouble("damage", 4.0);
        int slowTicks = section.getInt("slow-ticks", 80);
        Particle particle = Particle.valueOf(section.getString("particle", "BLOCK_CRACK"));
        Sound sound = Sound.valueOf(section.getString("sound", "BLOCK_GRASS_BREAK"));
        return new RootGrabAbility(damage, slowTicks, particle, sound);
    }

    @Override
    public String getName() {
        return "root-grab";
    }
}
