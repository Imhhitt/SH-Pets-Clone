package dev.smartshub.shpets.plugin.listener;

import dev.smartshub.shpets.plugin.packet.PacketHandlerImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final PacketHandlerImpl packetHandler;

    public PlayerQuitListener(PacketHandlerImpl packetHandler) {
        this.packetHandler = packetHandler;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        packetHandler.stop(event.getPlayer());
    }

}
