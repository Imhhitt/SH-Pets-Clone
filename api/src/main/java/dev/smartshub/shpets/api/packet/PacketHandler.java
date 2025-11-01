package dev.smartshub.shpets.api.packet;

import org.bukkit.entity.Player;

public interface PacketHandler {
    void inject(Player player);
    void stop(Player player);
}