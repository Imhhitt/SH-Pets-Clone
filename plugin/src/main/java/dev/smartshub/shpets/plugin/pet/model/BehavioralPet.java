package dev.smartshub.shpets.plugin.pet.model;

import dev.smartshub.shpets.api.PetsAPI;
import dev.smartshub.shpets.api.event.dispatcher.PetEventDispatcher;
import dev.smartshub.shpets.api.pet.Pet;
import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.PetState;
import dev.smartshub.shpets.api.pet.action.trigger.TriggerType;
import dev.smartshub.shpets.api.pet.behavior.MovementState;
import dev.smartshub.shpets.plugin.message.MessageParser;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * High-level pet implementation that handles behavior logic.
 * Wraps PacketPet for rendering and delegates behavioral logic.
 */
public class BehavioralPet implements Pet {

    private final PacketPet packetPet;
    private final PetData data;
    private final PetBehaviorHandler behaviorHandler;
    private MovementState movementState = MovementState.FOLLOWING;
    private final PetEventDispatcher eventDispatcher = new PetEventDispatcher();

    private long lastPeriodicExecution = 0;

    public BehavioralPet(PetData data, MessageParser parser) {
        this.data = data;
        this.packetPet = new PacketPet(data, parser);
        this.behaviorHandler = new PetBehaviorHandler(data);
    }

    @Override
    public void spawn() {
        if (data.getState() == PetState.SPAWNED) return;

        Player owner = data.getOwner();
        if (!owner.isOnline()) return;

        Location spawnLocation = behaviorHandler.calculateTargetLocation(owner);
        packetPet.spawn(spawnLocation);
        data.setState(PetState.SPAWNED);
        data.setUniqueId(packetPet.getUniqueId());

        // Trigger ON_SPAWN actions
        data.getTemplate().actions().get(TriggerType.ON_SPAWN).execute(data, owner);
    }

    @Override
    public void despawn() {
        if (data.getState() == PetState.STORED) return;

        Player owner = data.getOwner();
        if (owner != null) {
            // Trigger ON_DESPAWN actions
            data.getTemplate().actions().get(TriggerType.ON_DESPAWN).execute(data, owner);
        }

        packetPet.despawn();
        data.setState(PetState.STORED);
    }

    @Override
    public void tick() {
        if (data.getState() != PetState.SPAWNED) return;

        Player owner = data.getOwner();
        if (!owner.isOnline()) {
            despawn();
            return;
        }

        if (movementState == MovementState.FOLLOWING) {
            behaviorHandler.tick(packetPet, owner);
        }

        PetsAPI.getInstance().glowService().refreshGlowing(packetPet.getNmsEntity());
    }

    @Override
    public void updateTo(Player player) {
        packetPet.spawnFor(getLocation(), player);
    }

    @Override
    public void performPeriodic() {
        if (data.getState() != PetState.SPAWNED) return;

        Player owner = data.getOwner();
        if (owner == null) return;

        long now = System.currentTimeMillis();
        int periodicDelay = data.getTemplate().actions().periodicDelay();

        if (periodicDelay > 0 && (now - lastPeriodicExecution) >= periodicDelay) {

            var event = eventDispatcher.firePetPeriodicEvent(this);
            if(event.isCancelled()) return;

            data.getTemplate().actions().get(TriggerType.PERIODIC).execute(data, owner);
            setLastExecution(now);
        }
    }

    @Override
    public boolean isSpawned() {
        return data.getState() == PetState.SPAWNED && packetPet.isSpawned();
    }

    @Override
    public void teleport(Player player) {
        if (data.getState() != PetState.SPAWNED) return;

        Location targetLocation = behaviorHandler.calculateTargetLocation(player);
        packetPet.teleport(targetLocation);
    }

    @Override
    public void setLastExecution(long timestamp) {
        this.lastPeriodicExecution = timestamp;
    }

    @Override
    public PetData getData() {
        return data;
    }

    @Override
    public Location getLocation() {
        return packetPet.getLocation();
    }

    @Override
    public UUID getUniqueId() {
        return packetPet.getUniqueId();
    }

    @Override
    public PetState getState() {
        return data.getState();
    }

    public PacketPet getPacketPet() {
        return packetPet;
    }

    public MovementState getMovementState() {
        return movementState;
    }

    public void setMovementState(MovementState state) {
        this.movementState = state;
    }

}