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
    public void onPlayerJoun(PlayerJoinEvent event) {
        packetHandler.inject(event.getPlayer());

        PetsAPI.getInstance().petInstanceRegistry().getAll().forEach(pet -> {
            var petWorld = pet.getData().getOwner().getWorld().getName();
            var world = event.getPlayer().getWorld().getName();
            if (!petWorld.equals(world)) return;
            pet.updateTo(event.getPlayer());
        });
    }

}
