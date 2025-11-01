package dev.smartshub.shpets.api.service.skull;

import org.bukkit.inventory.ItemStack;

import java.util.concurrent.CompletableFuture;

public interface SkullService {
    CompletableFuture<ItemStack> getSkull(String textureOrPlayerName);
    ItemStack getCustomSkull(String textureOrPlayerName);
    boolean isBase64(String str);
}