package dev.smartshub.shpets.plugin.pet.goal;

import dev.smartshub.shpets.api.pet.behavior.FollowMode;
import dev.smartshub.shpets.api.pet.behavior.PetBehavior;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportFollowGoal extends Goal {

    private final Mob mob;
    private final Player owner;
    private final PetBehavior behavior;
    private int teleportCooldown = 0;

    public TeleportFollowGoal(Mob mob, Player owner, PetBehavior behavior) {
        this.mob = mob;
        this.owner = owner;
        this.behavior = behavior;
    }

    @Override
    public boolean canUse() {
        return owner.isOnline() && !mob.isDeadOrDying();
    }

    @Override
    public void tick() {
        teleportCooldown++;

        Location mobLoc = mob.getBukkitEntity().getLocation();
        Location ownerLoc = owner.getLocation();

        if (mobLoc.distance(ownerLoc) > 5.0 && teleportCooldown > 40) {
            Location teleportLoc = calculateTargetPosition(ownerLoc);
            mob.getBukkitEntity().teleport(teleportLoc);
            teleportCooldown = 0;
        }
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
}
