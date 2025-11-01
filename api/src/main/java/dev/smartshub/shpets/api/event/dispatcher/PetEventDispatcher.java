package dev.smartshub.shpets.api.event.dispatcher;

import dev.smartshub.shpets.api.event.pet.PetDespawnEvent;
import dev.smartshub.shpets.api.event.pet.PetInteractEvent;
import dev.smartshub.shpets.api.event.pet.PetPeriodicEvent;
import dev.smartshub.shpets.api.event.pet.PetSpawnEvent;
import dev.smartshub.shpets.api.pet.Pet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PetEventDispatcher {

    public PetSpawnEvent firePetSpawnEvent(Pet pet) {
        PetSpawnEvent event = new PetSpawnEvent(pet);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public PetDespawnEvent firePetDespawnEvent(Pet pet) {
        PetDespawnEvent event = new PetDespawnEvent(pet);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public PetInteractEvent firePetInteractEvent(Pet pet, Player interactingPlayer) {
        PetInteractEvent event = new PetInteractEvent(pet, interactingPlayer);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public PetPeriodicEvent firePetPeriodicEvent(Pet pet) {
        PetPeriodicEvent event = new PetPeriodicEvent(pet);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

}
