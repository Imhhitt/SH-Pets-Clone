package dev.smartshub.shpets.plugin.pet.model;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.behavior.FollowMode;
import dev.smartshub.shpets.api.pet.behavior.PetBehavior;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Handles pet movement and behavior patterns based on FollowMode
 */
public class PetBehaviorHandler {

    private final PetData data;
    private final PetBehavior behavior;
    private final double followDistance;
    private final double teleportDistance;
    private final FollowMode followMode;
    private final float flexYAmplitude;
    private final float flexYIncrement;
    private final boolean flexY;
    private int count = 0;
    private float yAmplitude = 0f;
    private boolean increasing = true;

    public PetBehaviorHandler(PetData data) {
        this.data = data;
        this.behavior = data.getTemplate().behavior();
        // distance
        this.followDistance = 2.0;
        this.teleportDistance = behavior.followData().teleportDistance();
        this.followMode = behavior.followData().followMode();
        this.flexY = behavior.flexY();
        this.flexYAmplitude = behavior.flexYAmplitude();
        this.flexYIncrement = behavior.flexYIncrement();
    }

    /**
     * Updates pet position and rotation based on owner
     */
    public void tick(PacketPet packetPet, Player owner) {
        count++;
        if (count >= 30) {
            packetPet.applyPlayerEquipment();
            count = 0;
        }

        Location petLoc = packetPet.getLocation();
        Location ownerLoc = owner.getLocation();

        if (petLoc == null) return;

        // Different world - always teleport
        if (!petLoc.getWorld().equals(ownerLoc.getWorld())) {
            packetPet.teleport(calculateTargetLocation(owner));
            return;
        }

        double distance = petLoc.distance(ownerLoc);

        // Always teleport if too far
        if (distance > teleportDistance) {
            packetPet.teleport(calculateTargetLocation(owner));
            return;
        }

        // Handle movement based on FollowMode
        handleMovement(packetPet, owner, distance);

        // Rotate to look at player if configured
        if (behavior.rotateToPlayer()) {
            Location lookTarget = ownerLoc.clone().add(0, 1, 0); // Look at player's head
            packetPet.lookAt(lookTarget);
        }
    }

    private void handleMovement(PacketPet packetPet, Player owner, double distance) {
        switch (followMode) {
            case WALK -> handleWalkMode(packetPet, owner, distance);
            case TELEPORT_ONLY -> handleTeleportOnlyMode(packetPet, owner, distance);
            case FLOATING -> handleFloatingMode(packetPet, owner, distance);
        }
    }

    /**
     * WALK: Pet walks/moves smoothly to follow the owner
     */
    private void handleWalkMode(PacketPet packetPet, Player owner, double distance) {
        if (distance > followDistance) {
            Location targetLoc = calculateTargetLocation(owner);
            packetPet.moveTo(targetLoc, true); // Smooth movement
        }
    }

    /**
     * TELEPORT_ONLY: Pet only teleports when beyond follow distance
     */
    private void handleTeleportOnlyMode(PacketPet packetPet, Player owner, double distance) {
        if (distance > followDistance) {
            Location targetLoc = calculateTargetLocation(owner);
            packetPet.teleport(targetLoc); // Instant teleport
        }
    }

    /**
     * FLOATING: Pet floats smoothly maintaining offset, ignoring ground
     */
    private void handleFloatingMode(PacketPet packetPet, Player owner, double distance) {
        // Always update position to maintain exact offset
        Location targetLoc = flexY ? calculateTargetLocationWithFlex(owner) : calculateTargetLocation(owner);

        // In floating mode, we update every tick to maintain smooth floating
        if (distance > 0.1) { // Small threshold to avoid jitter
            packetPet.moveTo(targetLoc, true);
        }
    }

    /**
     * Calculates the target location for the pet based on owner and configured offsets
     */
    public Location calculateTargetLocation(Player owner) {
        Location ownerLoc = owner.getLocation();

        double offsetX = behavior.followData().xOffset();
        double offsetY = behavior.followData().yOffset();
        double offsetZ = behavior.followData().zOffset();

        // Calculate direction vectors
        Vector direction = ownerLoc.getDirection().normalize();
        Vector right = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();

        // Apply offsets
        Location targetLoc = ownerLoc.clone();
        targetLoc.add(direction.multiply(offsetZ));  // Forward/backward
        targetLoc.add(right.multiply(offsetX));      // Left/right
        targetLoc.add(0, offsetY, 0);                // Up/down

        return targetLoc;
    }

    /**
     * Calculates the target location for the pet based on owner and configured offsets
     */
    public Location calculateTargetLocationWithFlex(Player owner) {
        if(increasing) {
            yAmplitude += flexYIncrement;
            if(yAmplitude >= flexYAmplitude) {
                increasing = false;
            }
        } else {
            yAmplitude -= flexYIncrement;
            if(yAmplitude <= -flexYAmplitude) {
                increasing = true;
            }
        }

        Location ownerLoc = owner.getLocation();

        double offsetX = behavior.followData().xOffset();
        double offsetY = behavior.followData().yOffset();
        double offsetZ = behavior.followData().zOffset();

        // Calculate direction vectors
        Vector direction = ownerLoc.getDirection().normalize();
        Vector right = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();

        // Apply offsets
        Location targetLoc = ownerLoc.clone();
        targetLoc.add(direction.multiply(offsetZ));  // Forward/backward
        targetLoc.add(right.multiply(offsetX));      // Left/right
        targetLoc.add(0, offsetY + yAmplitude, 0);                // Up/down

        return targetLoc;
    }
}