package net.frozenorb.foxtrot.partner.impl;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class BowingBow extends PartnerPackage {

    private static final int USES = 50;
    private static final int BASE = 384 - USES; // total durability - uses

    public BowingBow() {
        super("BowingBow");
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player shooter = (Player) event.getEntity();
            ItemStack item = event.getBow();
            if (!isPartnerItem(item)) {
                return;
            }

            short durability = item.getDurability();
            if (durability <= BASE) {
                item.setDurability((short) BASE);
            } else {
                item.setDurability((short) (durability + 1));
            }

            if (isOnCooldown(shooter)) {
                shooter.sendMessage(getCooldownMessage(shooter));
                return;
            }

            event.getProjectile().setMetadata("bowing_shot", new FixedMetadataValue(Foxtrot.getInstance(), true));
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onArrowHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow)) return;

        Arrow arrow = (Arrow) event.getDamager();

        if (event.getEntity() instanceof Player && arrow.getShooter() instanceof Player) {
            Player shooter = (Player) arrow.getShooter();
            Player target = (Player) event.getEntity();

            if (!arrow.hasMetadata("bowing_shot")) return;

            setGlobalCooldown(shooter);
            setCooldown(shooter);

            target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0));

            sendActivationMessages(
                    shooter,
                    new String[]{
                            "Successfully hit " + CC.DARK_GREEN + target.getName() + " " + CC.GREEN + "with " +
                                    getName() + CC.GREEN + "!"
                    },
                    target,
                    new String[]{
                            shooter.getName() + " has hit you with " + getName() + CC.RED + "!"
                    }
            );
        }
    }

    @EventHandler
    private void onPrepareAnvil(CraftItemEvent event) {
        if (isPartnerItem(event.getRecipe().getResult())) {
            event.setCancelled(true);
        }
    }

    @Override
    public long getCooldownTime() {
        return 60;
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.BOW)
                .name(getName())
                .addToLore(
                        "&7Shoot an enemy and they will be",
                        "&7poisoned for 5 seconds!"
                )
                .enchant(Enchantment.ARROW_DAMAGE, 3)
                .enchant(Enchantment.ARROW_INFINITE, 1)
                .build();
    }

    @Override
    public String getName() {
        return "§2§lBowing's Bow";
    }

    @Override
    public int getAmount() {
        return 1;
    }
}
