package dev.smartshub.shpets.plugin.service.pet;

import dev.smartshub.shpets.api.config.WrapperConfigurationSection;
import dev.smartshub.shpets.api.pet.action.ability.Ability;
import dev.smartshub.shpets.api.pet.action.ability.conditional.CompiledCondition;
import dev.smartshub.shpets.api.pet.action.ability.conditional.ConditionalAbility;
import dev.smartshub.shpets.api.pet.action.ability.registry.AbilityRegistry;
import dev.smartshub.shpets.api.pet.action.trigger.PetAction;
import dev.smartshub.shpets.api.pet.action.trigger.PetActions;
import dev.smartshub.shpets.api.pet.apparence.PetAppearance;
import dev.smartshub.shpets.api.pet.behavior.FollowData;
import dev.smartshub.shpets.api.pet.behavior.FollowMode;
import dev.smartshub.shpets.api.pet.behavior.PetBehavior;
import dev.smartshub.shpets.api.pet.template.EntityData;
import dev.smartshub.shpets.api.pet.template.EquipmentData;
import dev.smartshub.shpets.api.pet.template.GlowData;
import dev.smartshub.shpets.api.pet.template.PetTemplate;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Service for loading pet templates from individual YAML files in pets/ folder
 */
public class PetTemplateLoaderService {

    private final JavaPlugin plugin;
    private final PetService petService;
    private final File petsFolder;

    public PetTemplateLoaderService(JavaPlugin plugin, PetService petService) {
        this.plugin = plugin;
        this.petService = petService;
        this.petsFolder = new File(plugin.getDataFolder(), "pets");
    }

    /**
     * Loads all pet templates from the pets/ folder
     */
    public void loadAllTemplates() {
        if (!petsFolder.exists()) {
            petsFolder.mkdirs();
            plugin.getLogger().info("Created pets/ folder");
        }

        petService.getTemplateRegistry().clear();

        File[] files = petsFolder.listFiles((dir, name) -> name.endsWith(".yml"));

        if (files == null || files.length == 0) {
            plugin.getLogger().warning("No pet templates found in pets/ folder!");
            return;
        }

        int loaded = 0;
        int failed = 0;

        for (File file : files) {
            try {
                PetTemplate template = loadTemplateFromFile(file);
                if (template != null) {
                    petService.registerTemplate(template);
                    loaded++;
                    plugin.getLogger().info("  ✓ Loaded: " + template.id());
                }
            } catch (Exception e) {
                failed++;
                plugin.getLogger().log(Level.SEVERE, "  ✗ Failed: " + file.getName(), e);
            }
        }

        plugin.getLogger().info("Loaded " + loaded + " pet template(s)" +
                (failed > 0 ? " (" + failed + " failed)" : ""));
    }

    /**
     * Reloads all pet templates
     */
    public void reloadTemplates() {
        plugin.getLogger().info("Reloading pet templates...");
        loadAllTemplates();
    }

    /**
     * Loads a single template from a file
     */
    private PetTemplate loadTemplateFromFile(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Pet ID from filename (without .yml)
        String petId = file.getName().replace(".yml", "");

        // Name
        String displayName = config.getString("name.display", petId);
        boolean showName = config.getBoolean("name.show", true);

        // Permission
        String permission = config.getString("permission", "pets." + petId);

        // Entity data (contains type, baby, show-arms)
        EntityData entityData = loadEntityData(config);

        // Load sections
        PetBehavior behavior = loadBehavior(config);
        PetAppearance appearance = loadAppearance(config, displayName, showName);
        PetActions actions = loadActions(config);

        // Pass EntityData object and extract type for the template
        return new PetTemplate(
                petId,
                displayName,
                permission,
                entityData.type(),      // Extract type string for PetTemplate
                entityData,             // Pass full EntityData for additional properties
                behavior,
                appearance,
                actions
        );
    }

    // ==================== Entity Loading ====================

