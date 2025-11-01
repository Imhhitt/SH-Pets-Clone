package dev.smartshub.shpets.api.pet.action.ability;

import dev.smartshub.shpets.api.math.Probability;
import dev.smartshub.shpets.api.pet.PetData;
import org.bukkit.entity.Player;

import java.util.Random;

public abstract class PetAbility implements Ability {

    public static final Random RANDOM = new Random();

    private double probability = 100D;
    private long delay = 0; // In ticks
    private long lastExecution = 0;

    @Override
    public void execute(final Player player, final PetData petData) {
        // Verify cooldown
        long currentTime = System.currentTimeMillis();
        if (delay > 0 && (currentTime - lastExecution) < (delay * 50)) { // 50ms per tick
            return;
        }

        // Verify probability
        if (Probability.checkProbability(probability, RANDOM)) {
            executeAbility(player, petData);
            lastExecution = currentTime;
        }
    }

    @Override
    public void setProbability(final double probability) {
        this.probability = probability;
    }

    @Override
    public void setDelay(final long delay) {
        this.delay = delay;
    }

    @Override
    public long getDelay() {
        return delay;
    }

    protected abstract void executeAbility(final Player player, final PetData petData);

    @Override
    public String toString() {
        return "probability: " + probability + "%, delay: " + delay + " ticks";
    }
}