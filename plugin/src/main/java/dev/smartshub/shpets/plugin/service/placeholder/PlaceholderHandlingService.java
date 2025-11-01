package dev.smartshub.shpets.plugin.service.placeholder;

import dev.smartshub.shpets.api.service.placeholder.PlaceholderService;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class PlaceholderHandlingService implements PlaceholderService {
    public static final PlaceholderHandlingService INSTANCE = new PlaceholderHandlingService(
        Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")
    );

    private final boolean enable;

    private PlaceholderHandlingService(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String parse(final @NotNull Player player, final @NotNull String string) {
        return enable
            ? PlaceholderAPI.setPlaceholders(player, string)
            : string;
    }

    @Override
    public List<String> parse(final @NotNull Player player, final @NotNull List<String> strings) {
        if (!enable) return strings;

        List<String> result = new ArrayList<>(strings.size());
        for (String s : strings) {
            result.add(PlaceholderAPI.setPlaceholders(player, s));
        }
        return result;
    }

    @Override
    public List<String> parse(
        final @NotNull Player player,
        @NotNull List<String> strings,
        final Object... replacements
    ) {
        List<String> parsed = parse(player, strings);
        List<String> result = new ArrayList<>(parsed.size());

        for (String line : parsed) {
            for (int i = 0; i < replacements.length; i += 2) {
                line = line.replace(replacements[i].toString(), replacements[i + 1].toString());
            }
            result.add(line);
        }
        return result;
    }

    @Override
    public String parse(
        final @NotNull Player player,
        @NotNull String message,
        final Object... replacements
    ) {
        for (int i = 0; i < replacements.length; i += 2) {
            message = message.replace(replacements[i].toString(), replacements[i + 1].toString());
        }
        return parse(player, message);
    }
}