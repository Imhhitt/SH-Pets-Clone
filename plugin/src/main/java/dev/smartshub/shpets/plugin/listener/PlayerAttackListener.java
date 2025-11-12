package dev.smartshub.shpets.plugin.listener;

import dev.smartshub.shpets.plugin.service.pet.PetService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerAttackListener implements Listener {

    private final PetService petService;

    public PlayerAttackListener (PetService petService) {
        this.petService = petService;
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof org.bukkit.entity.Player player)) return;
        petService.performOnAttack(player.getUniqueId(), event.getEntity().getUniqueId());
    }


}
