package dev.smartshub.shpets.api.pet.action.ability.impl;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public final class ExplosionAbility extends PetAbility {
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final float power;

    public ExplosionAbility(double offsetX, double offsetY, double offsetZ, float power) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.power = power;
    }

    public static ExplosionAbility fromConfig(ConfigurationSection section) {
        double offsetX = section.getDouble("offsetX", 0.0);
        double offsetY = section.getDouble("offsetY", 0.0);
        double offsetZ = section.getDouble("offsetZ", 0.0);
        float power = (float) section.getDouble("power", 1.0);
        
        return new ExplosionAbility(offsetX, offsetY, offsetZ, power);
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        Location loc = player.getLocation().add(offsetX, offsetY, offsetZ);
        player.getWorld().createExplosion(loc, power, false, false);
    }

    @Override
    public String getName() {
        return "explosion";
    }
}