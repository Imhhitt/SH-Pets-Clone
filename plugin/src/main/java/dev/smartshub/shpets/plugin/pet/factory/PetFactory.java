package dev.smartshub.shpets.plugin.pet.factory;

import dev.smartshub.shpets.api.pet.Pet;
import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.plugin.message.MessageParser;
import dev.smartshub.shpets.plugin.pet.model.BehavioralPet;

/**
 * Factory for creating pet instances
 */
public class PetFactory {

    /**
     * Creates a new behavioral pet from pet data
     */
    public Pet createPet(PetData petData, MessageParser parser) {
        return new BehavioralPet(petData, parser);
    }
}