package dev.smartshub.shpets.api.registry;

import dev.smartshub.shpets.api.pet.Pet;
import dev.smartshub.shpets.api.pet.PetState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Registry for active pet instances in the server
 */
public class PetInstanceRegistry implements Registry<Pet, String> {

    // Primary storage: pet UUID -> Pet
    private final Map<UUID, Pet> petsByUUID = new ConcurrentHashMap<>();

    // Index: owner UUID -> List of their pets
    private final Map<UUID, Set<UUID>> petsByOwner = new ConcurrentHashMap<>();

    // Index: entity UUID -> pet UUID (for packet interactions)
    private final Map<UUID, UUID> petsByEntityId = new ConcurrentHashMap<>();

    @Override
    public void register(@NotNull Pet pet) {
        UUID petUUID = pet.getUniqueId();
        UUID ownerUUID = pet.getData().getOwner().getUniqueId();

        // Add to primary storage
        petsByUUID.put(petUUID, pet);

        // Index by owner
        petsByOwner.computeIfAbsent(ownerUUID, k -> ConcurrentHashMap.newKeySet())
                .add(petUUID);

        // Index by entity ID if spawned
        if (pet.isSpawned() && pet.getData().getUniqueId() != null) {
            petsByEntityId.put(pet.getData().getUniqueId(), petUUID);
        }
    }

    @Override
    public void unregister(@NotNull Pet pet) {
        UUID petUUID = pet.getUniqueId();
        UUID ownerUUID = pet.getData().getOwner().getUniqueId();

        // Remove from primary storage
        petsByUUID.remove(petUUID);

        // Remove from owner index
        Set<UUID> ownerPets = petsByOwner.get(ownerUUID);
        if (ownerPets != null) {
            ownerPets.remove(petUUID);
            if (ownerPets.isEmpty()) {
                petsByOwner.remove(ownerUUID);
            }
        }

        // Remove from entity index
        if (pet.getData().getUniqueId() != null) {
            petsByEntityId.remove(pet.getData().getUniqueId());
        }
    }

    @Override
    @Nullable
    public Pet get(@NotNull String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return petsByUUID.get(uuid);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Nullable
    public Pet getByUUID(@NotNull UUID petUUID) {
        return petsByUUID.get(petUUID);
    }

    @Nullable
    public Pet getByEntityUUID(@NotNull UUID entityUUID) {
        UUID petUUID = petsByEntityId.get(entityUUID);
        return petUUID != null ? petsByUUID.get(petUUID) : null;
    }

    /**
     * Gets all pets owned by a player
     */
    @NotNull
    public List<Pet> getPetsByOwner(@NotNull UUID ownerUUID) {
        Set<UUID> petUUIDs = petsByOwner.get(ownerUUID);
        if (petUUIDs == null || petUUIDs.isEmpty()) {
            return Collections.emptyList();
        }

        return petUUIDs.stream()
                .map(petsByUUID::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Gets the currently spawned pet for a player (if any)
     */
    @Nullable
    public Pet getSpawnedPetByOwner(@NotNull UUID ownerUUID) {
        return getPetsByOwner(ownerUUID).stream()
                .filter(pet -> pet.getState() == PetState.SPAWNED)
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets all spawned pets in the server
     */
    @NotNull
    public List<Pet> getAllSpawned() {
        return petsByUUID.values().stream()
                .filter(pet -> pet.getState() == PetState.SPAWNED)
                .collect(Collectors.toList());
    }

    /**
     * Updates entity UUID index when a pet spawns
     */
    public void updateEntityIndex(@NotNull Pet pet) {
        if (pet.isSpawned() && pet.getData().getUniqueId() != null) {
            petsByEntityId.put(pet.getData().getUniqueId(), pet.getUniqueId());
        }
    }

    /**
     * Removes entity UUID from index when a pet despawns
     */
    public void removeFromEntityIndex(@NotNull Pet pet) {
        if (pet.getData().getUniqueId() != null) {
            petsByEntityId.remove(pet.getData().getUniqueId());
        }
    }

    @Override
    @NotNull
    public Collection<Pet> getAll() {
        return petsByUUID.values();
    }

    @Override
    public boolean exists(@NotNull String id) {
        return get(id) != null;
    }

    @Override
    public void clear() {
        petsByUUID.clear();
        petsByOwner.clear();
        petsByEntityId.clear();
    }

    public int size() {
        return petsByUUID.size();
    }
}