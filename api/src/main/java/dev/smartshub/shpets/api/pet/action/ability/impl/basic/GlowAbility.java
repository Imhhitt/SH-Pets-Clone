package dev.smartshub.shpets.api.pet.action.ability.impl.basic;

import dev.smartshub.shpets.api.event.dispatcher.ServiceEventDispatcher;
import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public final class GlowAbility extends PetAbility {

    private final NamedTextColor color;
    private final int duration;
    private final ServiceEventDispatcher eventDispatcher = new ServiceEventDispatcher();

    public GlowAbility(NamedTextColor color, int duration) {
        this.color = color;
        this.duration = duration;
    }

    public static GlowAbility fromConfig(ConfigurationSection section) {
        String colorName = section.getString("color", "WHITE");
        NamedTextColor color;

        try {
            color = NamedTextColor.NAMES.value(colorName.toLowerCase());
            if (color == null) {
                color = NamedTextColor.WHITE;
            }
        } catch (Exception e) {
            color = NamedTextColor.WHITE;
        }

        int duration = section.getInt("duration", 100);

        return new GlowAbility(color, duration);
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        System.out.println("Executing GlowAbility: color=" + color + ", duration=" + duration);
        eventDispatcher.fireEntityGlowEvent(petData.getUniqueId(), duration, color);
    }

    @Override
    public String getName() {
        return "glow";
    }

    public NamedTextColor getColor() {
        return color;
    }

    public int getDuration() {
        return duration;
    }
}