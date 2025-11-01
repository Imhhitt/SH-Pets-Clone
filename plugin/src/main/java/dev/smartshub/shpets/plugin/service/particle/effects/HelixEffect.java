package dev.smartshub.shpets.plugin.service.particle.effects;

import dev.smartshub.shpets.api.position.Vect;
import dev.smartshub.shpets.plugin.service.particle.base.BaseParticleEffect;
import org.bukkit.Particle;

public class HelixEffect extends BaseParticleEffect {

    private final float radius;
    private final float height;
    private final float speed;
    private float angle = 0f;

    public HelixEffect(Particle particleType, float radius, float height, float speed) {
        super(particleType, "helix");
        this.radius = radius;
        this.height = height;
        this.speed = speed;
    }

    @Override
    protected void updateParticles(Vect position, float deltaTime) {
        particles.clear();

        angle += speed * deltaTime;
        if (angle >= 360f) angle -= 360f;

        for (int i = 0; i < 8; i++) {
            float currentAngle = angle + (i * 45f);
            double y = position.y() + (height * (i / 7.0)) + 1;
            double x1 = radius * Math.cos(Math.toRadians(currentAngle));
            double z1 = radius * Math.sin(Math.toRadians(currentAngle));

            particles.add(new ParticleData(
                position.x() + x1, y, position.z() + z1,
                0f, 0f, 0f, 0.02f, 1, 1.0f
            ));

            double x2 = radius * Math.cos(Math.toRadians(currentAngle + 180));
            double z2 = radius * Math.sin(Math.toRadians(currentAngle + 180));

            particles.add(new ParticleData(
                position.x() + x2, y, position.z() + z2,
                0f, 0f, 0f, 0.02f, 1, 1.0f
            ));
        }
    }
}