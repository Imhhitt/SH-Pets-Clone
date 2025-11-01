package dev.smartshub.shpets.plugin.service.particle.base;

import dev.smartshub.shpets.api.position.Vect;
import dev.smartshub.shpets.api.service.particle.ParticleEffect;
import dev.smartshub.shpets.api.service.particle.ParticleEffectType;
import dev.smartshub.shpets.plugin.service.particle.effects.HelixEffect;
import dev.smartshub.shpets.plugin.service.particle.effects.OrbitEffect;
import dev.smartshub.shpets.plugin.service.particle.effects.RisingParticlesEffect;
import org.bukkit.Particle;

public final class ParticleEffectFactory {

    public static ParticleEffect createFloatingEffect(ParticleEffectType type, Particle particle) {
        return switch (type) {
            case HELIX -> new HelixEffect(particle, 0.5f, 3.0f, 60f);
            case RISING -> new RisingParticlesEffect(particle, 5f);
            case SPIRAL -> new SpiralEffect(particle, 0.8f, 2.5f, 45f);
            case PULSE -> new PulseEffect(particle, 1.0f, 2.0f);
            case CONSTELLATION -> new ConstellationEffect(particle, 1.2f, 8);
            case MAGIC_CIRCLE -> new MagicCircleEffect(particle, 1.0f, 3);
            default -> new OrbitEffect(particle, 0.7f, 90f, 0.5f);
        };
    }

    public static class SpiralEffect extends BaseParticleEffect {
        private final float radius;
        private final float height;
        private final float speed;
        private float angle = 0f;

        public SpiralEffect(Particle particleType, float radius, float height, float speed) {
            super(particleType, "spiral");
            this.radius = radius;
            this.height = height;
            this.speed = speed;
        }

        @Override
        protected void updateParticles(Vect position, float deltaTime) {
            particles.clear();
            angle += speed * deltaTime;

            for (int i = 0; i < 12; i++) {
                float currentAngle = angle + (i * 30f);
                double currentRadius = radius * (1 - (i / 12.0) * 0.5);
                double y = position.y() + (height * (i / 11.0)) + 1;

                double x = currentRadius * Math.cos(Math.toRadians(currentAngle));
                double z = currentRadius * Math.sin(Math.toRadians(currentAngle));

                particles.add(new ParticleData(
                    position.x() + x, y, position.z() + z,
                    0f, 0f, 0f, 0.02f, 1, 1.0f
                ));
            }
        }
    }

    public static class PulseEffect extends BaseParticleEffect {
        private final float maxRadius;
        private final float pulseSpeed;
        private float currentRadius = 0f;
        private boolean expanding = true;

        public PulseEffect(Particle particleType, float maxRadius, float pulseSpeed) {
            super(particleType, "pulse");
            this.maxRadius = maxRadius;
            this.pulseSpeed = pulseSpeed;
        }

        @Override
        protected void updateParticles(Vect position, float deltaTime) {
            particles.clear();

            if (expanding) {
                currentRadius += pulseSpeed * deltaTime;
                if (currentRadius >= maxRadius) {
                    expanding = false;
                }
            } else {
                currentRadius -= pulseSpeed * deltaTime;
                if (currentRadius <= 0.2f) {
                    expanding = true;
                }
            }

            int particleCount = (int)(currentRadius * 16);
            for (int i = 0; i < particleCount; i++) {
                double angle = (2 * Math.PI * i) / particleCount;
                double x = currentRadius * Math.cos(angle);
                double z = currentRadius * Math.sin(angle);

                particles.add(new ParticleData(
                    position.x() + x, position.y() + 1.5, position.z() + z,
                    0f, 0f, 0f, 0.02f, 1, 1.0f
                ));
            }
        }
    }

    public static class ConstellationEffect extends BaseParticleEffect {
        private final float radius;
        private final int starCount;

        public ConstellationEffect(Particle particleType, float radius, int starCount) {
            super(particleType, "constellation");
            this.radius = radius;
            this.starCount = starCount;
        }

