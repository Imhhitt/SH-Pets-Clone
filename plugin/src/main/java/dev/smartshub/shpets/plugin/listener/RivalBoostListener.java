package dev.smartshub.shpets.plugin.listener;

import dev.smartshub.shpets.api.pet.action.ability.impl.RivalBoostAbility;
import dev.smartshub.shpets.api.service.boost.RivalBoostService;
import me.rivaldev.fishingrod.rivalfishingrods.api.RodMoneyReceiveEvent;
import me.rivaldev.harvesterhoes.api.events.HoeEssenceReceivePreEnchantEvent;
import me.rivaldev.harvesterhoes.api.events.HoeMoneyReceiveEnchant;
import me.rivaldev.mobsword.rivalmobswords.api.SwordMoneyReceiveEvent;
import me.rivaldev.pickaxes.api.events.PickaxeEssenceReceiveEnchantEvent;
import me.rivaldev.pickaxes.api.events.PickaxeEssenceReceivePreEnchantEvent;
import me.rivaldev.pickaxes.api.events.PickaxeMoneyReceiveEnchant;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class RivalBoostListener implements Listener {

    private final RivalBoostService boostService = RivalBoostService.getInstance();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onSwordMoney(SwordMoneyReceiveEvent event) {
        var boosted = boostService.applyBoost(event.getPlayer().getUniqueId(),
                RivalBoostAbility.RivalBoostType.SWORD,
                event.getMoney());

        event.setMoney(boosted);
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onRodMoney(RodMoneyReceiveEvent event) {
        var boosted = boostService.applyBoost(event.getPlayer().getUniqueId(),
                RivalBoostAbility.RivalBoostType.SWORD,
                event.getMoney());

        event.setMoney(boosted);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPickaxeEssence(PickaxeEssenceReceiveEnchantEvent event) {
        var boosted = boostService.applyBoost(event.getPlayer().getUniqueId(),
                RivalBoostAbility.RivalBoostType.PICKAXE,
                event.getEssence());

        event.setEssence(boosted);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHoeEssence(HoeEssenceReceivePreEnchantEvent event) {
        var boosted = boostService.applyBoost(event.getPlayer().getUniqueId(),
                RivalBoostAbility.RivalBoostType.HOE,
                event.getEssence());

        event.setEssence(boosted);
    }

}
