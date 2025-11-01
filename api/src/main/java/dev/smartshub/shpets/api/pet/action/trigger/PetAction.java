package dev.smartshub.shpets.api.pet.action.trigger;

import dev.smartshub.shpets.api.PetsAPI;
import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.Ability;
import dev.smartshub.shpets.api.service.placeholder.PlaceholderService;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public record PetAction(
    @NotNull Collection<Ability> abilities
) {
    public record StringWithProbability(String text, double probability) {}

    public void execute(final PetData petData, final Player target) {
        executeAbilities(petData, target);
    }

    public void executeAbilities(final PetData petData, final Player target) {
        for (final Ability ability : abilities) {
            ability.execute(target, petData);
        }
    }

    public static String createReplacements(PetData petData, Player target, String string) {
        final PlaceholderService placeholderService = PetsAPI.getInstance().placeholderService();
        return placeholderService.parse(target, string,
            "%owner%", petData.getOwner().getName(),
            "%player%", target.getName(),
            "%pet_id%", petData.getTemplate().id(),
            "%pet_name%", petData.getTemplate().displayName()
        );
    }
}
