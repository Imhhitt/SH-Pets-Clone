package dev.smartshub.shpets.api.event.pet;

import dev.smartshub.shpets.api.pet.Pet;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPetEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Pet pet;
    private boolean cancelled = false;

    public AbstractPetEvent(Pet pet) {
        this.pet = pet;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
