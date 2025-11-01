package dev.smartshub.shpets.plugin.listener;

import dev.smartshub.shpets.api.PetsAPI;
import dev.smartshub.shpets.api.event.service.EntityGlowEvent;
import dev.smartshub.shpets.api.service.glow.GlowService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PetGlowListener implements Listener {

    private final GlowService glowService;

    public PetGlowListener(GlowService glowService) {
        this.glowService = glowService;
    }

    @EventHandler
    public void onPetGlow(EntityGlowEvent event) {
        System.out.println("PetGlowListener received EntityGlowEvent: target=" + event.getTarget() +
                ", color=" + event.getColor() + ", duration=" + event.getDuration());
        var entity = PetsAPI.getInstance().nmsEntityRegistry().get(event.getTarget());
        glowService.setGlowing(entity, event.getColor(), event.getDuration());
    }

}
