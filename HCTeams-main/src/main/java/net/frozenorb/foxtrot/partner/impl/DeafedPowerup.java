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

public final class DeafedPowerup extends PartnerPackage {

    public DeafedPowerup() {
        super("DeafedPowerup");
    }

    @Override
    public long getCooldownTime() {
        return CustomTimerCreateCommand.isPurgeTimer() ? 120L : TimeUnit.MINUTES.toSeconds(5);
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.WATCH)
                .name("&d&l" + getName())
                .addToLore("&7Receive the following effects for 10 seconds:",
                        "&7- &bSpeed 2",
                        "&7- &cStrength 1",
                        "&7- &6Resistance 1",
                        "&7- &dRegeneration 1")
                .enchant(Enchantment.DURABILITY, 1)
                .build();
    }

    @Override
    public String getName() {
        return "Deafed's Powerup";
    }

    @Override
    public int getAmount() {
        return 1;
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        setGlobalCooldown(player);
        setCooldown(player);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0));

        sendActivationMessages(player,
                new String[]{
                        "You have activated " + getName() + CC.GREEN + "!"
                }, null, null);
        return true;
    }
}
