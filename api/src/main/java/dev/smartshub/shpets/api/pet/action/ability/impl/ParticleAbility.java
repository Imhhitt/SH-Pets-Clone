package dev.smartshub.shpets.api.pet.action.ability.impl;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import dev.smartshub.shpets.api.registry.PetInstanceRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public final class ParticleAbility extends PetAbility {

    private final Particle particleType;
    private final int count;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final double speed;
    private final ParticleTarget target;

    public enum ParticleTarget {
        PLAYER,
        PET,
        LOCATION
    }

    public ParticleAbility(
            Particle particleType,
            int count,
            double offsetX,
            double offsetY,
            double offsetZ,
            double speed,
            ParticleTarget target
    ) {
        this.particleType = particleType;
        this.count = count;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.target = target;
    }

    /**
     * Factory method to create ParticleAbility from config section
     * Compatible with AbilityRegistry system
     */
    public static ParticleAbility fromConfig(ConfigurationSection section) {
        // Particle type
        String typeName = section.getString("type", "FLAME");
        Particle particle;

        try {
            particle = Particle.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Invalid particle type: " + typeName + ", using FLAME");
            particle = Particle.FLAME;
        }

        // Count and offsets
        int count = section.getInt("count", 10);
        double offsetX = section.getDouble("offsetX", 0.5);
        double offsetY = section.getDouble("offsetY", 0.5);
        double offsetZ = section.getDouble("offsetZ", 0.5);
        double speed = section.getDouble("speed", 0.1);

        // Target
        String targetStr = section.getString("target", "PLAYER").toUpperCase();
        ParticleTarget target;

        try {
            target = ParticleTarget.valueOf(targetStr);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Invalid particle target: " + targetStr + ", using PLAYER");
            target = ParticleTarget.PLAYER;
        }

        return new ParticleAbility(
                particle,
                count,
                offsetX,
                offsetY,
                offsetZ,
                speed,
                target
        );
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        Location spawnLocation = getTargetLocation(player, petData);

        if (spawnLocation != null && spawnLocation.getWorld() != null) {
            // Spawn particles at the target location
            spawnLocation.getWorld().spawnParticle(
                    particleType,
                    spawnLocation,
                    count,
                    offsetX,
                    offsetY,
                    offsetZ,
                    speed
            );
        }
    }

    /**
     * Gets the location where particles should spawn based on target
     */
    private Location getTargetLocation(Player player, PetData petData) {
        return switch (target) {
            case PLAYER, LOCATION -> player.getLocation().clone().add(0, 1, 0); // Slightly above player
            case PET -> getPetLocation(player, petData);
        };
    }

    /**
     * Gets the pet's actual location from the registry
     */
    private Location getPetLocation(Player player, PetData petData) {
        //TODO ???

        // Fallback: spawn at player location if pet not found
        return player.getLocation().clone().add(0, 1, 0);
    }

    @Override
    public String getName() {
        return "particle";
    }

    @Override
    public String toString() {
        return super.toString() +
                ", type: " + particleType.name() +
                ", target: " + target.name() +
                ", count: " + count;
    }

    // Getters for debugging/inspection
    public Particle getParticleType() {
        return particleType;
    }

    public ParticleTarget getTarget() {
        return target;
    }

    public int getCount() {
        return count;
    }
}