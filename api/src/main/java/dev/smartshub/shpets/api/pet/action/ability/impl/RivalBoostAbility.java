package dev.smartshub.shpets.api.pet.action.ability.impl;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import dev.smartshub.shpets.api.service.boost.RivalBoostService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class RivalBoostAbility extends PetAbility {

    private final long duration;
    private final double multiplier;
    private final RivalBoostType type;

    public RivalBoostAbility(long duration, double multiplier, RivalBoostType type) {
        this.duration = duration;
        this.multiplier = multiplier;
        this.type = type;
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        RivalBoostService service = RivalBoostService.getInstance();

        long expirationTime;
        if (duration == -1) {
            expirationTime = -1; // Infinite
        } else {
            expirationTime = System.currentTimeMillis() + (duration * 1000);
        }

        service.addBoost(player.getUniqueId(), type, multiplier, expirationTime);

    }

    public static RivalBoostAbility fromConfig(ConfigurationSection section) {
        long duration = section.getLong("duration", 3600);
        double multiplier = section.getDouble("multiplier", 1.2);
        String typeStr = section.getString("type", "sword").toLowerCase();

        RivalBoostType type;
        try {
            type = RivalBoostType.valueOf(typeStr.toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Invalid rival boost type: " + typeStr + ". Using SWORD as default.");
            type = RivalBoostType.SWORD;
        }

        return new RivalBoostAbility(duration, multiplier, type);
    }

    @Override
    public String getName() {
        return "RivalBoost";
    }

    public enum RivalBoostType {
        SWORD("RivalSwords"),
        FISHING_ROD("RivalFishingRods"),
        PICKAXE("RivalPickaxe"),
        HOE("RivalHoes");

        private final String displayName;

        RivalBoostType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}