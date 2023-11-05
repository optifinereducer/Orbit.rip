package net.frozenorb.foxtrot.partner.impl.old;

import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class KeylessSwitchStick extends PartnerPackage {

    public KeylessSwitchStick() {
        super("KeylessSwitchStick");
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

            Location loc = entity.getLocation();
            loc.setYaw(loc.getYaw() + 180);
            entity.teleport(loc);

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
        return CustomTimerCreateCommand.isPurgeTimer() ? 15L : 30L;
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.STICK)
                .name("&d&l" + getName())
                .addToLore("&7Rotates your opponent by 180 degrees")
                .enchant(Enchantment.DURABILITY, 1)
                .build();
    }

    @Override
    public String getName() {
        return "Keyless' Switch Stick";
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
