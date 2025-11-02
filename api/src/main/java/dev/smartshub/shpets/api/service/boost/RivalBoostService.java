package dev.smartshub.shpets.api.service.boost;

import dev.smartshub.shpets.api.pet.action.ability.impl.RivalBoostAbility.RivalBoostType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RivalBoostService {

    private static RivalBoostService instance;
    
    private final Map<UUID, Map<RivalBoostType, BoostData>> activeBoosts;
    private Plugin plugin;

    private RivalBoostService() {
        this.activeBoosts = new ConcurrentHashMap<>();
    }

    public static RivalBoostService getInstance() {
        if (instance == null) {
            instance = new RivalBoostService();
        }
        return instance;
    }

    /**
     * Initializes the service with the plugin instance
     * Call this in your onEnable() method
     */
    public void initialize(Plugin plugin) {
        this.plugin = plugin;
        startCleanupTask();
    }

    /**
     * Adds or updates a boost for a player
     */
    public void addBoost(UUID playerUUID, RivalBoostType type, double multiplier, long expirationTime) {
        activeBoosts.computeIfAbsent(playerUUID, k -> new HashMap<>())
                    .put(type, new BoostData(multiplier, expirationTime));
    }

    /**
     * Removes a boost for a player
     */
    public void removeBoost(UUID playerUUID, RivalBoostType type) {
        Map<RivalBoostType, BoostData> playerBoosts = activeBoosts.get(playerUUID);
        if (playerBoosts != null) {
            playerBoosts.remove(type);
            if (playerBoosts.isEmpty()) {
                activeBoosts.remove(playerUUID);
            }
        }
    }

    /**
     * Removes all boosts for a player
     */
    public void removeAllBoosts(UUID playerUUID) {
        activeBoosts.remove(playerUUID);
    }

    /**
     * Gets the active multiplier for a player and type
     */
    public double getMultiplier(UUID playerUUID, RivalBoostType type) {
        Map<RivalBoostType, BoostData> playerBoosts = activeBoosts.get(playerUUID);
        if (playerBoosts == null) {
            return 1.0;
        }

        BoostData boost = playerBoosts.get(type);
        if (boost == null) {
            return 1.0;
        }

        // Check if boost has expired
        if (boost.expirationTime != -1 && System.currentTimeMillis() > boost.expirationTime) {
            removeBoost(playerUUID, type);
            return 1.0;
        }

        return boost.multiplier;
    }

    /**
     * Applies the boost to a money amount
     */
    public double applyBoost(UUID playerUUID, RivalBoostType type, double originalAmount) {
        double multiplier = getMultiplier(playerUUID, type);
        return originalAmount * multiplier;
    }

    /**
     * Checks if a player has an active boost
     */
    public boolean hasActiveBoost(UUID playerUUID, RivalBoostType type) {
        return getMultiplier(playerUUID, type) > 1.0;
    }

    /**
     * Gets remaining time in seconds for a boost (-1 if infinite, 0 if expired/not exists)
     */
    public long getRemainingTime(UUID playerUUID, RivalBoostType type) {
        Map<RivalBoostType, BoostData> playerBoosts = activeBoosts.get(playerUUID);
        if (playerBoosts == null) {
            return 0;
        }

        BoostData boost = playerBoosts.get(type);
        if (boost == null) {
            return 0;
        }

        if (boost.expirationTime == -1) {
            return -1; // Infinite
        }

        long remaining = (boost.expirationTime - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }

    /**
     * Gets all active boosts for a player
     */
    public Map<RivalBoostType, BoostData> getActiveBoosts(UUID playerUUID) {
        return activeBoosts.getOrDefault(playerUUID, new HashMap<>());
    }

    /**
     * Starts a task to clean up expired boosts every minute
     */
    private void startCleanupTask() {
        if (plugin == null) {
            Bukkit.getLogger().warning("RivalBoostService: Plugin not initialized! Call initialize() first.");
            return;
        }
        
        Bukkit.getScheduler().runTaskTimerAsynchronously(
            plugin, 
            this::cleanupExpiredBoosts, 
            20L * 60, // Start after 1 minute
            20L * 60  // Repeat every minute
        );
    }

    /**
     * Removes all expired boosts
     */
    private void cleanupExpiredBoosts() {
        long currentTime = System.currentTimeMillis();
        
        activeBoosts.entrySet().removeIf(entry -> {
            Map<RivalBoostType, BoostData> playerBoosts = entry.getValue();
            
            playerBoosts.entrySet().removeIf(boostEntry -> {
                BoostData boost = boostEntry.getValue();
                return boost.expirationTime != -1 && currentTime > boost.expirationTime;
            });
            
            return playerBoosts.isEmpty();
        });
    }

    /**
     * Shuts down the service (call on plugin disable)
     */
    public void shutdown() {
        activeBoosts.clear();
    }

    /**
     * Internal class to store boost data
     */
    public static class BoostData {
        private final double multiplier;
        private final long expirationTime; // -1 for infinite

        public BoostData(double multiplier, long expirationTime) {
            this.multiplier = multiplier;
            this.expirationTime = expirationTime;
        }

        public double getMultiplier() {
            return multiplier;
        }

        public long getExpirationTime() {
            return expirationTime;
        }

        public boolean isExpired() {
            return expirationTime != -1 && System.currentTimeMillis() > expirationTime;
        }

        public boolean isInfinite() {
            return expirationTime == -1;
        }
    }
}