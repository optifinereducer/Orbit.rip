package net.frozenorb.foxtrot.partner.impl;

import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

public final class EimohFiftyFifty extends PartnerPackage {

    public EimohFiftyFifty() {
        super("EimohFiftyFifty");
    }

    @Override
    public long getCooldownTime() {
        return CustomTimerCreateCommand.isPurgeTimer() ? 60L : TimeUnit.MINUTES.toSeconds(2);
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.SPIDER_EYE)
                .name("&d&l" + getName())
                .addToLore(
                        "&750% chance of getting Stength II",
                        "&750% chance of getting Weakness II"
                )
                .enchant(Enchantment.DURABILITY, 1)
                .build();
    }

    @Override
    public String getName() {
        return "Eimoh's Fifty Fifty";
    }

    @Override
    public int getAmount() {
        return 6;
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        setGlobalCooldown(player);
        setCooldown(player);
        sendActivationMessages(player,
                new String[]{
                        "You have activated " + getName() + CC.GREEN + "!",
                }, null, null);

        if (qLib.RANDOM.nextBoolean()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 1));
            player.sendMessage(CC.GREEN + CC.BOLD + "You have received Strength II");
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1));
            player.sendMessage(CC.RED + CC.BOLD + "You have received Weakness II");
        }
        return true;
    }
}
