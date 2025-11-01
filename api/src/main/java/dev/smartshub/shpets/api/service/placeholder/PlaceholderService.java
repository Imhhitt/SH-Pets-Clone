package dev.smartshub.shpets.api.service.placeholder;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PlaceholderService {
    List<String> parse(final @NotNull Player player, final @NotNull List<String> strings);
    List<String> parse(
        final @NotNull Player player,
        @NotNull List<String> strings,
        final Object... replacements
    );

    String parse(final @NotNull Player player, final @NotNull String string);
    String parse(
        final @NotNull Player player,
        @NotNull String message,
        final Object... replacements
    );
}
