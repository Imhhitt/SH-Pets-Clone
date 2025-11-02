package dev.smartshub.shpets.api.pet.action.ability.impl;

import dev.smartshub.shpets.api.pet.PetData;
import dev.smartshub.shpets.api.pet.action.ability.PetAbility;
import me.rivaldev.harvesterhoes.api.events.RivalHarvesterHoesAPI;
import me.rivaldev.pickaxes.ecomanager.RivalHarvesterHoesEconomy;
import org.bukkit.entity.Player;

public class RivalToolsBoostAbility extends PetAbility {
    @Override
    protected void executeAbility(Player player, PetData petData) {


    }

    @Override
    public String getName() {
        return "rival-tools-boost";
    }
}
