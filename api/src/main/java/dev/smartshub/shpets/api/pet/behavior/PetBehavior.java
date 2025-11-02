package dev.smartshub.shpets.api.pet.behavior;

public record PetBehavior(
        FollowData followData,
        double movementSpeed,
        boolean flexY,
        float flexYAmplitude,
        float flexYIncrement,
        boolean rotateToPlayer,
        boolean showNameTag
) {}