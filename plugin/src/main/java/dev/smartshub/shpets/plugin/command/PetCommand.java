package dev.smartshub.shpets.plugin.command;

import dev.smartshub.shpets.api.pet.action.ability.impl.attack.*;
import dev.smartshub.shpets.api.pet.template.PetTemplate;
import dev.smartshub.shpets.plugin.Main;
import dev.smartshub.shpets.plugin.service.pet.PetService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.List;

@Command({"shpets", "pets", "pet"})
public class PetCommand {

    private final PetService petService;
    private final Main plugin;

    public PetCommand(PetService petService, Main plugin) {
        this.petService = petService;
        this.plugin = plugin;
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

    @Subcommand("to-all")
    @CommandPermission("shpets.admin")
    public void toAll(BukkitCommandActor actor, PetTemplate pet) {

        var perm = pet.permission();

        new BukkitRunnable() {
            private final List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            private int index = 0;

            @Override
            public void run() {
                if (players.isEmpty() || index >= players.size()) {
                    cancel();
                    return;
                }

                Player player = players.get(index);
                player.addAttachment(plugin, perm, true);
                if (player.isOnline()) {
                    petService.spawnPet(player, pet.id());
                }

                index++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

}
