package dev.smartshub.shpets.plugin.message;

import io.papermc.paper.adventure.PaperAdventure;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class MessageParser {

    private final MiniMessage miniMessage;
    private final LegacyComponentSerializer legacySerializer;
    private final boolean placeholderAPIAvailable;

    public MessageParser() {
        this.miniMessage = MiniMessage.miniMessage();
        this.legacySerializer = LegacyComponentSerializer.builder()
                .character('&')
                .hexCharacter('#')
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build();
        this.placeholderAPIAvailable = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        if (!placeholderAPIAvailable) {
            Bukkit.getLogger().info("[SH-Pets] PlaceholderAPI not available - placeholders will not be processed");
        }
    }

    private Component parseMessage(String message) {
        Component miniMessageComponent = miniMessage.deserialize(message);
        Component legacyComponent = legacySerializer.deserialize(legacySerializer.serialize(miniMessageComponent));
        return legacyComponent.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public net.minecraft.network.chat.Component toNMS(String message, Player player) {
        return PaperAdventure.asVanilla(parseWithPlayer(message, player));
    }

    public net.minecraft.network.chat.Component toNMS(String message) {
        return PaperAdventure.asVanilla(parse(message));
    }

    public Component parseWithPlayer(String message, Player player) {
        if (message == null || message.trim().isEmpty()) {
            return Component.empty();
        }

        String processedMessage = placeholderAPIAvailable
                ? PlaceholderAPI.setPlaceholders(player, message)
                : message;
        return parseMessage(processedMessage);
    }

    public Component parse(String message) {
        if (message == null || message.trim().isEmpty()) {
            return Component.empty();
        }

        return parseMessage(message);
    }

    public List<Component> parseListWithPlayer(List<String> messages, Player player) {
        if (messages == null || messages.isEmpty()) {
            return List.of();
        }

        return messages.stream()
                .filter(message -> message != null && !message.trim().isEmpty())
                .map(message -> parseWithPlayer(message, player))
                .collect(Collectors.toList());
    }

    public List<Component> parseList(List<String> messages) {
        if (messages == null || messages.isEmpty()) {
            return List.of();
        }

        return messages.stream()
                .filter(message -> message != null && !message.trim().isEmpty())
                .map(this::parse)
                .collect(Collectors.toList());
    }

    public String toString(Component component) {
        if (component == null) {
            return "";
        }
        return miniMessage.serialize(component);
    }
}
