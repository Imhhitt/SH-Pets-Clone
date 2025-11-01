package dev.smartshub.shpets.api.pet.behavior;

public record FollowData(
    FollowMode followMode,
    double teleportDistance,
    double xOffset,
    double yOffset,
    double zOffset
) {
}
