package net.frozenorb.foxtrot.partner.impl;

import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

public final class GgusGift extends PartnerPackage {

    public GgusGift() {
        super("GgusGift");
    }

    @Override
    public long getCooldownTime() {
        return CustomTimerCreateCommand.isPurgeTimer() ? 60L : TimeUnit.MINUTES.toSeconds(2);
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.NETHER_STAR)
                .name("&c&l" + getName())
                .addToLore("&7Right click to fully heal yourself",
                        "&7and receive 5 seconds of Strength II")
                .enchant(Enchantment.DURABILITY, 1)
                .build();
    }

    @Override
    public String getName() {
        return "Ggus' Gift";
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
                        "You have activated " + getName() + CC.GREEN + "!"
                }, null, null);
        player.setHealth(player.getMaxHealth());
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 1));
        return true;
    }
}