        @Override
        protected void updateParticles(Vect position, float deltaTime) {
            particles.clear();

            for (int i = 0; i < starCount; i++) {
                double angle = (2 * Math.PI * i) / starCount + Math.sin(timeAccumulated) * 0.1;
                double currentRadius = radius + Math.sin(timeAccumulated + i) * 0.3;
                double height = 1.5 + Math.sin(timeAccumulated * 0.5 + i) * 0.5;

                double x = currentRadius * Math.cos(angle);
                double z = currentRadius * Math.sin(angle);

                if (Math.sin(timeAccumulated * 3 + i) > 0) {
                    particles.add(new ParticleData(
                        position.x() + x, position.y() + height, position.z() + z,
                        0f, 0f, 0f, 0.02f, 2, 1.0f
                    ));
                }
            }
        }
    }

    public static class MagicCircleEffect extends BaseParticleEffect {
        private final float radius;
        private final int circles;

        public MagicCircleEffect(Particle particleType, float radius, int circles) {
            super(particleType, "magic_circle");
            this.radius = radius;
            this.circles = circles;
        }

        @Override
        protected void updateParticles(Vect position, float deltaTime) {
            particles.clear();

            for (int circle = 0; circle < circles; circle++) {
                float currentRadius = radius * (circle + 1) / circles;
                float rotationOffset = timeAccumulated * (30f + circle * 15f);

                int particleCount = 8 + circle * 4;
                for (int i = 0; i < particleCount; i++) {
                    double angle = (2 * Math.PI * i) / particleCount + Math.toRadians(rotationOffset);
                    double x = currentRadius * Math.cos(angle);
                    double z = currentRadius * Math.sin(angle);

                    particles.add(new ParticleData(
                        position.x() + x, position.y() + 0.1f, position.z() + z,
                        0f, 0f, 0f, 0.02f, 1, 1.0f
                    ));
                }
            }
        }
    }

    public static class ShockwaveEffect extends BaseParticleEffect {
        private final float maxRadius;
        private final float duration;
        private boolean triggered = false;
        private float currentRadius = 0f;

        public ShockwaveEffect(Particle particleType, float maxRadius, float duration) {
            super(particleType, "shockwave");
            this.maxRadius = maxRadius;
            this.duration = duration;
        }

        public void trigger() {
            triggered = true;
            currentRadius = 0f;
            timeAccumulated = 0f;
        }

        @Override
        protected void updateParticles(Vect position, float deltaTime) {
            if (!triggered) return;

            particles.clear();
            currentRadius = (timeAccumulated / duration) * maxRadius;

            if (timeAccumulated > duration) {
                finished = true;
                return;
            }

            int particleCount = (int)(currentRadius * 20);
            for (int i = 0; i < particleCount; i++) {
                double angle = (2 * Math.PI * i) / particleCount;
                double x = currentRadius * Math.cos(angle);
                double z = currentRadius * Math.sin(angle);

                particles.add(new ParticleData(
                    position.x() + x, position.y() + 0.1, position.z() + z,
                    0f, 0f, 0f, 0.1f, 1, 1.0f
                ));
            }
        }
    }

    public static class FountainEffect extends BaseParticleEffect {
        private final int particleCount;
        private final float power;
        private final java.util.Random random = new java.util.Random();
        private boolean triggered = false;

        public FountainEffect(Particle particleType, int particleCount, float power) {
            super(particleType, "fountain");
            this.particleCount = particleCount;
            this.power = power;
        }

        public void trigger(Vect position) {
            if (triggered) return;
            triggered = true;
            particles.clear();

            for (int i = 0; i < particleCount; i++) {
                float vx = (random.nextFloat() - 0.5f) * power * 0.5f;
                float vy = random.nextFloat() * power + power * 0.5f;
                float vz = (random.nextFloat() - 0.5f) * power * 0.5f;

                particles.add(new ParticleData(
                    position.x(), position.y() + 1, position.z(),
                    vx, vy, vz,
                    0.02f, 1, 4.0f
                ));
            }
        }

        @Override
        protected void updateParticles(Vect position, float deltaTime) {
            if (!triggered) return;

            particles.removeIf(particle -> {
                particle.update(deltaTime);
                particle.velocityY -= 3.0f * deltaTime;
                return !particle.isActive();
            });

            if (particles.isEmpty()) {
                finished = true;
            }
        }
    }

