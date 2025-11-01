package dev.smartshub.shpets.api.event.pet;

import dev.smartshub.shpets.api.pet.Pet;

public class PetSpawnEvent extends AbstractPetEvent {
    public PetSpawnEvent(Pet pet) {
        super(pet);
    }
}
