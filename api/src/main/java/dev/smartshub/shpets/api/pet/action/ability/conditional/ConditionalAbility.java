package dev.smartshub.shpets.api.pet.action.ability.conditional;

import dev.smartshub.shpets.api.PetsAPI;
import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.Ability;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import org.bukkit.entity.Player;

import java.util.List;

public final class ConditionalAbility extends PetAbility {

    private final String name;
    private final CompiledCondition condition;
    private final List<Ability> subAbilities;

    public ConditionalAbility(String name, CompiledCondition condition, List<Ability> subAbilities) {
        this.name = name;
        this.condition = condition;
        this.subAbilities = subAbilities;
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        CompiledCondition.StringParser parser = input ->
                PetsAPI.getInstance().placeholderService().parse(player, input);

        if (condition.evaluate(parser)) {
            for (Ability ability : subAbilities) {
                ability.execute(player, petData);
            }
        }
    }

    @Override
    public String getName() {
        return "conditional:" + name;
    }

    @Override
    public String toString() {
        return super.toString() + ", condition: " + condition + ", abilities: " + subAbilities.size();
    }
}