package dev.smartshub.shpets.plugin.service.notify;

import dev.smartshub.shpets.plugin.message.MessageParser;
import dev.smartshub.shpets.plugin.message.MessageRepository;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

public class NotifyService {

    private final MessageParser parser;
    private final MessageRepository repository;

    // Default durations for titles
    private final Duration fadeIn = Duration.ofMillis(500);
    private final Duration stay = Duration.ofSeconds(3);
    private final Duration fadeOut = Duration.ofMillis(500);

    public NotifyService(MessageParser parser, MessageRepository repository) {
        this.parser = parser;
        this.repository = repository;
    }

    public void sendChat(Player player, String path) {
        if (player == null) return;
        String message = repository.getMessage(path);
        if (message == null || message.trim().isEmpty()) return;

        Component component = parser.parseWithPlayer(message, player);
        player.sendMessage(component);
    }

    public void sendChat(CommandSender sender, String path) {
        if (sender == null) return;
        String message = repository.getMessage(path);
        if (message == null || message.trim().isEmpty()) return;

        Component component = parser.parse(message);
        sender.sendMessage(component);
    }

    public void sendRawMessage(Player player, String message) {
        player.sendMessage(message);
    }

    public void sendTeamMessage(Player player, String message) {
        var finalMessage = parser.parse(repository.getMessage("team.chat-prefix"))
                .append(Component.text(message));
        player.sendMessage(finalMessage);
    }

    public void sendRawMessage(CommandSender sender, String message) {
        sender.sendMessage(message);
    }

    public void sendTitle(Player player, String path) {
        if (player == null) return;
        String titleText = repository.getMessage(path);
        if (titleText == null || titleText.trim().isEmpty()) return;

        Component titleComponent = parser.parseWithPlayer(titleText, player);
        Title title = Title.title(titleComponent, Component.empty(),
                Title.Times.times(fadeIn, stay, fadeOut));
        player.showTitle(title);
    }

    public void sendSubtitle(Player player, String path) {
        if (player == null) return;
        String subtitleText = repository.getMessage(path);
        if (subtitleText == null || subtitleText.trim().isEmpty()) return;

        Component subtitleComponent = parser.parseWithPlayer(subtitleText, player);
        Title title = Title.title(Component.empty(), subtitleComponent,
                Title.Times.times(fadeIn, stay, fadeOut));
        player.showTitle(title);
    }

}

