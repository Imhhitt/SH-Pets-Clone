package dev.smartshub.shpets.plugin.command;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.Ability;
import dev.smartshub.shpets.api.pet.action.ability.impl.attack.*;
import dev.smartshub.shpets.api.pet.action.trigger.PetAction;
import dev.smartshub.shpets.api.pet.action.trigger.PetActions;
import dev.smartshub.shpets.api.pet.apparence.PetAppearance;
import dev.smartshub.shpets.api.pet.behavior.PetBehavior;
import dev.smartshub.shpets.api.pet.template.EntityData;
import dev.smartshub.shpets.api.pet.template.PetTemplate;
import dev.smartshub.shpets.api.service.context.PetContextService;
import dev.smartshub.shpets.plugin.Main;
import dev.smartshub.shpets.plugin.service.pet.PetService;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Command({"shpets", "pets", "pet"})
public class PetCommand {

    private static final String ATTACK_SUGGESTIONS = String.join(", ",
            "fireball-attack",
            "fire-blast",
            "crystal-shard",
            "frost-attack",
            "wind-slash",
            "wind-blast",
            "thunder-strike",
            "slash-attack",
            "spin-attack",
            "root-grab",
            "explosion-attack",
            "healing-aura",
            "ice-beam-attack",
            "lightning-strike",
            "poison-spit"
    );

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

    @Subcommand("test")
    @CommandPermission("shpets.admin")
    public void testAttack(BukkitCommandActor actor, String abilityName) {
        Player player = actor.asPlayer();

        if (abilityName == null || abilityName.isEmpty()) {
            actor.reply("§cUsage: /shpets test <ability>§7. Available: §e" + ATTACK_SUGGESTIONS + "§7.");
            return;
        }

        Ability ability = createAbilityByName(abilityName.toLowerCase());
        if (ability == null) {
            actor.reply("§cUnknown ability: §e" + abilityName + "§7. Available: §e" + ATTACK_SUGGESTIONS.replace('|', ',') + "§7.");
            return;
        }

        Entity targetEntity = player.getTargetEntity(30);
        if (!(targetEntity instanceof LivingEntity living)) {
            actor.reply("§cLook at a living entity within §e30§c blocks to test this attack.");
            return;
        }

        // Create a minimal PetData and context
        PetTemplate template = new PetTemplate(
                "test",
                "Test Pet",
                "shpets.test",
                "ARMOR_STAND",
                new EntityData("ARMOR_STAND", false),
                new PetBehavior(null, 0.3, false, 0f, 0f, false, true),
                new PetAppearance("Test Pet", false, null, null),
                new PetActions(new PetAction(List.of()), new PetAction(List.of()), new PetAction(List.of()), 20, new PetAction(List.of()), new PetAction(List.of()))
        );

        PetData petData = new PetData(player, template);
        UUID petId = petData.getUniqueId();

        PetContextService.setPetLocation(petId, player.getLocation());
        PetContextService.setPetTarget(petId, living.getUniqueId());

        ability.execute(player, petData);
        actor.reply("§aTriggered ability §e" + abilityName + "§a on target §e" + living.getName() + "§a.");
    }

    private Ability createAbilityByName(String name) {
        switch (name) {
            case "fireball-attack":
                return new FireballAttackAbility(5.0, Particle.FLAME, Sound.ENTITY_GHAST_SHOOT);
            case "fire-blast":
                return new FireBlastAbility(6.0, 14.0, Particle.FLAME);
            case "crystal-shard":
                return new CrystalShardAbility(5.5, 15.0, Particle.END_ROD);
            case "frost-attack":
                return new FrostAttackAbility(4.0, 12.0, Particle.SNOWFLAKE);
            case "wind-slash":
                return new WindSlashAbility(5.0, 15.0, Particle.CLOUD);
            case "wind-blast":
                return new WindBlastAbility(1.2, Particle.CLOUD, Sound.ENTITY_ENDER_DRAGON_FLAP);
            case "thunder-strike":
                return new ThunderStrikeAbility(8.0, Particle.ELECTRIC_SPARK, Sound.ENTITY_LIGHTNING_BOLT_THUNDER);
            case "slash-attack":
                return new SlashAttackAbility(6.0, 8.0, Particle.SWEEP_ATTACK, Sound.ENTITY_PLAYER_ATTACK_SWEEP);
            case "spin-attack":
                return new SpinAttackAbility(4.0, 3.0, Particle.SWEEP_ATTACK, Sound.ENTITY_PLAYER_ATTACK_SWEEP);
            case "root-grab":
                return new RootGrabAbility(4.0, 80, Particle.CLOUD, Sound.BLOCK_GRASS_BREAK);
            case "explosion-attack":
                return new ExplosionAttackAbility(2.0, Particle.CLOUD, Sound.ENTITY_GENERIC_EXPLODE);
            case "healing-aura":
                return new HealingAuraAbility(4.0, 5.0, Particle.HEART, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            case "ice-beam-attack":
                return new IceBeamAttackAbility(5.0, 2.0, Particle.SNOWFLAKE, Sound.BLOCK_GLASS_BREAK);
            case "lightning-strike":
                return new LightningStrikeAbility(8.0, Particle.ELECTRIC_SPARK);
            case "poison-spit":
                return new PoisonSpitAbility(3.0, Particle.HEART, Sound.ENTITY_SLIME_JUMP, 60);
            default:
                return null;
        }
    }

}
