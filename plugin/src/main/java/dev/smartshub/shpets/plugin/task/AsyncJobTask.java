package dev.smartshub.shpets.plugin.task;

import dev.smartshub.shpets.plugin.service.pet.PetService;
import org.bukkit.scheduler.BukkitRunnable;

public class AsyncJobTask extends BukkitRunnable {

    private final PetService petService;

    public AsyncJobTask(PetService petService) {
        this.petService = petService;
    }

    @Override
    public void run() {
        petService.tickAllPets();
    }
}
