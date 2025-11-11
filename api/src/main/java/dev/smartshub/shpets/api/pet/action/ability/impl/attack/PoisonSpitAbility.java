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

public class PoisonSpitAbility extends PetAbility {

    private final double damage;
    private final Particle particle;
    private final Sound sound;
    private final int poisonTicks;

    public PoisonSpitAbility(double damage, Particle particle, Sound sound, int poisonTicks) {
        this.damage = damage;
        this.particle = particle;
        this.sound = sound;
        this.poisonTicks = poisonTicks;
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        var entity = Bukkit.getEntity(PetContextService.getPetTarget(petData.getUniqueId()));

        if(!(entity instanceof LivingEntity target)) {
            return;
        }

        target.getWorld().spawnParticle(particle, target.getLocation(), 10, 0.3, 0.3, 0.3, 0);
        target.getWorld().playSound(target.getLocation(), sound, 1f, 1.2f);
        target.damage(damage);
        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, poisonTicks, 0));
    }

    public static PoisonSpitAbility fromConfig(ConfigurationSection section) {
        double damage = section.getDouble("damage", 3.0);
        int poisonTicks = section.getInt("poison-ticks", 60);
        Particle particle = Particle.valueOf(section.getString("particle", "SLIME"));
        Sound sound = Sound.valueOf(section.getString("sound", "ENTITY_SLIME_JUMP"));
        return new PoisonSpitAbility(damage, particle, sound, poisonTicks);
    }

    @Override
    public String getName() {
        return "poison-spit";
    }
}
