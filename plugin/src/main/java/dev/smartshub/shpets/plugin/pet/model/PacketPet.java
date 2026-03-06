package dev.smartshub.shpets.plugin.pet.model;

import dev.smartshub.shpets.api.PetsAPI;
import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.apparence.PetAppearance;
import dev.smartshub.shpets.api.pet.template.EquipmentData;
import dev.smartshub.shpets.plugin.message.MessageParser;
import dev.smartshub.shpets.plugin.pet.goal.*;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.Collections;
import java.util.UUID;

/**
 * Low-level packet-based pet implementation.
 * Handles NMS entity creation and packet sending.
 */
public class PacketPet {

    private final PetData petData;
    private UUID uniqueId;
    private final MessageParser parser;
    private Entity nmsEntity;
    private boolean spawned = false;
    private float yaw = 0f;
    private float pitch = 0f;
    private Team glowTeam;

    public PacketPet(PetData petData, MessageParser parser) {
        this.petData = petData;
        this.parser = parser;
    }

    /**
     * Create the NMS entity based on the pet data
     */
    private Entity createNMSEntity(Location location) {
        ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();
        EntityType<?> entityType = getEntityType();

        if (entityType == null) {
            throw new IllegalStateException("Invalid entity type: " + petData.getTemplate().entityType());
        }

        Entity entity = entityType.create(level, EntitySpawnReason.COMMAND);
        if (entity == null) {
            throw new RuntimeException("Failed to create entity instance for type: " + entityType);
        }

        entity.setPos(location.getX(), location.getY(), location.getZ());
        entity.setYRot(location.getYaw());
        entity.setXRot(location.getPitch());

        entity.setInvulnerable(true);
        entity.setNoGravity(true);
        entity.isCollidable(false);

        this.uniqueId = entity.getUUID();

        applyAppearance(entity);
        applyEntitySpecificProperties(entity);
        applyBehaviorProperties(entity);

        return entity;
    }


    /**
     * Applies entity-specific properties based on config
     */
    private void applyEntitySpecificProperties(Entity entity) {
        // Baby entities
        if (petData.getTemplate().entityData().baby()) {
            if (entity instanceof AgeableMob ageable) {
                ageable.setBaby(true);
            }
        }

        // Armor stands configuration
        if (entity instanceof ArmorStand armorStand) {
            armorStand.setNoBasePlate(true);
            boolean showArms = petData.getTemplate().appearance().equipment().showArms();
            armorStand.setShowArms(showArms);
            armorStand.setSmall(petData.getTemplate().entityData().baby());

            // FIX: Always hide the body when showing arms, and make it invisible
            if (showArms) {
                armorStand.setInvisible(true);
                hideArmorStandBody(armorStand);
            }
        }
    }

