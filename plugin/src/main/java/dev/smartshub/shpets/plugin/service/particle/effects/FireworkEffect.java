package dev.smartshub.shpets.plugin.service.particle.effects;

import dev.smartshub.shpets.api.position.Vect;
import dev.smartshub.shpets.plugin.service.particle.base.BaseParticleEffect;
import org.bukkit.Particle;

public class FireworkEffect extends BaseParticleEffect {

    private final java.util.Random random = new java.util.Random();
    private boolean triggered = false;
    private float stage = 0f;
    private final java.util.List<Rocket> rockets = new java.util.ArrayList<>();
    private static final int ROCKET_COUNT = 2;

    public FireworkEffect(Particle particleType) {
        super(particleType, "firework");
    }

    public void trigger(Vect position) {
        if (triggered) return;
        triggered = true;
        particles.clear();
        rockets.clear();
        stage = 0f;

        for (int i = 0; i < ROCKET_COUNT; i++) {
            float delayOffset = i * 0.4f;
            float targetHeight = 3.5f + random.nextFloat() * 2.0f;
            float horizontalOffset = (random.nextFloat() - 0.5f);

            Rocket rocket = new Rocket(
                position.x() + horizontalOffset,
                position.y() + 1,
                position.z() + (random.nextFloat() - 0.5f),
                targetHeight,
                delayOffset
            );
            rockets.add(rocket);
        }
    }

    @Override
    protected void updateParticles(Vect position, float deltaTime) {
        if (!triggered) return;

        stage += deltaTime;

        for (Rocket rocket : rockets) {
            rocket.update(deltaTime);
        }

        particles.removeIf(particle -> {
            particle.update(deltaTime);

            if (particle.velocityY > -2.0f) {
                particle.velocityY -= 4.0f * deltaTime;
            }

            particle.velocityX *= 0.97f;
            particle.velocityZ *= 0.97f;

            return !particle.isActive();
        });

        if (stage > 7.0f || (rockets.stream().allMatch(Rocket::isFinished) && particles.isEmpty())) {
            finished = true;
        }
    }

    @Override
    public void reset() {
        super.reset();
        triggered = false;
        stage = 0f;
        rockets.clear();
    }

    private class Rocket {
        private final double x;
        private double y;
        private final double z;
        private final double startY;
        private final float targetHeight;
        private final float delay;
        private boolean launched = false;
        private boolean exploded = false;
        private float rocketTime = 0f;
        private final RocketType type;

        public Rocket(double x, double y, double z, float targetHeight, float delay) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.startY = y;
            this.targetHeight = targetHeight;
            this.delay = delay;
            this.type = RocketType.values()[random.nextInt(RocketType.values().length)];
        }

        public void update(float deltaTime) {
            if (!launched) {
                if (stage >= delay) {
                    launched = true;
                }
                return;
            }

            rocketTime += deltaTime;

            if (!exploded) {
                double progress = rocketTime / 1.5f;

                if (progress < 1.0f) {
                    if (rocketTime % 0.15f < deltaTime) {
                        particles.add(new ParticleData(
                            x + (random.nextFloat() - 0.5f) * 0.2f,
                            y - 0.3f,
                            z + (random.nextFloat() - 0.5f) * 0.2f,
                            (random.nextFloat() - 0.5f) * 0.3f,
                            -0.3f,
                            (random.nextFloat() - 0.5f) * 0.3f,
                            0.02f, 2, 1.5f
                        ));
                    }

                    y = startY + (targetHeight * easeOut(progress));
                } else {
                    exploded = true;
                    createExplosion();
                }
            }
        }

        private void createExplosion() {
            switch (type) {
                case SPHERE -> createSphereExplosion();
                case STAR -> createStarExplosion();
                case RING -> createRingExplosion();
                case WILLOW -> createWillowExplosion();
            }
        }

        private void createSphereExplosion() {
            int particleCount = 15 + random.nextInt(10);
            for (int i = 0; i < particleCount; i++) {
                float u = random.nextFloat();
                float v = random.nextFloat();
                double theta = 2 * Math.PI * u;
                double phi = Math.acos(2 * v - 1);

                float speed = 1.5f + random.nextFloat() * 1.0f;
                float vx = (float)(speed * Math.sin(phi) * Math.cos(theta));
                float vy = (float)(speed * Math.sin(phi) * Math.sin(theta));
                float vz = (float)(speed * Math.cos(phi));

                particles.add(new ParticleData(
                    x, y, z, vx, vy, vz,
                    0.08f, 3, 2.5f + random.nextFloat()
                ));
            }
        }

        private void createStarExplosion() {
            int arms = 5;
            int particlesPerArm = 6;

            for (int arm = 0; arm < arms; arm++) {
                double armAngle = (2 * Math.PI * arm) / arms;

                for (int p = 0; p < particlesPerArm; p++) {
                    float distance = (p + 1) * 0.25f;
                    float speed = 2.0f - (p * 0.2f);

                    float vx = (float)(Math.cos(armAngle) * speed);
                    float vy = (float)((random.nextFloat() - 0.5f) * 0.5f);
                    float vz = (float)(Math.sin(armAngle) * speed);

                    particles.add(new ParticleData(
                        x, y, z, vx, vy, vz,
                        0.07f, 2, 3.0f
                    ));
                }
            }
        }

        private void createRingExplosion() {
            int ringCount = 2;
            for (int ring = 0; ring < ringCount; ring++) {
                float ringRadius = 1.2f + ring * 0.6f;
                float ringHeight = (ring - 1) * 0.3f;
                int particleCount = 12 + ring * 4;

                for (int i = 0; i < particleCount; i++) {
                    double angle = (2 * Math.PI * i) / particleCount;
                    float vx = (float)(Math.cos(angle) * ringRadius);
                    float vz = (float)(Math.sin(angle) * ringRadius);
                    float vy = ringHeight + (random.nextFloat() - 0.5f) * 0.2f;

                    particles.add(new ParticleData(
                        x, y, z, vx, vy, vz,
                        0.06f, 1, 2.5f
                    ));
                }
            }
        }

        private void createWillowExplosion() {
            int particleCount = 20;
            for (int i = 0; i < particleCount; i++) {
                float vx = (random.nextFloat() - 0.5f) * 2.0f;
                float vy = 1.2f + random.nextFloat() * 1.5f;
                float vz = (random.nextFloat() - 0.5f) * 2.0f;

                particles.add(new ParticleData(
                    x, y, z, vx, vy, vz,
                    0.05f, 2, 3.5f
                ));
            }
        }

        private double easeOut(double t) {
            return 1 - Math.pow(1 - t, 3);
        }

        public boolean isFinished() {
            return exploded;
        }
    }

    private enum RocketType {
        SPHERE,
        STAR,
        RING,
        WILLOW
    }
}