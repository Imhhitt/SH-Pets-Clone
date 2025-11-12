package dev.smartshub.shpets.plugin.service.pet;

import dev.smartshub.shpets.api.event.dispatcher.PetEventDispatcher;
import dev.smartshub.shpets.api.pet.Pet;
import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.trigger.TriggerType;
import dev.smartshub.shpets.api.pet.template.PetTemplate;
import dev.smartshub.shpets.api.registry.PetInstanceRegistry;
import dev.smartshub.shpets.api.registry.PetTemplateRegistry;
import dev.smartshub.shpets.plugin.message.MessageParser;
import dev.smartshub.shpets.plugin.pet.factory.PetFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Main service for pet management operations
 */
public class PetService {

    private final PetTemplateRegistry templateRegistry;
    private final PetInstanceRegistry instanceRegistry;
    private final PetFactory petFactory;
    private final MessageParser parser;
    private final PetEventDispatcher eventDispatcher = new PetEventDispatcher();

    public PetService(
            PetTemplateRegistry templateRegistry,
            PetInstanceRegistry instanceRegistry,
            PetFactory petFactory,
            MessageParser parser
    ) {
        this.templateRegistry = templateRegistry;
        this.instanceRegistry = instanceRegistry;
        this.petFactory = petFactory;
        this.parser = parser;
    }

    // ==================== Template Operations ====================

    /**
     * Registers a pet template
     */
    public void registerTemplate(@NotNull PetTemplate template) {
        templateRegistry.register(template);
    }

    /**
     * Unregisters a pet template
     */
    public void unregisterTemplate(@NotNull PetTemplate template) {
        templateRegistry.unregister(template);
    }

    /**
     * Gets a pet template by ID
     */
    @Nullable
    public PetTemplate getTemplate(@NotNull String templateId) {
        return templateRegistry.get(templateId);
    }

    /**
     * Gets all registered templates
     */
    @NotNull
    public Collection<PetTemplate> getAllTemplates() {
        return templateRegistry.getAll();
    }

    /**
     * Gets all template IDs
     */
    @NotNull
    public List<String> getAllTemplateIds() {
        return getAllTemplates().stream()
                .map(PetTemplate::id)
                .collect(Collectors.toList());
    }

    public void performOnHurt(UUID owner) {
        List<Pet> pets = getPetsByOwner(owner);
        for (Pet pet : pets) {
            pet.getData().getTemplate().actions().get(TriggerType.ON_HURT).execute(pet.getData(), pet.getData().getOwner());
        }
    }

    /**
     * Checks if a template exists
     */
    public boolean templateExists(@NotNull String templateId) {
        return templateRegistry.exists(templateId);
    }

    // ==================== Pet Instance Operations ====================

    /**
     * Creates a new pet from a template for a player
     */
    @Nullable
    public Pet createPet(@NotNull Player owner, @NotNull String templateId) {
        PetTemplate template = getTemplate(templateId);
        if (template == null) {
            return null;
        }

        PetData petData = new PetData(owner, template);

        return petFactory.createPet(petData, parser);
    }

    /**
     * Creates a pet from a template object
     */
    @NotNull
    public Pet createPetFromTemplate(@NotNull PetTemplate template, @NotNull Player owner) {
        PetData petData = new PetData(owner, template);
        Pet pet = petFactory.createPet(petData, parser);

        instanceRegistry.register(pet);
        return pet;
    }

    /**
     * Spawns a pet for a player. If they already have one spawned, despawns it first.
     */
    public void spawnPet(@NotNull Player player, @NotNull String templateId) {
        // Despawn any existing spawned pet
        Pet existingSpawned = getSpawnedPetByOwner(player.getUniqueId());
        if (existingSpawned != null) {
            existingSpawned.despawn();
            instanceRegistry.removeFromEntityIndex(existingSpawned);
        }

        // Get or create the pet
        Pet pet = getPetByOwnerAndTemplate(player.getUniqueId(), templateId);
        if (pet == null) {
            pet = createPet(player, templateId);
        }

        if (pet == null) {
            return;
        }

        // Spawn the pet
        var event = eventDispatcher.firePetSpawnEvent(pet);
        if(event.isCancelled()) return;

        pet.spawn();
        instanceRegistry.register(pet);
        instanceRegistry.updateEntityIndex(pet);
    }

