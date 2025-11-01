package dev.smartshub.shpets.api.event.pet;

import dev.smartshub.shpets.api.pet.Pet;

public class PetDespawnEvent extends AbstractPetEvent {
    public PetDespawnEvent(Pet pet) {
        super(pet);
    }
}