    public static class VortexEffect extends BaseParticleEffect {
        private final float radius;
        private final float height;
        private final int particleCount;
        private boolean triggered = false;

        public VortexEffect(Particle particleType, float radius, float height, int particleCount) {
            super(particleType, "vortex");
            this.radius = radius;
            this.height = height;
            this.particleCount = particleCount;
        }

        public void trigger(Vect position) {
            if (triggered) return;
            triggered = true;
            particles.clear();

            for (int i = 0; i < particleCount; i++) {
                float heightRatio = (float) i / particleCount;
                float currentRadius = radius * (1 - heightRatio);
                double angle = (heightRatio * 720) + timeAccumulated * 180;

                double x = currentRadius * Math.cos(Math.toRadians(angle));
                double z = currentRadius * Math.sin(Math.toRadians(angle));
                double y = height * heightRatio;

                particles.add(new ParticleData(
                    position.x() + x, position.y() + 1 + y, position.z() + z,
                    0f, 0.5f, 0f,
                    0.05f, 1, 3.0f
                ));
            }
        }

        @Override
        protected void updateParticles(Vect position, float deltaTime) {
            if (!triggered) return;

            particles.removeIf(particle -> {
                particle.update(deltaTime);
                return !particle.isActive();
            });

            if (particles.isEmpty()) {
                finished = true;
            }
        }
    }

    public static class StarBurstEffect extends BaseParticleEffect {
        private final int arms;
        private final int particlesPerArm;
        private boolean triggered = false;

        public StarBurstEffect(Particle particleType, int arms, int particlesPerArm) {
            super(particleType, "star_burst");
            this.arms = arms;
            this.particlesPerArm = particlesPerArm;
        }

        public void trigger(Vect position) {
            if (triggered) return;
            triggered = true;
            particles.clear();

            for (int arm = 0; arm < arms; arm++) {
                double armAngle = (2 * Math.PI * arm) / arms;

                for (int p = 0; p < particlesPerArm; p++) {
                    float speed = 2.0f - (p * 0.05f);

                    float vx = (float)(Math.cos(armAngle) * speed);
                    float vy = (float)((Math.random() - 0.5) * 0.5f);
                    float vz = (float)(Math.sin(armAngle) * speed);

                    particles.add(new ParticleData(
                        position.x(), position.y() + 1.5, position.z(),
                        vx, vy, vz,
                        0.03f, 1, 2.5f
                    ));
                }
            }
        }

        @Override
        protected void updateParticles(Vect position, float deltaTime) {
            if (!triggered) return;

            particles.removeIf(particle -> {
                particle.update(deltaTime);
                particle.velocityX *= 0.98f;
                particle.velocityY -= deltaTime;
                particle.velocityZ *= 0.98f;
                return !particle.isActive();
            });

            if (particles.isEmpty()) {
                finished = true;
            }
        }
    }

    public static class HeartEffect extends BaseParticleEffect {
        private final float size;
        private boolean triggered = false;

        public HeartEffect(Particle particleType, float size) {
            super(particleType, "heart");
            this.size = size;
        }

        public void trigger(Vect position) {
            if (triggered) return;
            triggered = true;
            particles.clear();

            for (double t = 0; t < 2 * Math.PI; t += 0.1) {
                double x = size * (16 * Math.pow(Math.sin(t), 3)) / 16;
                double y = size * (13 * Math.cos(t) - 5 * Math.cos(2*t) - 2 * Math.cos(3*t) - Math.cos(4*t)) / 16;

                particles.add(new ParticleData(
                    position.x() + x, position.y() + 2 + y, position.z(),
                    (float)((Math.random() - 0.5) * 0.5),
                    (float)(Math.random() * 0.5),
                    (float)((Math.random() - 0.5) * 0.5),
                    0.02f, 1, 4.0f
                ));
            }
        }

        @Override
        protected void updateParticles(Vect position, float deltaTime) {
            if (!triggered) return;

            particles.removeIf(particle -> {
                particle.update(deltaTime);
                particle.velocityY += 0.5f * deltaTime;
                return !particle.isActive();
            });

            if (particles.isEmpty()) {
                finished = true;
            }
        }
    }
}