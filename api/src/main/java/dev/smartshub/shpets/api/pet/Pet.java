package dev.smartshub.shpets.api.pet;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface Pet {
    /**
     * Spawns the pet at the owner's location
     */
    void spawn();

    /**
     * Despawns the pet completely
     */
    void despawn();

    /**
     * Called every tick for movement, AI, etc.
     */
    void tick();

    void updateTo(Player player);

    /**
     * Executes periodic actions based on configuration
     */
    void performPeriodic();

    /**
     * Checks if the pet is currently spawned
     */
    boolean isSpawned();

    /**
     * Teleports the pet to the player's location
     */
    void teleport(Player player);

    void setLastExecution(long timestamp);

    /**
     * Gets the pet's data
     */
    PetData getData();

    /**
     * Gets the pet's current location
     */
    Location getLocation();

    /**
     * Gets the pet's unique identifier
     */
    UUID getUniqueId();

    /**
     * Gets the current state
     */
    PetState getState();
}