package dev.smartshub.shpets.plugin.service.glow;

import dev.smartshub.shpets.api.service.glow.GlowService;
import dev.smartshub.shpets.plugin.Main;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class GlowHandlingService implements GlowService<Entity> {

    private final Main plugin;
    private final GlowEffectService glowEffectService;
    private final HashMap<UUID, NamedTextColor> glowingEntities = new HashMap<>();

    public GlowHandlingService(Main plugin, GlowEffectService glowEffectService) {
        this.plugin = plugin;
        this.glowEffectService = glowEffectService;
    }

    @Override
    public void setGlowing(Entity entity, NamedTextColor color) {
        System.out.println("Setting glowing effect: uuid=" + entity.getUUID() + ", color=" + color);
        glowingEntities.put(entity.getUUID(), color);
        glowEffectService.setGlowing(entity, color);
    }

    @Override
    public void setGlowing(Entity entity, NamedTextColor color, int duration) {
        System.out.println("Setting glowing effect: uuid=" + entity.getUUID() + ", color=" + color + ", duration=" + duration);
        glowingEntities.put(entity.getUUID(), color);
        glowEffectService.setGlowing(entity, color);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> glowEffectService.removeGlowing(entity), duration);
    }

    @Override
    public void removeGlowing(Entity entity) {
        glowEffectService.removeGlowing(entity);
        glowingEntities.remove(entity.getUUID());
    }

    @Override
    public void changeGlowingColor(Entity entity, @Nullable NamedTextColor newColor) {
        glowEffectService.setGlowing(entity, newColor);
        glowingEntities.put(entity.getUUID(), newColor);
    }

    @Override
    public void refreshGlowing(Entity entity) {
        if(!glowingEntities.containsKey(entity.getUUID())) return;

        NamedTextColor color = glowingEntities.get(entity.getUUID());
        glowEffectService.setGlowing(entity, color);
    }
}
