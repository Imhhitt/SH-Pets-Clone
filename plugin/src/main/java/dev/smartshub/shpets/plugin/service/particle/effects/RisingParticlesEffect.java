package dev.smartshub.shpets.plugin.service.particle.effects;

import dev.smartshub.shpets.api.position.Vect;
import dev.smartshub.shpets.plugin.service.particle.base.BaseParticleEffect;
import org.bukkit.Particle;

public class RisingParticlesEffect extends BaseParticleEffect {

    private final float spawnRate;
    private final java.util.Random random = new java.util.Random();
    private float spawnTimer = 0f;

    public RisingParticlesEffect(Particle particleType, float spawnRate) {
        super(particleType, "rising");
        this.spawnRate = spawnRate;
    }

    @Override
    protected void updateParticles(Vect position, float deltaTime) {
        spawnTimer += deltaTime;

        if (spawnTimer >= (1.0f / spawnRate)) {
            spawnTimer = 0f;

            double x = position.x() + (random.nextGaussian() * 0.3);
            double z = position.z() + (random.nextGaussian() * 0.3);

            particles.add(new ParticleData(
                x, position.y() + 1, z,
                (random.nextFloat() - 0.5f) * 0.2f,
                random.nextFloat() * 1.5f + 0.5f,
                (random.nextFloat() - 0.5f) * 0.2f,
                0.03f, 1, 4.0f
            ));
        }

        particles.removeIf(particle -> {
            particle.update(deltaTime);
            particle.velocityY += 0.1f * deltaTime;
            return !particle.isActive();
        });
    }
}