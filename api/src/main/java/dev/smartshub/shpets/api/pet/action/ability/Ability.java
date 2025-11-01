package dev.smartshub.shpets.api.pet.action.ability;

import dev.smartshub.shpets.api.pet.PetData;
import org.bukkit.entity.Player;

public interface Ability {
    void execute(final Player player, final PetData petData);
    void setProbability(final double probability);
    void setDelay(final long delay);
    String getName();

    default long getDelay() {
        return 0;
    }
}
