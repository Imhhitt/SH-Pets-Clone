package dev.smartshub.shpets.plugin.service.skull;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import dev.rollczi.liteskullapi.LiteSkullFactory;
import dev.rollczi.liteskullapi.SkullAPI;
import dev.smartshub.shpets.api.service.skull.SkullService;
import dev.smartshub.shpets.plugin.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SkullHandlingService implements SkullService {

    private final Main plugin;
    private final SkullAPI skullAPI;

    public SkullHandlingService(Main plugin) {
        this.plugin = plugin;
        this.skullAPI = LiteSkullFactory.builder()
                .bukkitScheduler(plugin)
                .build();
    }

    @Override
    public CompletableFuture<ItemStack> getSkull(String textureOrPlayerName) {
        CompletableFuture<ItemStack> future = new CompletableFuture<>();

        if (isBase64(textureOrPlayerName)) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                ItemStack skull = getCustomSkull(textureOrPlayerName);
                future.complete(skull);
            });
        } else {
            skullAPI.acceptAsyncSkullData(textureOrPlayerName, skullData -> {
                String texture = skullData.getValue();
                String signature = skullData.getSignature();

                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta meta = (SkullMeta) head.getItemMeta();

                    PlayerProfile profile = new CraftPlayerProfile(UUID.randomUUID(), textureOrPlayerName);
                    profile.getProperties().add(new ProfileProperty("textures", texture, signature));

                    meta.setPlayerProfile(profile);
                    head.setItemMeta(meta);
                    future.complete(head);
                });
            });
        }

        return future;
    }

    @Override
    public boolean isBase64(String str) {
        try {
            Base64.getDecoder().decode(str);
            return str.length() > 100;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public ItemStack getCustomSkull(String base64) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (base64 == null || base64.isEmpty()) {
            return head;
        }

        SkullMeta meta = (SkullMeta) head.getItemMeta();

        PlayerProfile profile = new CraftPlayerProfile(UUID.randomUUID(), null);
        profile.getProperties().add(new ProfileProperty("textures", base64));

        meta.setPlayerProfile(profile);
        head.setItemMeta(meta);

        return head;
    }
}
