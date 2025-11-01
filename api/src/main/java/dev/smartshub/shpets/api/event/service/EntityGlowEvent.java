package dev.smartshub.shpets.api.event.service;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EntityGlowEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final UUID target;
    private final int duration;
    private final NamedTextColor color;

    public EntityGlowEvent(UUID target, int duration, NamedTextColor color) {
        this.target = target;
        this.duration = duration;
        this.color = color;
    }

    public UUID getTarget() {
        return target;
    }

    public int getDuration() {
        return duration;
    }

    public NamedTextColor getColor() {
        return color;
    }

    public boolean isTemporary() {
        return duration > 0;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}