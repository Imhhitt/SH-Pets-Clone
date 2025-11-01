package dev.smartshub.shpets.api.event.pet;

import dev.smartshub.shpets.api.pet.Pet;

public class PetPeriodicEvent extends AbstractPetEvent {
    public PetPeriodicEvent(Pet pet) {
        super(pet);
    }
}