    private EntityData loadEntityData(YamlConfiguration config) {
        ConfigurationSection entitySection = config.getConfigurationSection("entity");

        String type = "ZOMBIE";
        boolean baby = false;

        if (entitySection != null) {
            type = entitySection.getString("type", "ZOMBIE").toUpperCase();
            baby = entitySection.getBoolean("baby", false);
        }

        return new EntityData(type, baby);
    }

    // ==================== Appearance Loading ====================

    private PetAppearance loadAppearance(YamlConfiguration config, String displayName, boolean showName) {
        // Glow
        GlowData glow = loadGlow(config);

        // Equipment
        EquipmentData equipment = loadEquipment(config);

        return new PetAppearance(displayName, showName, glow, equipment);
    }

    private GlowData loadGlow(YamlConfiguration config) {
        ConfigurationSection glowSection = config.getConfigurationSection("glow");

        boolean enabled = false;
        String color = "WHITE";

        if (glowSection != null) {
            enabled = glowSection.getBoolean("enabled", false);
            color = glowSection.getString("color", "WHITE").toLowerCase();
        }

        return new GlowData(enabled, color);
    }

    private EquipmentData loadEquipment(YamlConfiguration config) {
        ConfigurationSection equipSection = config.getConfigurationSection("equipment");

        if (equipSection == null) {
            return new EquipmentData(null, null, null, null, null,
                    null, null, false, false, false);
        }

        return new EquipmentData(
                equipSection.getString("helmet"),
                equipSection.getString("chestplate"),
                equipSection.getString("leggings"),
                equipSection.getString("boots"),
                equipSection.getString("hand"),
                equipSection.getString("off-hand"),
                equipSection.getString("head-value"),
                equipSection.getBoolean("show-arms", false),
                equipSection.getBoolean("player-equipment", false),
                equipSection.getBoolean("head-override", false)
        );
    }

    // ==================== Behavior Loading ====================

    private PetBehavior loadBehavior(YamlConfiguration config) {
        // Movement
        ConfigurationSection movementSection = config.getConfigurationSection("movement");

        FollowMode followMode = FollowMode.WALK;
        double speed = 1.0;
        boolean flexY = false;
        float flexYAmplitude = 0f;
        float flexYIncrement = 0f;

        if (movementSection != null) {
            String modeStr = movementSection.getString("mode", "WALK").toUpperCase();
            try {
                followMode = FollowMode.valueOf(modeStr);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid movement mode: " + modeStr + ", using WALK");
            }
            speed = movementSection.getDouble("speed", 1.0);
            flexY = movementSection.getBoolean("flex-y", false);
            flexYAmplitude = (float) movementSection.getDouble("flex-y-amplitude", 0f);
            flexYIncrement = (float) movementSection.getDouble("flex-y-increment", 0.02f);

        }

        // Distance/Offsets
        ConfigurationSection distanceSection = config.getConfigurationSection("distance");

        double xOffset = 1.0;
        double yOffset = 0.0;
        double zOffset = 1.0;
        double teleportDistance = 20.0;

        if (distanceSection != null) {
            xOffset = distanceSection.getDouble("x-offset", 1.0);
            yOffset = distanceSection.getDouble("y-offset", 0.0);
            zOffset = distanceSection.getDouble("z-offset", 1.0);
            teleportDistance = distanceSection.getDouble("teleport", 20.0);
        }

        FollowData followData = new FollowData(followMode, teleportDistance,
                xOffset, yOffset, zOffset);

        return new PetBehavior(followData, speed, flexY, flexYAmplitude, flexYIncrement, true, true);
    }

    // ==================== Actions Loading ====================

