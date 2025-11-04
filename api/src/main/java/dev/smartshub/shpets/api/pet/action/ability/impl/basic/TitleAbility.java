package dev.smartshub.shpets.api.pet.action.ability.impl.basic;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import dev.smartshub.shpets.api.pet.action.trigger.PetAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.Duration;

public final class TitleAbility extends PetAbility {

    private final String title;
    private final String subtitle;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    private final MiniMessage miniMessage;
    private final LegacyComponentSerializer legacySerializer;

    public TitleAbility(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;

        this.miniMessage = MiniMessage.miniMessage();
        this.legacySerializer = LegacyComponentSerializer.builder()
                .character('&')
                .hexCharacter('#')
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build();
    }

    public static TitleAbility fromConfig(ConfigurationSection section) {
        String title = section.getString("title", "");
        String subtitle = section.getString("subtitle", "");
        int fadeIn = section.getInt("fadeIn", 10);
        int stay = section.getInt("stay", 70);
        int fadeOut = section.getInt("fadeOut", 20);
        
        return new TitleAbility(title, subtitle, fadeIn, stay, fadeOut);
    }

    private Component parse(Player player, String message, PetData petData) {
        var parsed = PetAction.createReplacements(petData, player, message);
        Component miniMessageComponent = miniMessage.deserialize(parsed);
        Component legacyComponent = legacySerializer.deserialize(legacySerializer.serialize(miniMessageComponent));
        return legacyComponent.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        player.showTitle(Title.title(
            parse(player, title, petData),
                parse(player, subtitle, petData),
            Title.Times.times(
                Duration.ofMillis(fadeIn * 50L),
                Duration.ofMillis(stay * 50L),
                Duration.ofMillis(fadeOut * 50L)
            )
        ));
    }

    @Override
    public String getName() {
        return "title";
    }
}