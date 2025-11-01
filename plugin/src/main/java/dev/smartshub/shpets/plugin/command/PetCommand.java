package dev.smartshub.shpets.plugin.command;

import dev.smartshub.shpets.api.pet.template.PetTemplate;
import dev.smartshub.shpets.plugin.service.pet.PetService;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"shpets", "pets", "pet"})
public class PetCommand {

    private final PetService petService;

    public PetCommand(PetService petService) {
        this.petService = petService;
    }

    @Subcommand("equip")
    public void spawn(BukkitCommandActor actor, PetTemplate pet) {
        petService.spawnPet(actor.asPlayer(), pet.id());
    }

    @Subcommand("unequip")
    public void despawn(BukkitCommandActor actor) {
        petService.despawnPlayerPets(actor.asPlayer().getUniqueId());
    }

    @Subcommand("reload")
    @CommandPermission("shpets.admin")
    public void reload(BukkitCommandActor actor) {
        //TODO: implement reload functionality
        actor.reply("§aReloaded pet templates.");
    }

}
