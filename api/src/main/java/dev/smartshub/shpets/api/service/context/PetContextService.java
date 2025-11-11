package dev.smartshub.shpets.api.service.context;

import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PetContextService {

    private static final Map<UUID, Location> petLocations = new ConcurrentHashMap<>();
    private static final Map<UUID, UUID> petTargets = new ConcurrentHashMap<>();

    private PetContextService() {}

    public static void setPetLocation(UUID petId, Location location) {
        if (petId != null && location != null)
            petLocations.put(petId, location.clone());
    }

    public static Location getPetLocation(UUID petId) {
        Location loc = petLocations.get(petId);
        return loc != null ? loc.clone() : null;
    }

    public static void setPetTarget(UUID petId, UUID targetId) {
        if (petId != null && targetId != null)
            petTargets.put(petId, targetId);
    }

    public static UUID getPetTarget(UUID petId) {
        return petTargets.get(petId);
    }

    public static void clearPetTarget(UUID petId) {
        petTargets.remove(petId);
    }

    public static void unregister(UUID petId) {
        petLocations.remove(petId);
        petTargets.remove(petId);
    }

    public static void clear() {
        petLocations.clear();
        petTargets.clear();
    }
}