    /**
     * Despawns a specific pet
     */
    public void despawnPet(@NotNull Pet pet) {
        var event = eventDispatcher.firePetDespawnEvent(pet);

        if (pet.isSpawned() || !event.isCancelled()) {
            pet.despawn();
            instanceRegistry.removeFromEntityIndex(pet);
        }
    }

    /**
     * Despawns all pets for a player
     */
    public void despawnPlayerPets(@NotNull UUID ownerUUID) {
        List<Pet> pets = instanceRegistry.getPetsByOwner(ownerUUID);
        pets.forEach(this::despawnPet);
    }

    /**
     * Removes a pet completely (despawn + unregister)
     */
    public void removePet(@NotNull Pet pet) {
        despawnPet(pet);
        instanceRegistry.unregister(pet);
    }

    /**
     * Removes all pets for a player
     */
    public void removePlayerPets(@NotNull UUID ownerUUID) {
        List<Pet> pets = instanceRegistry.getPetsByOwner(ownerUUID);
        pets.forEach(this::removePet);
    }

    public void performInteraction(@NotNull UUID petId, @NotNull UUID playerUUID) {
        Pet pet = getPet(petId);
        if(pet == null) return;

        Player player = Bukkit.getPlayer(playerUUID);
        if(player == null) return;
        var event = eventDispatcher.firePetInteractEvent(pet, player);
        if(event.isCancelled()) return;

        pet.getData().getTemplate().actions().get(TriggerType.ON_INTERACT).execute(pet.getData(), player);
    }

    // ==================== Query Operations ====================

    /**
     * Gets a pet by its UUID
     */
    @Nullable
    public Pet getPet(@NotNull UUID petUUID) {
        return instanceRegistry.getByUUID(petUUID);
    }

    /**
     * Gets a pet by entity UUID (for packet interactions)
     */
    @Nullable
    public Pet getPetByEntityUUID(@NotNull UUID entityUUID) {
        return instanceRegistry.getByEntityUUID(entityUUID);
    }

    /**
     * Gets all pets owned by a player
     */
    @NotNull
    public List<Pet> getPetsByOwner(@NotNull UUID ownerUUID) {
        return instanceRegistry.getPetsByOwner(ownerUUID);
    }

    /**
     * Gets the currently spawned pet for a player
     */
    @Nullable
    public Pet getSpawnedPetByOwner(@NotNull UUID ownerUUID) {
        return instanceRegistry.getSpawnedPetByOwner(ownerUUID);
    }

    /**
     * Gets a specific pet by owner and template
     */
    @Nullable
    public Pet getPetByOwnerAndTemplate(@NotNull UUID ownerUUID, @NotNull String templateId) {
        return instanceRegistry.getPetsByOwner(ownerUUID).stream()
                .filter(pet -> pet.getData().getTemplate().id().equalsIgnoreCase(templateId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets all active (spawned) pets
     */
    @NotNull
    public List<Pet> getAllSpawnedPets() {
        return instanceRegistry.getAllSpawned();
    }

    /**
     * Gets all registered pet instances
     */
    @NotNull
    public Collection<Pet> getAllPets() {
        return instanceRegistry.getAll();
    }

    /**
     * Checks if a player has a spawned pet
     */
    public boolean hasSpawnedPet(@NotNull UUID ownerUUID) {
        return getSpawnedPetByOwner(ownerUUID) != null;
    }

    // ==================== Tick Operations ====================

    /**
     * Ticks all spawned pets (movement, AI, etc.)
     */
    public void tickAllPets() {
        getAllSpawnedPets().forEach(Pet::tick);
    }

    // ==================== Lifecycle ====================

    /**
     * Shuts down the service, despawning all pets and clearing registries
     */
    public void shutdown() {
        // Despawn all pets
        getAllPets().forEach(this::despawnPet);

        // Clear registries
        instanceRegistry.clear();
        templateRegistry.clear();
    }

    // ==================== Registry Access ====================

    public PetTemplateRegistry getTemplateRegistry() {
        return templateRegistry;
    }

    public PetInstanceRegistry getInstanceRegistry() {
        return instanceRegistry;
    }
}