package dev.smartshub.shpets.plugin.service.particle.base;

import dev.smartshub.shpets.api.position.Vect;
import dev.smartshub.shpets.api.service.particle.ParticleEffect;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseParticleEffect implements ParticleEffect {

    protected final Particle particle;
    protected final String name;
    protected float timeAccumulated = 0f;
    protected boolean finished = false;
    protected final List<ParticleData> particles = new ArrayList<>();

    public BaseParticleEffect(Particle particle, String name) {
        this.particle = particle;
        this.name = name;
    }

    @Override
    public void update(Vect position, float deltaTime) {
        timeAccumulated += deltaTime;
        updateParticles(position, deltaTime);
    }

    protected abstract void updateParticles(Vect position, float deltaTime);

    @Override
    public void spawnIn(final World world) {
        final Location location = new Location(world, 0, 0, 0);
        for (final ParticleData particleData : particles) {
            location.setX(particleData.x);
            location.setY(particleData.y);
            location.setZ(particleData.z);
            world.spawnParticle(particle, location, particleData.count);
        }
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void reset() {
        timeAccumulated = 0f;
        finished = false;
        particles.clear();
    }

    @Override
    public String getName() {
        return name;
    }

    protected static class ParticleData {
        public double x, y, z;
        public float velocityX, velocityY, velocityZ;
        public float speed;
        public int count;
        public boolean active;
        public float life;
        public float maxLife;

        public ParticleData(double x, double y, double z, float vx, float vy, float vz, float speed, int count, float life) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.velocityX = vx;
            this.velocityY = vy;
            this.velocityZ = vz;
            this.speed = speed;
            this.count = count;
            this.active = true;
            this.life = life;
            this.maxLife = life;
        }

        public void update(float deltaTime) {
            if (active) {
                life -= deltaTime;
                if (life <= 0) {
                    active = false;
                }

                x += velocityX * deltaTime;
                y += velocityY * deltaTime;
                z += velocityZ * deltaTime;
            }
        }

        public boolean isActive() {
            return active && life > 0;
        }
    }
}