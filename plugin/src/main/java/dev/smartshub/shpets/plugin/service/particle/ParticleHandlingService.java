package dev.smartshub.shpets.plugin.service.particle;

import dev.smartshub.shpets.api.service.particle.ParticleEffect;
import dev.smartshub.shpets.api.service.particle.ParticleEffectType;
import dev.smartshub.shpets.api.service.particle.ParticleService;
import dev.smartshub.shpets.plugin.service.particle.base.ParticleEffectFactory;
import org.bukkit.Particle;

public class ParticleHandlingService implements ParticleService {

    @Override
    public ParticleEffect createEffect(final ParticleEffectType type, final Particle particle) {
        return ParticleEffectFactory.createFloatingEffect(type, particle);
    }
}
