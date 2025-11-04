package dev.smartshub.shpets.api.pet.action.ability.path;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class PathTracker {

    public static double STEP_SIZE = 0.15;
    public static double REACH_THRESHOLD = 0.3;

    private final Location startLocation;
    private final Location returnLocation;
    private final TargetProvider targetProvider;
    
    private Vector currentPosition;
    private PathPhase currentPhase;
    private boolean completed;
    
    private int tickCount;


    public PathTracker(Location startLocation, TargetProvider targetProvider, Location returnLocation) {
        this.startLocation = startLocation.clone();
        this.returnLocation = returnLocation.clone();
        this.targetProvider = targetProvider;
        this.currentPosition = startLocation.toVector();
        this.currentPhase = PathPhase.GOING_TO_TARGET;
        this.completed = false;
        this.tickCount = 0;
    }


    public PathTracker(Location startLocation, TargetProvider targetProvider) {
        this(startLocation, targetProvider, null);
    }


    public boolean tick() {
        if (completed) {
            return false;
        }

        tickCount++;

        switch (currentPhase) {
            case GOING_TO_TARGET:
                return tickGoingToTarget();
            case RETURNING:
                return tickReturning();
            default:
                completed = true;
                return false;
        }
    }


    private boolean tickGoingToTarget() {
        Location targetLoc = targetProvider.getTargetLocation();
        
        if (targetLoc == null) {
            if (returnLocation != null) {
                currentPhase = PathPhase.RETURNING;
                return true;
            } else {
                completed = true;
                return false;
            }
        }

        Vector targetPos = targetLoc.toVector();
        Vector direction = targetPos.subtract(currentPosition);
        double distance = direction.length();

        if (distance <= REACH_THRESHOLD) {
            onTargetReached();
            
            if (returnLocation != null) {
                currentPhase = PathPhase.RETURNING;
                return true;
            } else {
                completed = true;
                return false;
            }
        }

        direction.normalize().multiply(Math.min(STEP_SIZE, distance));
        currentPosition.add(direction);
        
        return true;
    }

    private boolean tickReturning() {
        Vector returnPos = returnLocation.toVector();
        Vector direction = returnPos.subtract(currentPosition);
        double distance = direction.length();

        if (distance <= REACH_THRESHOLD) {
            completed = true;
            return false;
        }

        direction.normalize().multiply(Math.min(STEP_SIZE, distance));
        currentPosition.add(direction);
        
        return true;
    }


    protected void onTargetReached() {
    }


    public Location getCurrentLocation() {
        return currentPosition.toLocation(startLocation.getWorld());
    }


    public Vector getCurrentPosition() {
        return currentPosition.clone();
    }

    public PathPhase getCurrentPhase() {
        return currentPhase;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getTickCount() {
        return tickCount;
    }

    public Vector getCurrentDirection() {
        Location target;
        
        if (currentPhase == PathPhase.GOING_TO_TARGET) {
            target = targetProvider.getTargetLocation();
            if (target == null) return new Vector(0, 0, 0);
        } else {
            target = returnLocation;
        }
        
        return target.toVector().subtract(currentPosition).normalize();
    }

    public double getDistanceToCurrentTarget() {
        Location target;
        
        if (currentPhase == PathPhase.GOING_TO_TARGET) {
            target = targetProvider.getTargetLocation();
            if (target == null) return 0;
        } else {
            target = returnLocation;
        }
        
        return currentPosition.distance(target.toVector());
    }

    public void forceComplete() {
        completed = true;
    }

    public enum PathPhase {
        GOING_TO_TARGET,
        RETURNING
    }

    @FunctionalInterface
    public interface TargetProvider {
        Location getTargetLocation();
    }

    public static PathTracker createMeleeTracker(Location petLocation, Location targetLocation, Location returnLocation) {
        return new PathTracker(petLocation, () -> targetLocation, returnLocation);
    }

    public static PathTracker createProjectileTracker(Location petLocation, Location targetLocation) {
        return new PathTracker(petLocation, () -> targetLocation);
    }

    public static PathTracker createDynamicMeleeTracker(Location petLocation, TargetProvider targetProvider, Location returnLocation) {
        return new PathTracker(petLocation, targetProvider, returnLocation);
    }

    public static PathTracker createDynamicProjectileTracker(Location petLocation, TargetProvider targetProvider) {
        return new PathTracker(petLocation, targetProvider);
    }
}