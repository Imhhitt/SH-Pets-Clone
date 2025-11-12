package dev.smartshub.shpets.api.pet.action.ability.registry;

import dev.smartshub.shpets.api.pet.action.ability.Ability;
import dev.smartshub.shpets.api.pet.action.ability.impl.attack.*;
import dev.smartshub.shpets.api.pet.action.ability.impl.basic.*;
import dev.smartshub.shpets.api.pet.action.ability.impl.hook.EDToolsBoosAbility;
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
        // Hook
        register("ed-tools-boost", EDToolsBoosAbility::fromConfig);

        // Attacks
        register("crystal-shard", CrystalShardAbility::fromConfig);
        register("explosion-attack", ExplosionAttackAbility::fromConfig);
        register("fireball-attack", FireballAttackAbility::fromConfig);
        register("fire-blast", FireBlastAbility::fromConfig);
        register("frost-attack", FrostAttackAbility::fromConfig);
        register("healing-aura", HealingAuraAbility::fromConfig);
        register("ice-beam-attack", IceBeamAttackAbility::fromConfig);
        register("lightning-strike", LightningStrikeAbility::fromConfig);
        register("poison-spit", PoisonSpitAbility::fromConfig);
        register("root-grab", RootGrabAbility::fromConfig);
        register("slash-attack", SlashAttackAbility::fromConfig);
        register("spin-attack", SpinAttackAbility::fromConfig);
        register("thunder-strike", ThunderStrikeAbility::fromConfig);
        register("wind-blast", WindBlastAbility::fromConfig);
        register("wind-slash", WindSlashAbility::fromConfig);
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