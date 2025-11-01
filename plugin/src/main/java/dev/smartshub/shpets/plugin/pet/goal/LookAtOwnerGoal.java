package dev.smartshub.shpets.plugin.pet.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.EnumSet;

public class LookAtOwnerGoal extends Goal {

    private final Mob mob;
    private final Player owner;

    public LookAtOwnerGoal(Mob mob, Player owner) {
        this.mob = mob;
        this.owner = owner;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return owner != null && owner.isOnline() && mob.isAlive();
    }

    @Override
    public void tick() {
        if (owner == null || !owner.isOnline()) return;

        Location ownerLoc = owner.getLocation().clone();

        mob.getLookControl().setLookAt(
            ownerLoc.getX(),
            ownerLoc.getY() + owner.getEyeHeight(),
            ownerLoc.getZ(),
            10f, mob.getMaxHeadXRot()
        );
    }
}
