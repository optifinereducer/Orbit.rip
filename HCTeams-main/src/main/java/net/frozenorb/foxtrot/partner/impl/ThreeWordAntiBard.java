package net.frozenorb.foxtrot.partner.impl;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Pair;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public final class ThreeWordAntiBard extends PartnerPackage {

    private static final int HITS = 3; // how many hits it takes for the item to activate

    private final Map<Pair<UUID, UUID>, Integer> attackMap = new HashMap<>();
    public static final List<UUID> ANTI_BARD_PLAYERS = new ArrayList<>();

    public ThreeWordAntiBard() {
        super("ThreeWordAntiBard");
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

            if (DTRBitmask.KOTH.appliesAt(attacker.getLocation()) || DTRBitmask.CITADEL.appliesAt(attacker.getLocation())) {
                attacker.sendMessage(CC.RED + "You cannot use this in koth/citadel!");
                return;
            }

            if (isOnCooldown(attacker)) {
                attacker.sendMessage(getCooldownMessage(attacker));
                return;
            }

            if (ANTI_BARD_PLAYERS.contains(entity.getUniqueId())) {
                attacker.sendMessage(ChatColor.RED + "That player is already tagged by " + getName() + "!");
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

            ANTI_BARD_PLAYERS.add(entity.getUniqueId());
            Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> ANTI_BARD_PLAYERS.remove(entity.getUniqueId()), 20 * 20);

            sendActivationMessages(attacker,
                    new String[]{
                            "Successfully hit " + CC.DARK_GREEN + entity.getName() + CC.GREEN + " with " + getName() + CC.GREEN + "!",
                            CC.RED + "They can't receive bard effects for 20 seconds."
                    },
                    entity,
                    new String[]{
                            CC.DARK_RED + attacker.getName() + " has hit you " + CC.RED + " with " + getName() + CC.RED + "!",
                            CC.RED + "You can't receive bard effects for 20 seconds."
                    });
        }
    }

    @EventHandler // cleanup attack map
    private void onQuit(PlayerQuitEvent event) {
        attackMap.entrySet().removeIf(entry -> entry.getKey().first.equals(event.getPlayer().getUniqueId()));
    }

    @Override
    public long getCooldownTime() {
        return CustomTimerCreateCommand.isPurgeTimer() ? 60L : 120L;
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.GOLD_INGOT)
                .name("&6&l" + getName())
                .addToLore("&7Hit an opponent to prevent them from",
                        "&7receiving bard effects for 1 minute")
                .enchant(Enchantment.DURABILITY, 1)
                .build();
    }

    @Override
    public String getName() {
        return "3word's Anti-Bard";
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
