package dev.smartshub.shpets.plugin.service.particle.effects;

import dev.smartshub.shpets.api.position.Vect;
import dev.smartshub.shpets.plugin.service.particle.base.BaseParticleEffect;
import org.bukkit.Particle;

public class OrbitEffect extends BaseParticleEffect {

    private final float radius;
    private final float speed;
    private final float height;
    private float angle = 0f;

    public OrbitEffect(Particle particle, float radius, float speed, float height) {
        super(particle, "orbit");
        this.radius = radius;
        this.speed = speed;
        this.height = height;
    }

    @Override
    protected void updateParticles(Vect position, float deltaTime) {
        particles.clear();

        angle += speed * deltaTime * 50f;
        if (angle >= 360f) angle -= 360f;

        for (int i = 0; i < 8; i++) {
            float currentAngle = angle + (i * 45f);
            double x = radius * Math.cos(Math.toRadians(currentAngle));
            double z = radius * Math.sin(Math.toRadians(currentAngle));
            double y = position.y() + height + 1.5;

            particles.add(new ParticleData(
                position.x() + x, y, position.z() + z,
                0f, 0f, 0f, 0.02f, 1, 1.0f
            ));
        }
    }
}