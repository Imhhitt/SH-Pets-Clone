package dev.smartshub.shpets.api.pet.behavior;

public record PetBehavior(
        FollowData followData,
        double movementSpeed,
        boolean rotateToPlayer,
        boolean showNameTag
) {}