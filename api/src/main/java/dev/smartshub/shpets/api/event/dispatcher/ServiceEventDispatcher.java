package dev.smartshub.shpets.api.event.dispatcher;

import dev.smartshub.shpets.api.event.service.EntityGlowEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

import java.util.UUID;

public class ServiceEventDispatcher {

    public void fireEntityGlowEvent(UUID target, int duration, NamedTextColor color) {
        EntityGlowEvent event = new EntityGlowEvent(
                target,
                duration,
                color
        );
        Bukkit.getPluginManager().callEvent(event);
    }

}
