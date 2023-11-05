package net.frozenorb.foxtrot.partner.impl.old;

import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Pair;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class SnekerrScramble extends PartnerPackage {

    private static final int HITS = 3; // how many hits it takes for the item to activate

    private final Map<Pair<UUID, UUID>, Integer> attackMap = new HashMap<>();

    public SnekerrScramble() {
        super("SnekerrScramble");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player entity = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();

            Pair<UUID, UUID> key = new Pair<>(attacker.getUniqueId(), entity.getUniqueId());

            ItemStack item = attacker.getItemInHand();
            boolean partnerItem = isPartnerItem(item);

            if (attackMap.containsKey(key) && !partnerItem) {
                attackMap.remove(key);
                return;
            }

            if (!partnerItem) {
                return;
            }

            if (isOnCooldown(attacker)) {
                attacker.sendMessage(getCooldownMessage(attacker));
                return;
            }

            int hits = attackMap.getOrDefault(key, 0);

            if (++hits < HITS) {
                attackMap.put(key, hits);
                return;
            }

            attackMap.remove(key);

            setCooldown(attacker);
            consume(attacker, item);

            List<ItemStack> hi = new ArrayList<>();

            for (int i = 0; i < 9; i++) {
                hi.add(entity.getInventory().getItem(i));
            }

            Collections.shuffle(hi);

            for (int i = 0; i < 9; i++) {
                entity.getInventory().setItem(i, hi.get(i));
            }

            sendActivationMessages(
                    attacker,
                    new String[]{
                            "Successfully hit " + CC.DARK_GREEN + entity.getName() + CC.GREEN + " with " + getName() + CC.GREEN + "!",
                    },
                    entity,
                    new String[]{
                            attacker.getName() + " has hit you with " + getName() + CC.RED + "!",
                            "Your inventory has been scrambled."
                    }
            );

        }
    }

    @EventHandler // cleanup attack map
    private void onQuit(PlayerQuitEvent event) {
        attackMap.entrySet().removeIf(entry -> entry.getKey().first.equals(event.getPlayer().getUniqueId()));
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }

    @Override
    public long getCooldownTime() {
        return CustomTimerCreateCommand.isPartnerPackageHour() ? 60L : TimeUnit.MINUTES.toSeconds(2);
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.RAW_FISH)
                .name(getName())
                .data((byte) 2)
                .addToLore(
                        "&7Hit a player 3 times to",
                        "&7scramble their hotbar!"
                )
                .enchant(Enchantment.DURABILITY, 1)
                .build();
    }

    @Override
    public String getName() {
        return "§9§lSnekerr's Scrambler";
    }

    @Override
    public int getAmount() {
        return 2;
    }
}