    private PetActions loadActions(YamlConfiguration config) {
        PetAction onSpawn = loadPetActionFromSection(config.getConfigurationSection("on-spawn"));
        PetAction onDespawn = loadPetActionFromSection(config.getConfigurationSection("on-despawn"));
        PetAction onHurt = loadPetActionFromSection(config.getConfigurationSection("on-hurt"));
        PetAction onInteract = loadPetActionFromSection(config.getConfigurationSection("on-interact"));

        // Periodic
        ConfigurationSection periodicSection = config.getConfigurationSection("periodic");
        int periodicDelay = 20;

        if (periodicSection != null && periodicSection.contains("delay")) {
            periodicDelay = periodicSection.getInt("delay", 20);
        }

        PetAction periodic = loadPetActionFromSection(periodicSection);

        return new PetActions(onSpawn, onDespawn, periodic, periodicDelay, onInteract, onHurt);
    }

    /**
     * Loads a PetAction from a configuration section.
     * Each key in the section represents either:
     * - A known ability (messages, title, sound, etc.) - can be a section or a list
     * - A conditional ability (contains "condition" field)
     */
    private PetAction loadPetActionFromSection(ConfigurationSection section) {
        if (section == null) {
            return new PetAction(List.of());
        }

        List<Ability> allAbilities = new ArrayList<>();

        for (String key : section.getKeys(false)) {
            // First check if it's a known ability (handles both lists and sections)
            if (AbilityRegistry.isKnownAbility(key)) {
                Ability ability = createAbility(section, key);
                if (ability != null) {
                    allAbilities.add(ability);
                }
                continue;
            }

            // Check if it's a conditional ability
            Object value = section.get(key);
            if (value instanceof ConfigurationSection abilitySection) {
                if (abilitySection.contains("condition")) {
                    ConditionalAbility conditionalAbility = loadConditionalAbility(key, abilitySection);
                    if (conditionalAbility != null) {
                        allAbilities.add(conditionalAbility);
                    }
                }
            }
        }

        return new PetAction(allAbilities);
    }

    /**
     * Creates an ability from either a ConfigurationSection or a List value
     */
    private Ability createAbility(ConfigurationSection parent, String key) {
        Object value = parent.get(key);

        ConfigurationSection abilitySection;

        // If it's a list (like messages or console-commands), wrap it
        if (value instanceof List) {
            abilitySection = new WrapperConfigurationSection(parent, key);
        }
        // If it's already a section (like title or sound), use it directly
        else if (value instanceof ConfigurationSection) {
            abilitySection = (ConfigurationSection) value;
        }
        // Unknown type
        else {
            return null;
        }

        return AbilityRegistry.create(key, abilitySection);
    }

    /**
     * Loads a conditional ability with its nested sub-abilities
     */
    private ConditionalAbility loadConditionalAbility(String name, ConfigurationSection section) {
        String conditionStr = section.getString("condition");
        if (conditionStr == null || conditionStr.isEmpty()) {
            return null;
        }

        CompiledCondition condition = CompiledCondition.compile(conditionStr);
        List<Ability> subAbilities = new ArrayList<>();

        // Iterate through all keys in the conditional section
        for (String key : section.getKeys(false)) {
            // Skip metadata fields
            if (key.equals("condition") || key.equals("probability") || key.equals("delay")) {
                continue;
            }

            // Try to create as a known ability (handles both lists and sections)
            if (AbilityRegistry.isKnownAbility(key)) {
                Ability ability = createAbility(section, key);
                if (ability != null) {
                    subAbilities.add(ability);
                }
                continue;
            }

            // Check for nested conditional abilities
            Object value = section.get(key);
            if (value instanceof ConfigurationSection subSection) {
                if (subSection.contains("condition")) {
                    ConditionalAbility nested = loadConditionalAbility(key, subSection);
                    if (nested != null) {
                        subAbilities.add(nested);
                    }
                }
            }
        }

        ConditionalAbility conditionalAbility = new ConditionalAbility(name, condition, subAbilities);

        // Set probability and delay if present
        if (section.contains("probability")) {
            conditionalAbility.setProbability(section.getDouble("probability"));
        }
        if (section.contains("delay")) {
            conditionalAbility.setDelay(section.getLong("delay"));
        }

        return conditionalAbility;
    }
}