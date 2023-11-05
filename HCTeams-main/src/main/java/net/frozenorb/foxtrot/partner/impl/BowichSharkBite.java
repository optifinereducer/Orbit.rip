package net.frozenorb.foxtrot.partner.impl;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;

public final class BowichSharkBite extends PartnerPackage {

    public BowichSharkBite() {
        super("BowichSharkBite");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player entity = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();

            ItemStack item = attacker.getItemInHand();
            boolean partnerItem = isPartnerItem(item);

            if (!partnerItem) {
                return;
            }

            if (DTRBitmask.KOTH.appliesAt(attacker.getLocation()) || DTRBitmask.CITADEL.appliesAt(attacker.getLocation())) {
                attacker.sendMessage(CC.RED + "You cannot use this in koth/citadel!");
                return;
            }

            if (isOnCooldown(attacker)) {
                attacker.sendMessage(getCooldownMessage(attacker));
                return;
            }

            setCooldown(attacker);
            consume(attacker, item);

            AtomicInteger ticks = new AtomicInteger(0);

            new BukkitRunnable() {

                @Override
                public void run() {
                    if (!entity.isOnline() || entity.isDead() || ticks.incrementAndGet() == 10) {
                        cancel();
                    } else {
                        entity.damage(0.5);
                        entity.getWorld().playEffect(entity.getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
                    }
                }
            }.runTaskTimer(Foxtrot.getInstance(), 20, 20);

            sendActivationMessages(attacker,
                    new String[]{
                            "Successfully hit " + CC.DARK_GREEN + entity.getName() + CC.GREEN + " with " + getName() + CC.GREEN + "!"
                    },
                    entity,
                    new String[]{
                            CC.DARK_RED + attacker.getName() + " has hit you " + CC.RED + " with " + getName() + CC.RED + "!"
                    });
        }
    }

    @Override
    public long getCooldownTime() {
        return CustomTimerCreateCommand.isPurgeTimer() ? 60L : 120L;
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.SHEARS)
                .name("&3&l" + getName())
                .addToLore("&7Hit an opponent to deal a bleeding effect for 10 seconds")
                .enchant(Enchantment.DURABILITY, 1)
                .build();
    }

    @Override
    public String getName() {
        return "Bowich's Shark Bite";
    }

    @Override
    public int getAmount() {
        return 4;
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }
}
