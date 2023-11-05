package net.frozenorb.foxtrot.partner.impl.old;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.EffectUtil;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class MeloniteBow extends PartnerPackage {

    private static final int USES = 50;
    private static final int BASE = 384 - USES; // total durability - uses

    private final Map<UUID, Instant> bleedingMap = new HashMap<>();

    public MeloniteBow() {
        super("MeloniteBow");
    }

    @Override
    protected Runnable tickTask() {
        return () -> bleedingMap.entrySet().removeIf(entry -> {
            if (entry.getValue() == null) {
                entry.setValue(Instant.now().plusSeconds(CustomTimerCreateCommand.isPartnerPackageHour() ? 10 : 5));
            }
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) {
                return true;
            }

            long seconds = Duration.between(Instant.now(), entry.getValue()).getSeconds();
            String message = seconds > 1 ? "seconds" : "second";
            if (seconds <= 0) {
                player.sendMessage(ChatColor.RED + "The bleeding has stopped!");
                return true;
            } else {
                EffectUtil.bleed(player);
                player.damage(.5);
                player.sendMessage(CC.RED + "You are bleeding out for " +
                        CC.BOLD + seconds + CC.RED + " more " + message + "!");
            }
            return false;
        });
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

            event.getProjectile().setMetadata("melonite_shot", new FixedMetadataValue(Foxtrot.getInstance(), true));
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onArrowHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow))
            return;

        Arrow arrow = (Arrow) event.getDamager();

        if (event.getEntity() instanceof Player && arrow.getShooter() instanceof Player) {
            Player shooter = (Player) arrow.getShooter();
            Player target = (Player) event.getEntity();

            if (!arrow.hasMetadata("melonite_shot"))
                return;

            setGlobalCooldown(shooter);
            setCooldown(shooter);

            EffectUtil.bleed(target);
            bleedingMap.put(target.getUniqueId(), null);


            sendActivationMessages(
                    shooter,
                    new String[]{
                            "Successfully hit " + CC.DARK_GREEN + target.getName() + " " + CC.GREEN + "with " +
                                    getName() + CC.GREEN + "!",
                            "They are bleeding out!"
                    },
                    target,
                    new String[]{
                            shooter.getName() + " has hit you with " + getName() + CC.RED + "!",
                            "Your are now bleeding out!"
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
        return 30;
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.BOW)
                .name(getName())
                .addToLore(
                        "&7Shoot an enemy and they will",
                        "&7bleed out for 5 seconds!"
                )
                .enchant(Enchantment.ARROW_DAMAGE, 3)
                .enchant(Enchantment.ARROW_INFINITE, 1)
                .build();
    }

    @Override
    public String getName() {
        return "§2§lMelonite's Bow";
    }

    @Override
    public int getAmount() {
        return 1;
    }
}
