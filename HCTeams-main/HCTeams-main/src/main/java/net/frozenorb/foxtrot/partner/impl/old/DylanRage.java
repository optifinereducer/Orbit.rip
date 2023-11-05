package net.frozenorb.foxtrot.partner.impl.old;

import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

public final class DylanRage extends PartnerPackage {

    public DylanRage() {
        super("DylanRage");
    }

    @Override
    public long getCooldownTime() {
        return CustomTimerCreateCommand.isPurgeTimer() ? 60L : TimeUnit.MINUTES.toSeconds(2);
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.INK_SACK).data((short) 1)
                .name("&c&l" + getName())
                .addToLore(
                        "&7Gives you speed III strength II",
                        "&7 if you have less than 4 hearts"
                ).build();
    }

    @Override
    public String getName() {
        return "Dylan's Rage";
    }

    @Override
    public int getAmount() {
        return 2;
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getHealth() > 8) {
            player.sendMessage(ChatColor.RED + "You must have less than 4 hearts to use this item.");
            return false;
        }

        setGlobalCooldown(player);
        setCooldown(player);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 120, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1));

        sendActivationMessages(player,
                new String[]{
                        "You have activated " + getName() + CC.GREEN + "!"
                }, null, null);
        return true;
    }
}
