package dev.smartshub.shpets.api.service.particle;

import org.bukkit.Particle;

public interface ParticleService {
    ParticleEffect createEffect(final ParticleEffectType type, final Particle particle);
}
