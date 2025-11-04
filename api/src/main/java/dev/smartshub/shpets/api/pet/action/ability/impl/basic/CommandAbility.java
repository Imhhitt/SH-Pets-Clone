package dev.smartshub.shpets.api.pet.action.ability.impl.basic;

import dev.smartshub.shpets.api.math.Probability;
import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import dev.smartshub.shpets.api.pet.action.trigger.PetAction;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class CommandAbility extends PetAbility {
    private final List<PetAction.StringWithProbability> commands;
    private final boolean isConsole;

    public CommandAbility(List<PetAction.StringWithProbability> commands, boolean isConsole) {
        this.commands = commands;
        this.isConsole = isConsole;
    }

    public static CommandAbility fromConfig(ConfigurationSection section) {
        String sectionName = section.getName();
        boolean isConsole = sectionName.equals("console-commands");

        List<PetAction.StringWithProbability> commands = new ArrayList<>();

        ConfigurationSection parent = section.getParent();
        if (parent != null) {
            List<String> list = parent.getStringList(sectionName);
            for (String line : list) {
                commands.add(parseCommandLine(line));
            }
        }

        return new CommandAbility(commands, isConsole);
    }

    private static PetAction.StringWithProbability parseCommandLine(String line) {
        if (line.contains("%:")) {
            String[] parts = line.split("%:", 2);
            try {
                double prob = Double.parseDouble(parts[0].trim());
                return new PetAction.StringWithProbability(parts[1].trim(), prob);
            } catch (NumberFormatException e) {
                return new PetAction.StringWithProbability(line, 100.0);
            }
        }
        return new PetAction.StringWithProbability(line, 100.0);
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        CommandSender commandSender = isConsole ? Bukkit.getConsoleSender() : player;
        for (PetAction.StringWithProbability command : commands) {
            if (Probability.checkProbability(command.probability(), RANDOM)) {
                String parsed = PetAction.createReplacements(petData, player, command.text());
                Bukkit.dispatchCommand(commandSender, parsed);
            }
        }
    }

    @Override
    public String getName() {
        return isConsole ? "console-commands" : "player-commands";
    }
}