package dev.smartshub.shpets.api.pet.action.ability.impl;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class EffectAbility extends PetAbility {
    private final PotionEffectType effectType;
    private final int duration; // In ticks
    private final int amplifier;
    private final boolean ambient;
    private final boolean particles;
    private final boolean icon;

    public EffectAbility(PotionEffectType effectType, int duration, int amplifier, 
                        boolean ambient, boolean particles, boolean icon) {
        this.effectType = effectType;
        this.duration = duration;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.particles = particles;
        this.icon = icon;
    }

    public static EffectAbility fromConfig(ConfigurationSection section) {
        String effectName = section.getString("type", "SPEED");
        PotionEffectType effectType;
        
        try {
            effectType = PotionEffectType.getByName(effectName.toUpperCase());
            if (effectType == null) {
                effectType = PotionEffectType.SPEED;
            }
        } catch (Exception e) {
            effectType = PotionEffectType.SPEED;
        }
        
        int duration = section.getInt("duration", 200); // 10 segundos por defecto
        int amplifier = section.getInt("amplifier", 0);
        boolean ambient = section.getBoolean("ambient", false);
        boolean particles = section.getBoolean("particles", true);
        boolean icon = section.getBoolean("icon", true);
        
        return new EffectAbility(effectType, duration, amplifier, ambient, particles, icon);
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        PotionEffect effect = new PotionEffect(
            effectType,
            duration,
            amplifier,
            ambient,
            particles,
            icon
        );
        
        player.addPotionEffect(effect);
    }

    @Override
    public String getName() {
        return "effect";
    }
}