package dev.smartshub.shpets.plugin.pet.goal;

import dev.smartshub.shpets.api.pet.behavior.FollowMode;
import dev.smartshub.shpets.api.pet.behavior.PetBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.EnumSet;

public class NormalFollowGoal extends Goal {

    private final Mob mob;
    private final Player owner;
    private final PetBehavior behavior;
    private Location lastOwnerLocation;
    private Location targetLocation;
    private static final double MOVEMENT_THRESHOLD = 0.2;

    public NormalFollowGoal(Mob mob, Player owner, PetBehavior behavior) {
        this.mob = mob;
        this.owner = owner;
        this.behavior = behavior;
        this.lastOwnerLocation = owner.getLocation().clone();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return owner.isOnline() && !mob.isDeadOrDying();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        updateTargetLocation();
    }

    @Override
    public void tick() {
        Location currentOwnerLoc = owner.getLocation();

        if (hasOwnerMoved(currentOwnerLoc)) {
            updateTargetLocation();
            walkToTarget();
            lastOwnerLocation = currentOwnerLoc.clone();
        } else {
            maintainWalkingAnimation();
        }

        checkTeleportDistance();
    }

    private boolean hasOwnerMoved(Location currentLoc) {
        return currentLoc.distance(lastOwnerLocation) > MOVEMENT_THRESHOLD;
    }

    private void updateTargetLocation() {
        Location ownerLoc = owner.getLocation();
        targetLocation = calculateTargetPosition(ownerLoc);
    }

    private Location calculateTargetPosition(Location ownerLoc) {
        float yaw = ownerLoc.getYaw();
        // distance
        double distance = 2.0;
        double radians = Math.toRadians(yaw + 135);

        double offsetX = Math.cos(radians) * distance;
        double offsetZ = Math.sin(radians) * distance;
        double offsetY = behavior.followData().followMode() == FollowMode.FLOATING ? 1.0 : 0.0;

        return ownerLoc.clone().add(offsetX, offsetY, offsetZ);
    }

    private void walkToTarget() {
        if (targetLocation == null) return;

        BlockPos targetPos = new BlockPos(
                (int) targetLocation.getX(),
                (int) targetLocation.getY(),
                (int) targetLocation.getZ()
        );

        PathNavigation navigation = mob.getNavigation();
        Path path = navigation.createPath(targetPos, 1);

        if (path != null) {
            navigation.moveTo(path, 1.2);
        }
    }

    private void maintainWalkingAnimation() {
        if (targetLocation != null) {
            Location mobLoc = mob.getBukkitEntity().getLocation();
            double distance = mobLoc.distance(targetLocation);

            if (distance > 0.5) {
                walkToTarget();
            } else {
                long time = System.currentTimeMillis();
                double microX = Math.sin(time * 0.001) * 0.1;
                double microZ = Math.cos(time * 0.001) * 0.1;

                BlockPos microTarget = new BlockPos(
                        (int) (targetLocation.getX() + microX),
                        (int) targetLocation.getY(),
                        (int) (targetLocation.getZ() + microZ)
                );

                PathNavigation navigation = mob.getNavigation();
                Path path = navigation.createPath(microTarget, 0);
                if (path != null) {
                    navigation.moveTo(path, 0.5);
                }
            }
        }
    }

    private void checkTeleportDistance() {
        Location mobLoc = mob.getBukkitEntity().getLocation();
        Location ownerLoc = owner.getLocation();

        if (mobLoc.distance(ownerLoc) > behavior.followData().teleportDistance()) {
            Location teleportLoc = calculateTargetPosition(ownerLoc);
            mob.getBukkitEntity().teleport(teleportLoc);
            mob.getNavigation().stop();
        }
    }
}