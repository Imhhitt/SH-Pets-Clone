package dev.smartshub.shpets.plugin.pet.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class PetIdleGoal extends Goal {

    private final Mob mob;
    private int idleTime = 0;

    public PetIdleGoal(Mob mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        return Math.random() < 0.001;
    }

    @Override
    public boolean canContinueToUse() {
        return idleTime < 60;
    }

    @Override
    public void start() {
        idleTime = 0;
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        idleTime++;
    }
}