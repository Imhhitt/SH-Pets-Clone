package dev.smartshub.shpets.api.pet.action.ability.registry;

import dev.smartshub.shpets.api.pet.action.ability.Ability;
import dev.smartshub.shpets.api.pet.action.ability.impl.ParticleAbility;
import dev.smartshub.shpets.api.pet.action.ability.impl.*;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class AbilityRegistry {
    private static final Map<String, AbilityFactory> FACTORIES = new HashMap<>();
    
    static {
        register("console-commands", CommandAbility::fromConfig);
        register("player-commands", CommandAbility::fromConfig);
        register("messages", MessageAbility::fromConfig);
        register("title", TitleAbility::fromConfig);
        register("sound", SoundAbility::fromConfig);
        register("explosion", ExplosionAbility::fromConfig);
        register("heal", HealAbility::fromConfig);
        register("effect", EffectAbility::fromConfig);
        register("particle", ParticleAbility::fromConfig);
        register("glow", GlowAbility::fromConfig);
    }
    
    public static void register(String key, AbilityFactory factory) {
        FACTORIES.put(key, factory);
    }
    
    public static boolean isKnownAbility(String key) {
        return FACTORIES.containsKey(key);
    }
    
    public static Ability create(String key, ConfigurationSection section) {
        AbilityFactory factory = FACTORIES.get(key);
        if (factory == null) return null;
        
        Ability ability = factory.create(section);
        
        if (section.contains("probability")) {
            ability.setProbability(section.getDouble("probability", 100.0));
        }
        if (section.contains("delay")) {
            ability.setDelay(section.getLong("delay", 0));
        }
        
        return ability;
    }
    
    @FunctionalInterface
    public interface AbilityFactory {
        Ability create(ConfigurationSection section);
    }
}