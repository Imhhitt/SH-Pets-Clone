package dev.smartshub.shpets.api.service.glow;

import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

public interface GlowService<T> {
    void setGlowing(T t, NamedTextColor color);
    void setGlowing(T t, NamedTextColor color, int duration);
    void removeGlowing(T t);
    void changeGlowingColor(T t, @Nullable NamedTextColor newColor);
    void refreshGlowing(T t);
}
