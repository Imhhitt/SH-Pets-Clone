package dev.smartshub.shpets.api.pet.action.ability.loader;

import dev.smartshub.shpets.api.pet.action.ability.Ability;
import dev.smartshub.shpets.api.pet.action.ability.registry.AbilityRegistry;
import dev.smartshub.shpets.api.pet.action.ability.conditional.CompiledCondition;
import dev.smartshub.shpets.api.pet.action.ability.conditional.ConditionalAbility;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class AbilityLoader {

    public static List<Ability> loadAbilities(ConfigurationSection section) {
        List<Ability> abilities = new ArrayList<>();

        if (section == null) return abilities;


        for (String key : section.getKeys(false)) {
            if (!section.isConfigurationSection(key)) {
                continue;
            }

            ConfigurationSection abilitySection = section.getConfigurationSection(key);

            if (AbilityRegistry.isKnownAbility(key)) {
                Ability ability = AbilityRegistry.create(key, abilitySection);
                if (ability != null) {
                    abilities.add(ability);
                }
            } else if (abilitySection.contains("condition")) {
                ConditionalAbility conditionalAbility = loadConditionalAbility(key, abilitySection);
                if (conditionalAbility != null) {
                    abilities.add(conditionalAbility);
                }
            }
        }

        return abilities;
    }
    
    private static ConditionalAbility loadConditionalAbility(String name, ConfigurationSection section) {
        String conditionStr = section.getString("condition");
        if (conditionStr == null || conditionStr.isEmpty()) {
            return null;
        }
        
        CompiledCondition condition = CompiledCondition.compile(conditionStr);
        
        List<Ability> subAbilities = new ArrayList<>();
        
        for (String key : section.getKeys(false)) {
            if (key.equals("condition") || key.equals("probability") || key.equals("delay")) {
                continue;
            }
            
            if (AbilityRegistry.isKnownAbility(key)) {
                ConfigurationSection subSection = section.getConfigurationSection(key);
                if (subSection != null) {
                    Ability ability = AbilityRegistry.create(key, subSection);
                    if (ability != null) {
                        subAbilities.add(ability);
                    }
                }
            } else if (section.isConfigurationSection(key)) {
                ConfigurationSection subSection = section.getConfigurationSection(key);
                if (subSection.contains("condition")) {
                    ConditionalAbility nested = loadConditionalAbility(key, subSection);
                    if (nested != null) {
                        subAbilities.add(nested);
                    }
                }
            }
        }
        
        ConditionalAbility conditionalAbility = new ConditionalAbility(name, condition, subAbilities);
        
        if (section.contains("probability")) {
            conditionalAbility.setProbability(section.getDouble("probability"));
        }
        if (section.contains("delay")) {
            conditionalAbility.setDelay(section.getLong("delay"));
        }
        
        return conditionalAbility;
    }
}