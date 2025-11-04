package dev.smartshub.shpets.api.pet.action.ability.impl.basic;

import dev.smartshub.shpets.api.math.Probability;
import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import dev.smartshub.shpets.api.pet.action.trigger.PetAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class MessageAbility extends PetAbility {

    private final List<PetAction.StringWithProbability> messages;

    private final MiniMessage miniMessage;
    private final LegacyComponentSerializer legacySerializer;

    public MessageAbility(List<PetAction.StringWithProbability> messages) {
        this.messages = messages;
        this.miniMessage = MiniMessage.miniMessage();
        this.legacySerializer = LegacyComponentSerializer.builder()
                .character('&')
                .hexCharacter('#')
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build();
    }

    public static MessageAbility fromConfig(ConfigurationSection section) {
        List<PetAction.StringWithProbability> messages = new ArrayList<>();

        ConfigurationSection parent = section.getParent();
        if (parent == null) {
            return new MessageAbility(messages);
        }

        List<String> list = parent.getStringList(section.getName());

        for (String line : list) {
            messages.add(parseMessageLine(line));
        }

        return new MessageAbility(messages);
    }

    private static PetAction.StringWithProbability parseMessageLine(String line) {
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

    private void parseAndSend(Player player, String message, PetData petData) {
        var parsed = PetAction.createReplacements(petData, player, message);
        Component miniMessageComponent = miniMessage.deserialize(parsed);
        Component legacyComponent = legacySerializer.deserialize(legacySerializer.serialize(miniMessageComponent));
        player.sendMessage(legacyComponent.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        System.out.println("Executing MessageAbility for player: " + player.getName());
        for (PetAction.StringWithProbability message : messages) {
            System.out.println("Checking message: " + message.text() + " with probability: " + message.probability());
            if (Probability.checkProbability(message.probability(), RANDOM)) {
                parseAndSend(player, message.text(), petData);
            }
        }
    }

    @Override
    public String getName() {
        return "messages";
    }
}