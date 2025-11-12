package dev.smartshub.shpets.plugin.command;

import dev.smartshub.shpets.api.pet.action.ability.impl.attack.*;
import dev.smartshub.shpets.api.pet.template.PetTemplate;
import dev.smartshub.shpets.plugin.service.config.ConfigService;
import dev.smartshub.shpets.plugin.service.pet.PetService;
import dev.smartshub.shpets.plugin.service.pet.PetTemplateLoaderService;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;


@Command({"shpets", "pets", "pet"})
public class PetCommand {

    private final PetService petService;
    private final PetTemplateLoaderService templateLoaderService;
    private final ConfigService configService;

    public PetCommand(PetService petService, ConfigService configService,
                      PetTemplateLoaderService templateLoaderService) {
        this.petService = petService;
        this.templateLoaderService = templateLoaderService;
        this.configService = configService;
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
        configService.reloadAll();
        petService.shutdown();
        templateLoaderService.loadAllTemplates();
        actor.reply("§aReloaded pet templates.");
    }

}
