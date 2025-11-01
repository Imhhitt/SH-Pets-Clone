package dev.smartshub.shpets.api.service.particle;

import dev.smartshub.shpets.api.position.Vect;
import org.bukkit.World;

public interface ParticleEffect {
    void update(Vect position, float deltaTime);
    void spawnIn(final World world);
    boolean isFinished();
    void reset();
    String getName();
}