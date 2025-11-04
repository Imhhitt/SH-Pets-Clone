package dev.smartshub.shpets.api.pet.action.ability.impl.basic;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public final class HealAbility extends PetAbility {
    private final HealType type;
    private final double value;

    public enum HealType { ADD, SET, MULTIPLY }

    public HealAbility(HealType type, double value) {
        this.type = type;
        this.value = value;
    }

    public static HealAbility fromConfig(ConfigurationSection section) {
        String typeStr = section.getString("type", "ADD").toUpperCase();
        HealType type = HealType.valueOf(typeStr);
        double value = section.getDouble("value", 1.0);
        
        return new HealAbility(type, value);
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        double currentHealth = player.getHealth();
        double maxHealth = player.getMaxHealth();
        
        double newHealth = switch (type) {
            case ADD -> Math.min(currentHealth + value, maxHealth);
            case SET -> Math.min(value, maxHealth);
            case MULTIPLY -> Math.min(currentHealth * value, maxHealth);
        };
        
        player.setHealth(newHealth);
    }

    @Override
    public String getName() {
        return "heal";
    }
}