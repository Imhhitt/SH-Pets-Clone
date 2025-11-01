package dev.smartshub.shpets.plugin.pet.goal;

import dev.smartshub.shpets.api.pet.behavior.PetBehavior;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class FloatingFollowGoal extends Goal {

    private final Mob mob;
    private final Player owner;
    private final PetBehavior behavior;
    private float floatCounter = 0f;

    public FloatingFollowGoal(Mob mob, Player owner, PetBehavior behavior) {
        this.mob = mob;
        this.owner = owner;
        this.behavior = behavior;
        this.setFlags(EnumSet.of(Flag.MOVE));

        mob.setNoGravity(true);
    }

    @Override
    public boolean canUse() {
        return owner.isOnline() && !mob.isDeadOrDying();
    }

    @Override
    public void tick() {
        floatCounter += 0.05f;
        final Location ownerLoc = owner.getLocation();
        final Location targetLoc = calculateFloatingPosition(ownerLoc);
        final Location currentLoc = mob.getBukkitEntity().getLocation();

        final double distance = currentLoc.distance(targetLoc);

        final Vector velocity = getVelocityVector(distance, currentLoc, targetLoc);
        mob.setDeltaMovement(velocity.getX(), velocity.getY(), velocity.getZ());
    }

    private static @NotNull Vector getVelocityVector(double distance, Location currentLoc, Location targetLoc) {
        final double baseSpeed = 0.15;
        final double speed = baseSpeed + Math.min(distance * 0.1, 0.5);

        final double newX = currentLoc.getX() + (targetLoc.getX() - currentLoc.getX()) * speed;
        final double newY = currentLoc.getY() + (targetLoc.getY() - currentLoc.getY()) * speed;
        final double newZ = currentLoc.getZ() + (targetLoc.getZ() - currentLoc.getZ()) * speed;

        return new Vector(newX - currentLoc.getX(), newY - currentLoc.getY(), newZ - currentLoc.getZ());
    }

    private Location calculateFloatingPosition(Location ownerLoc) {
        float yaw = ownerLoc.getYaw();
        // distance
        double distance = 2.0;
        double radians = Math.toRadians(yaw + 135);

        double offsetX = Math.cos(radians) * distance;
        double offsetZ = Math.sin(radians) * distance;
        double offsetY = 1.5 + Math.sin(floatCounter) * 0.25;

        return ownerLoc.clone().add(offsetX, offsetY, offsetZ);
    }
}
