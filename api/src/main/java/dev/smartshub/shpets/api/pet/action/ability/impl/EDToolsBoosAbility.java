package dev.smartshub.shpets.api.pet.action.ability.impl;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import es.edwardbelt.edgens.iapi.EdToolsAPI;
import es.edwardbelt.edgens.iapi.EdToolsBoostersAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EDToolsBoosAbility extends PetAbility {

    private final EdToolsBoostersAPI boostersAPI = EdToolsAPI.getInstance().getBoostersAPI();

    private final String boosterName;
    private final String economy;
    private final double multiplier;
    private final long duration;
    private final boolean enchantBooster;
    private final boolean saveDB;

    public EDToolsBoosAbility(String boosterName, String economy, double multiplier, long duration,
                              boolean enchantBooster, boolean saveDB) {
        this.boosterName = boosterName;
        this.economy = economy;
        this.multiplier = multiplier;
        this.duration = duration;
        this.enchantBooster = enchantBooster;
        this.saveDB = saveDB;
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        if(boostersAPI == null) {
            Bukkit.getLogger().severe("EdTools is not installed or is unavailable. Cannot use the boost ability!");
            return;
        }

        UUID playerUUID = player.getUniqueId();
        String boosterId = "pet_boost_" + playerUUID + "_" + System.currentTimeMillis();

        boostersAPI.addBooster(
                playerUUID,
                boosterId,
                boosterName,
                economy,
                multiplier,
                duration,
                enchantBooster,
                saveDB
        );
    }

    public static EDToolsBoosAbility fromConfig(ConfigurationSection section) {
        EdToolsBoostersAPI edTools = EdToolsAPI.getInstance().getBoostersAPI();

        if(edTools == null) {
            Bukkit.getLogger().severe("EdTools is not installed or is unavailable. Cannot use the boost ability!");
            return null;
        }

        String boosterName = section.getString("booster-name", "Pet Booster");
        String economy = section.getString("economy", "");
        double multiplier = section.getDouble("multiplier", 1.0);
        long duration = section.getLong("duration", 3600);
        boolean enchantBooster = section.getBoolean("enchant-booster", false);
        boolean saveDB = section.getBoolean("save-db", false);

        return new EDToolsBoosAbility(boosterName, economy, multiplier, duration, enchantBooster, saveDB);
    }

    @Override
    public String getName() {
        return "ed-tools-boost";
    }
}