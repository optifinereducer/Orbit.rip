package net.frozenorb.foxtrot.partner.impl.old;

import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

public final class SlexiRing extends PartnerPackage {

    public SlexiRing() {
        super("SlexisRing");
    }

    @EventHandler
    private void onConsume(PlayerItemConsumeEvent event) {
        if (isPartnerItem(event.getItem())) {
            event.getPlayer().getItemInHand().setType(Material.AIR);
            event.setCancelled(true);
        }
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.removePotionEffect(PotionEffectType.SPEED);
        }

        int seconds = CustomTimerCreateCommand.isPartnerPackageHour() ? 15 : 7;
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * seconds, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * seconds, 1));
        sendActivationMessages(
                player,
                new String[]{
                        "You have activated " + getName() + CC.GREEN + "!",
                        "You have been given Speed III and Resistance II for" + seconds + " seconds!"
                },
                null,
                null
        );
        setCooldown(player);
        setGlobalCooldown(player);
        return true;
    }

    @Override
    public long getCooldownTime() {
        return CustomTimerCreateCommand.isPurgeTimer() ? TimeUnit.MINUTES.toSeconds(2) : TimeUnit.MINUTES.toSeconds(3);
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.DOUBLE_PLANT)
                .name(getName())
                .addToLore(
                        "&7Right-click to receive Speed III",
                        "&7and Resistance II for 7 seconds!."
                ).build();
    }

    @Override
    public String getName() {
        return "§5§lSlexi's Ring";
    }

    @Override
    public int getAmount() {
        return 4;
    }
}