    private void hideArmorStandBody(ArmorStand armorStand) {
        try {
            var entityData = armorStand.getEntityData();
            var dataAccessor = ArmorStand.DATA_CLIENT_FLAGS;
            byte currentFlags = entityData.get(dataAccessor);

            // Bit flags del ArmorStand:
            // 0x01 = isSmall
            // 0x02 = hasArms
            // 0x04 = noBasePlate
            // 0x08 = setMarker (hace que sea más pequeño y sin hitbox)
            // 0x10 = hideBody (oculta el cuerpo/tronco)

            // FIX: Aplicar tanto MARKER como HIDE_BODY para ocultar completamente el cuerpo
            byte newFlags = (byte) (currentFlags | 0x08 | 0x10);
            entityData.set(dataAccessor, newFlags);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Applies appearance settings (glowing, equipment, etc.)
     */
    private void applyAppearance(Entity entity) {
        PetAppearance appearance = petData.getTemplate().appearance();

        // Apply equipment if entity supports it
        if (entity instanceof LivingEntity living) {
            applyEquipment(living, appearance.equipment());
        }

        if (appearance.showNameTag()) {
            entity.setCustomName(parser.toNMS(appearance.displayName()));
            entity.setCustomNameVisible(true);
        }
    }

    private void applyBehaviorProperties(Entity entity) {
        var behavior = petData.getTemplate().behavior();

        if(!(entity instanceof Mob mob)) return;

        mob.goalSelector.getAvailableGoals().clear();
        mob.targetSelector.getAvailableGoals().clear();
        mob.setNoAi(false);

        switch (behavior.followData().followMode()) {
            case WALK -> mob.goalSelector.addGoal(10, new NormalFollowGoal(mob, petData.getOwner(), behavior));
            case FLOATING -> mob.goalSelector.addGoal(10, new FloatingFollowGoal(mob, petData.getOwner(), behavior));
            case TELEPORT_ONLY -> mob.goalSelector.addGoal(10, new TeleportFollowGoal(mob, petData.getOwner(), behavior));
        }

        mob.goalSelector.addGoal(5, new LookAtOwnerGoal(mob, petData.getOwner()));
        mob.goalSelector.addGoal(6, new PetIdleGoal(mob));
    }

    /**
     * Applies equipment to living entities
     */
    private void applyEquipment(LivingEntity entity, EquipmentData equipment) {
        if (equipment == null) return;

        // Helmet
        if (equipment.helmet() != null) {
            ItemStack helmet = createItemStack(equipment.helmet());
            if (helmet != null) {
                entity.setItemSlot(EquipmentSlot.HEAD, helmet);
            }
        }

        // Chestplate
        if (equipment.chestplate() != null) {
            ItemStack chestplate = createItemStack(equipment.chestplate());
            if (chestplate != null) {
                entity.setItemSlot(EquipmentSlot.CHEST, chestplate);
            }
        }

        // Leggings
        if (equipment.leggings() != null) {
            ItemStack leggings = createItemStack(equipment.leggings());
            if (leggings != null) {
                entity.setItemSlot(EquipmentSlot.LEGS, leggings);
            }
        }

        // Boots
        if (equipment.boots() != null) {
            ItemStack boots = createItemStack(equipment.boots());
            if (boots != null) {
                entity.setItemSlot(EquipmentSlot.FEET, boots);
            }
        }

        // Main hand
        if (equipment.hand() != null) {
            ItemStack hand = createItemStack(equipment.hand());
            if (hand != null) {
                entity.setItemSlot(EquipmentSlot.MAINHAND, hand);
            }
        }

        // Off hand
        if (equipment.offHand() != null) {
            ItemStack offHand = createItemStack(equipment.offHand());
            if (offHand != null) {
                entity.setItemSlot(EquipmentSlot.OFFHAND, offHand);
            }
        }

        if(!equipment.headOverride()) return;
        if(!equipment.headValue().isEmpty()) {
            var skull = PetsAPI.getInstance().skullService().getCustomSkull(equipment.headValue());
            entity.setItemSlot(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(skull));
        }

    }

    public void applyPlayerEquipment() {
        var equipment = petData.getTemplate().appearance().equipment();

        if(equipment.playerEquipment()) return;
        if(!(nmsEntity instanceof LivingEntity living)) return;

        var player = petData.getOwner();

        living.setItemSlot(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(player.getInventory().getItemInMainHand()));
        living.setItemSlot(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(player.getInventory().getItemInOffHand()));
        living.setItemSlot(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(player.getInventory().getChestplate()));
        living.setItemSlot(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(player.getInventory().getLeggings()));
        living.setItemSlot(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(player.getInventory().getBoots()));

        if(equipment.headOverride()) {
            living.setItemSlot(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(player.getInventory().getHelmet()));
        }

        updateEquipment();

    }

    /**
     * Creates an NMS ItemStack from a material string
     */
    private ItemStack createItemStack(String materialName) {
        try {
            Material material = Material.valueOf(materialName.toUpperCase());
            org.bukkit.inventory.ItemStack bukkitStack = new org.bukkit.inventory.ItemStack(material);
            return CraftItemStack.asNMSCopy(bukkitStack);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void spawn(Location location) {
        if (spawned) return;

        nmsEntity = createNMSEntity(location);
        spawned = true;
        location.getWorld().getPlayers().forEach(this::sendSpawnPackets);

        PetAppearance appearance = petData.getTemplate().appearance();

        PetsAPI.getInstance().nmsEntityRegistry().register(nmsEntity);

        // Apply glow
        if (appearance.glow().enabled()) {
            var color = NamedTextColor.NAMES.value(appearance.glow().color().toLowerCase());
            PetsAPI.getInstance().glowService().setGlowing(nmsEntity, color);
        }
    }

    public void spawnFor(Location location, Player player) {
        if (!spawned) {
            nmsEntity = createNMSEntity(location);
            spawned = true;
        }
        sendSpawnPackets(player);
    }

    private void sendSpawnPackets(Player player) {
        if (nmsEntity == null) return;

        var connection = ((CraftPlayer) player).getHandle().connection;
        ServerLevel level = nmsEntity.level().getMinecraftWorld();

        ServerEntity serverEntity = new ServerEntity(
                level,
                nmsEntity,
                0,
                false,
                null,
                Collections.emptySet()
        );

        ClientboundAddEntityPacket spawnPacket = new ClientboundAddEntityPacket(nmsEntity, serverEntity);
        ClientboundSetEntityDataPacket dataPacket = new ClientboundSetEntityDataPacket(
                nmsEntity.getId(),
                nmsEntity.getEntityData().packAll()
        );

        connection.send(spawnPacket);
        connection.send(dataPacket);

        // Send equipment packets if entity has equipment
        if (nmsEntity instanceof LivingEntity living) {
            sendEquipmentPackets(player, living);
        }

    }

    /**
     * Sends equipment packets to player
     */
    private void sendEquipmentPackets(Player player, LivingEntity entity) {
        var connection = ((CraftPlayer) player).getHandle().connection;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack item = entity.getItemBySlot(slot);
            if (!item.isEmpty()) {
                ClientboundSetEquipmentPacket packet = new ClientboundSetEquipmentPacket(
                        entity.getId(),
                        java.util.List.of(com.mojang.datafixers.util.Pair.of(slot, item))
                );
                connection.send(packet);
            }
        }
    }

    public void teleport(Location location) {
        if (!spawned || nmsEntity == null) return;

        nmsEntity.setPos(location.getX(), location.getY(), location.getZ());
        nmsEntity.setYRot(location.getYaw());
        nmsEntity.setXRot(location.getPitch());

        this.yaw = location.getYaw();
        this.pitch = location.getPitch();

        sendMovementPackets(location);
    }

    public void moveTo(Location location, boolean smooth) {
        if (!spawned || nmsEntity == null) return;

        if (smooth) {
            updatePosition(location);
        } else {
            teleport(location);
        }
    }

    private void updatePosition(Location location) {
        nmsEntity.setPos(location.getX(), location.getY(), location.getZ());
        nmsEntity.setYRot(location.getYaw());
        nmsEntity.setXRot(location.getPitch());

        this.yaw = location.getYaw();
        this.pitch = location.getPitch();

        sendMovementPackets(location);
    }

    private void sendMovementPackets(Location location) {
        ClientboundEntityPositionSyncPacket positionPacket = new ClientboundEntityPositionSyncPacket(
                nmsEntity.getId(),
                new PositionMoveRotation(
                        nmsEntity.position(),
                        nmsEntity.getDeltaMovement(),
                        nmsEntity.getYRot(),
                        nmsEntity.getXRot()
                ),
                nmsEntity.onGround()
        );

        ClientboundSetEntityDataPacket dataPacket = new ClientboundSetEntityDataPacket(
                nmsEntity.getId(),
                nmsEntity.getEntityData().packAll()
        );

        location.getWorld().getPlayers().forEach(player -> {
            var connection = ((CraftPlayer) player).getHandle().connection;
            connection.send(positionPacket);
            connection.send(dataPacket);
        });
    }

    public void lookAt(Location target) {
        if (!spawned || nmsEntity == null) return;

        Location current = new Location(
                target.getWorld(),
                nmsEntity.getX(),
                nmsEntity.getY(),
                nmsEntity.getZ()
        );

        double dx = target.getX() - current.getX();
        double dy = target.getY() - current.getY();
        double dz = target.getZ() - current.getZ();

        double distance = Math.sqrt(dx * dx + dz * dz);
        this.yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        this.pitch = (float) Math.toDegrees(Math.atan2(-dy, distance));

        nmsEntity.setYRot(yaw);
        nmsEntity.setXRot(pitch);

        ClientboundRotateHeadPacket headRotation = new ClientboundRotateHeadPacket(
                nmsEntity,
                (byte) ((yaw * 256.0F) / 360.0F)
        );

        current.getWorld().getPlayers().forEach(player -> {
            ((CraftPlayer) player).getHandle().connection.send(headRotation);
        });
    }

    public void despawn() {
        if (!spawned || nmsEntity == null) return;

        nmsEntity.level().getWorld().getPlayers().forEach(this::despawnFor);

        // Clean up glow team
        if (glowTeam != null) {
            glowTeam.unregister();
            glowTeam = null;
        }

        PetsAPI.getInstance().nmsEntityRegistry().register(nmsEntity);
        spawned = false;
        nmsEntity.discard();
        nmsEntity = null;
    }

    public void despawnFor(Player player) {
        if (nmsEntity == null) return;

        var connection = ((CraftPlayer) player).getHandle().connection;
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(nmsEntity.getId());
        connection.send(packet);
    }

    public void updateMetadata() {
        if (!spawned || nmsEntity == null) return;

        ClientboundSetEntityDataPacket dataPacket = new ClientboundSetEntityDataPacket(
                nmsEntity.getId(),
                nmsEntity.getEntityData().packAll()
        );

        nmsEntity.level().getWorld().getPlayers().forEach(player -> {
            ((CraftPlayer) player).getHandle().connection.send(dataPacket);
        });
    }

    /**
     * Updates equipment dynamically
     */
    public void updateEquipment() {
        if (!spawned || nmsEntity == null) return;
        if (!(nmsEntity instanceof LivingEntity livingEntity)) return;

        nmsEntity.level().getWorld().getPlayers().forEach(player -> {
            sendEquipmentPackets(player, livingEntity);
        });
    }

    private EntityType<?> getEntityType() {
        try {
            if (petData.getTemplate().entityType() != null) {
                return EntityType.byString(
                        petData.getTemplate().entityType().toLowerCase()
                ).orElse(null);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    // Getters
    public Entity getNmsEntity() {
        return nmsEntity;
    }

    public int getEntityId() {
        return nmsEntity != null ? nmsEntity.getId() : -1;
    }

    public boolean isSpawned() {
        return spawned && nmsEntity != null;
    }

    public PetData getPetData() {
        return petData;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public Location getLocation() {
        if (nmsEntity == null) return null;

        return new Location(
                nmsEntity.level().getWorld(),
                nmsEntity.getX(),
                nmsEntity.getY(),
                nmsEntity.getZ(),
                yaw,
                pitch
        );
    }
}