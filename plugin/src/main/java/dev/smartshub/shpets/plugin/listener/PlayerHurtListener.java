package dev.smartshub.shpets.plugin.listener;

import dev.smartshub.shpets.plugin.service.pet.PetService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerHurtListener implements Listener {

    private final PetService petService;

    public PlayerHurtListener (PetService petService) {
        this.petService = petService;
    }

    @EventHandler
    public void onPlayerHurt(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;
        petService.performOnHurt(player.getUniqueId());
    }

}
