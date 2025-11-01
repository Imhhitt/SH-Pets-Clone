package dev.smartshub.shpets.api.event.pet;

import dev.smartshub.shpets.api.pet.Pet;
import org.bukkit.entity.Player;

public class PetInteractEvent extends AbstractPetEvent {
    private final Player interactingPlayer;

    public PetInteractEvent(Pet pet, Player interactingPlayer) {
        super(pet);
        this.interactingPlayer = interactingPlayer;
    }

}
