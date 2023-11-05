package net.frozenorb.foxtrot.partner.impl.old;

import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public final class PalmitaAngel extends PartnerPackage {

    public PalmitaAngel() {
        super("PalmitaAngel");
    }

    @Override
    public long getCooldownTime() {
        return CustomTimerCreateCommand.isPurgeTimer() ? 60L : TimeUnit.MINUTES.toSeconds(2);
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.FEATHER)
                .name("&d&l" + getName())
                .addToLore(
                        "&7Fully heal yourself"
                ).build();
    }

    @Override
    public String getName() {
        return "Palmita's Angelic Feather";
    }

    @Override
    public int getAmount() {
        return 4;
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        setGlobalCooldown(player);
        setCooldown(player);
        sendActivationMessages(player,
                new String[]{
                        "You have activated " + getName() + CC.GREEN + "!",
                        "You have been healed."
                }, null, null);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(player.getFoodLevel());
        return true;
    }
}
