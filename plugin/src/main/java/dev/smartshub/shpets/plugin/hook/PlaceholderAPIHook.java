package dev.smartshub.shpets.plugin.hook;

import dev.smartshub.shpets.plugin.service.pet.PetService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final PetService petService;

    public PlaceholderAPIHook(PetService petService) {
        this.petService = petService;
    }


    @NotNull
    @Override
    public String getIdentifier() {
        return "shpets";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "hhitt";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {

        return switch (identifier.toLowerCase()) {
            case "pet_id" -> petService.getSpawnedPetByOwner(player.getUniqueId()) == null ? "" : petService.getSpawnedPetByOwner(player.getUniqueId()).getData().getTemplate().id();
            case "has_pet_equipped" -> petService.hasSpawnedPet(player.getUniqueId()) ? "true" : "false";
            default -> "Invalid placeholder";
        };
    }

}
