package net.frozenorb.foxtrot.partner.impl.old;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

public final class OldVersionPumpkinHead extends PartnerPackage {

    public OldVersionPumpkinHead() {
        super("PumpkinHead");
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

            if (isOnCooldown(attacker)) {
                attacker.sendMessage(getCooldownMessage(attacker));
                return;
            }

            entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));

            ItemStack head = entity.getInventory().getHelmet();

            if (head == null || head.getType() == Material.DIAMOND_HELMET) {
                entity.getInventory().setHelmet(new ItemStack(Material.PUMPKIN));

                Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                    if (entity.isOnline()) {
                        entity.getInventory().setHelmet(head);
                    }
                }, 100);
            }

            setCooldown(attacker);
            consume(attacker, item);

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
        return CustomTimerCreateCommand.isPurgeTimer() ? 60L : TimeUnit.MINUTES.toSeconds(2);
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.PUMPKIN_SEEDS)
                .name("&6&l" + getName())
                .addToLore(
                        "&7Turns your opponent's head into a pumpkin",
                        "&7and gives them slowness and blindness for 5 seconds")
                .enchant(Enchantment.DURABILITY, 1)
                .build();
    }

    @Override
    public String getName() {
        return "OldVersion's Toxic Pumpkin";
    }

    @Override
    public int getAmount() {
        return 2;
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }
}
