package dev.smartshub.shpets.plugin.listener;

import dev.smartshub.shpets.api.PetsAPI;
import dev.smartshub.shpets.plugin.packet.PacketHandlerImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final PacketHandlerImpl packetHandler;

    public PlayerJoinListener(PacketHandlerImpl packetHandler) {
        this.packetHandler = packetHandler;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        packetHandler.inject(event.getPlayer());

        PetsAPI.getInstance().petInstanceRegistry().getAll().forEach(pet -> {
            if (!pet.isSpawned()) return;

            var owner = pet.getData().getOwner();
            if (owner == null || !owner.isOnline()) return;

            var petWorld = owner.getWorld().getName();
            var playerWorld = event.getPlayer().getWorld().getName();

            if (petWorld.equals(playerWorld)) {
                pet.updateTo(event.getPlayer());
            }
        });
    }

}