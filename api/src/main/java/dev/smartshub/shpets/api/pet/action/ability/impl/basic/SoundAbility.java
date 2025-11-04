package dev.smartshub.shpets.api.pet.action.ability.impl.basic;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public final class SoundAbility extends PetAbility {
    private final Sound sound;
    private final float volume;
    private final float pitch;
    private final SoundTarget target;

    public enum SoundTarget { PLAYER, LOCATION }

    public SoundAbility(Sound sound, float volume, float pitch, SoundTarget target) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.target = target;
    }

    public static SoundAbility fromConfig(ConfigurationSection section) {
        String soundName = section.getString("name", "ENTITY_EXPERIENCE_ORB_PICKUP");
        Sound sound;
        
        try {
            sound = Sound.valueOf(soundName.toUpperCase().replace("MINECRAFT:", "").replace(".", "_"));
        } catch (IllegalArgumentException e) {
            // Fallback si el sonido no existe
            sound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        }
        
        float volume = (float) section.getDouble("volume", 1.0);
        float pitch = (float) section.getDouble("pitch", 1.0);
        String targetStr = section.getString("target", "PLAYER").toUpperCase();
        SoundTarget target = SoundTarget.valueOf(targetStr);
        
        return new SoundAbility(sound, volume, pitch, target);
    }

    @Override
    protected void executeAbility(Player player, PetData petData) {
        switch (target) {
            case PLAYER -> player.playSound(player.getLocation(), sound, volume, pitch);
            case LOCATION -> player.getWorld().playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    @Override
    public String getName() {
        return "sound";
    }
}