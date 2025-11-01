package dev.smartshub.shpets.api.position;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class Vect {

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public Vect(double x, double y, double z) {
        this(x, y, z, 0f, 0f);
    }

    public Vect(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Vect(int x, int y, int z) {
        this((double) x, (double) y, (double) z);
    }

    public Vect(int x, int y, int z, float yaw, float pitch) {
        this((double) x, (double) y, (double) z, yaw, pitch);
    }

    public Vect(float x, float y, float z) {
        this((double) x, (double) y, (double) z);
    }

    public Vect(float x, float y, float z, float yaw, float pitch) {
        this((double) x, (double) y, (double) z, yaw, pitch);
    }

    public static Vect of(double x, double y, double z) {
        return new Vect(x, y, z);
    }

    public static Vect of(double x, double y, double z, float yaw, float pitch) {
        return new Vect(x, y, z, yaw, pitch);
    }

    public static Vect fromLocation(Location location) {
        return new Vect(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    public static Vect fromVector(Vector vector) {
        return new Vect(vector.getX(), vector.getY(), vector.getZ());
    }

    public static Vect zero() {
        return new Vect(0, 0, 0);
    }

    public Vect add(double x, double y, double z) {
        return new Vect(this.x + x, this.y + y, this.z + z, this.yaw, this.pitch);
    }

    public Vect add(Vect other) {
        return add(other.x, other.y, other.z);
    }

    public Vect subtract(double x, double y, double z) {
        return new Vect(this.x - x, this.y - y, this.z - z, this.yaw, this.pitch);
    }

    public Vect subtract(Vect other) {
        return subtract(other.x, other.y, other.z);
    }

    public Vect multiply(double scalar) {
        return new Vect(x * scalar, y * scalar, z * scalar, yaw, pitch);
    }

    public Vect multiply(double x, double y, double z) {
        return new Vect(this.x * x, this.y * y, this.z * z, yaw, pitch);
    }

    public Vect divide(double scalar) {
        if (scalar == 0) throw new ArithmeticException("Cannot divide by zero");
        return new Vect(x / scalar, y / scalar, z / scalar, yaw, pitch);
    }

    public Vect normalize() {
        double length = length();
        if (length == 0) return this;
        return divide(length);
    }

    public Vect withYaw(float yaw) {
        return new Vect(x, y, z, yaw, pitch);
    }

    public Vect withPitch(float pitch) {
        return new Vect(x, y, z, yaw, pitch);
    }

    public Vect withRotation(float yaw, float pitch) {
        return new Vect(x, y, z, yaw, pitch);
    }

    public Vect addYaw(float yaw) {
        return new Vect(x, y, z, this.yaw + yaw, pitch);
    }

    public Vect addPitch(float pitch) {
        return new Vect(x, y, z, yaw, this.pitch + pitch);
    }

    public double distance(Vect other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double distanceSquared(Vect other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return dx * dx + dy * dy + dz * dz;
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    public double dot(Vect other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vect cross(Vect other) {
        return new Vect(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x
        );
    }

    public Vect lerp(Vect target, double factor) {
        return new Vect(
                x + (target.x - x) * factor,
                y + (target.y - y) * factor,
                z + (target.z - z) * factor,
                yaw + (target.yaw - yaw) * (float) factor,
                pitch + (target.pitch - pitch) * (float) factor
        );
    }

    public Vect withX(double x) {
        return new Vect(x, y, z, yaw, pitch);
    }

    public Vect withY(double y) {
        return new Vect(x, y, z, yaw, pitch);
    }

    public Vect withZ(double z) {
        return new Vect(x, y, z, yaw, pitch);
    }

    public Vect addX(double x) {
        return new Vect(this.x + x, y, z, yaw, pitch);
    }

    public Vect addY(double y) {
        return new Vect(x, this.y + y, z, yaw, pitch);
    }

    public Vect addZ(double z) {
        return new Vect(x, y, this.z + z, yaw, pitch);
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public int blockX() {
        return (int) Math.floor(x);
    }

    public int blockY() {
        return (int) Math.floor(y);
    }

    public int blockZ() {
        return (int) Math.floor(z);
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public float yaw() {
        return yaw;
    }

    public float pitch() {
        return pitch;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float xf() {
        return (float) x;
    }

    public float yf() {
        return (float) y;
    }

    public float zf() {
        return (float) z;
    }

    public int xi() {
        return (int) x;
    }

    public int yi() {
        return (int) y;
    }

    public int zi() {
        return (int) z;
    }

    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    public boolean hasRotation() {
        return yaw != 0 || pitch != 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vect vect = (Vect) o;

        return Double.compare(vect.x, x) == 0 &&
                Double.compare(vect.y, y) == 0 &&
                Double.compare(vect.z, z) == 0 &&
                Float.compare(vect.yaw, yaw) == 0 &&
                Float.compare(vect.pitch, pitch) == 0;
    }

    @Override
    public String toString() {
        if (hasRotation()) {
            return String.format("Vect{x=%.2f, y=%.2f, z=%.2f, yaw=%.2f, pitch=%.2f}",
                    x, y, z, yaw, pitch);
        }
        return String.format("Vect{x=%.2f, y=%.2f, z=%.2f}", x, y, z);
    }
}