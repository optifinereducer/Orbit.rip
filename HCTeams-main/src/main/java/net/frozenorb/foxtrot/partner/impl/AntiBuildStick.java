package net.frozenorb.foxtrot.partner.impl;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Pair;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class AntiBuildStick extends PartnerPackage {

    private static final int HITS = 3; // how many hits it takes for the item to activate
    private static final int ANTI_TIME = 15; // how long the victim is anti-d for in seconds
    private static final int ANTI_BOOST_TIME = 20; // how long the victim is anti-d for in seconds

    // block types you cannot interact with while anti-d
    private static final List<Material> BLACKLISTED_BLOCKS = Arrays.asList(
            Material.FENCE_GATE,
            Material.TRAP_DOOR,
            Material.TRAPPED_CHEST,
            Material.CHEST,
            Material.LEVER,
            Material.STONE_BUTTON,
            Material.WOOD_BUTTON,
            Material.HOPPER
    );
    // attacker uuid -> victim id , total hits
    private final Map<Pair<UUID, UUID>, Integer> attackMap = new HashMap<>();
    // anti-d players
    private final Map<UUID, Instant> antiMap = new HashMap<>();

    public AntiBuildStick() {
        super("AntiStick");
        Bukkit.getScheduler().runTaskTimer(Foxtrot.getInstance(), () -> {
            if (antiMap.isEmpty())
                return;

            Iterator<UUID> iterator = antiMap.keySet().iterator();
            UUID target;
            while (iterator.hasNext() && (target = iterator.next()) != null) {
                refreshAnti(target);
            }
        }, 35L, 35L);
    }

    // returns true if they are currently anti-d
    private boolean refreshAnti(UUID uuid) {
        Instant instant = antiMap.get(uuid);

        if (instant == null)
            return false;

        Instant currentTime = Instant.now();
        boolean expired = instant.isBefore(currentTime);

        if (expired) {
            antiMap.remove(uuid);
        } else {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                long seconds = Duration.between(currentTime, instant).getSeconds();
                if (seconds > 0) {
                    String time = seconds > 1 ? "seconds" : "second";
                    player.sendMessage(ChatColor.RED + "You cannot interact with blocks for " +
                            ChatColor.BOLD + seconds + ChatColor.RED + " more " + time + ".");
                } else {
                    player.sendMessage(ChatColor.GREEN + "You can now interact with blocks!");
                }
            }
        }

        return !expired;
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

            if (antiMap.containsKey(entity.getUniqueId())) {
                attacker.sendMessage(ChatColor.RED + "That player is already anti-d!");
                return;
            }

            int hits = attackMap.getOrDefault(key, 0);

            if (++hits < HITS) {
                attackMap.put(key, hits);
                return;
            }

            attackMap.remove(key);

            antiMap.put(entity.getUniqueId(), Instant.now().plusSeconds(getAntiTime()));
            setCooldown(attacker);
            consume(attacker, item);

            sendActivationMessages(attacker,
                    new String[]{
                            "Successfully hit " + CC.DARK_GREEN + entity.getName() + CC.GREEN + " with an " + getName() + CC.GREEN + "!",
                            "That player can no longer interact with blocks!"
                    },
                    entity,
                    new String[]{
                            CC.DARK_RED + attacker.getName() + " has hit you " + CC.RED + " with an " + getName() + CC.RED + "!",
                            "You may not interact with blocks for " + getAntiTime() + " seconds!"
                    });
        }
    }

    // anti-d listeners
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBuild(BlockPlaceEvent event) {
        if (refreshAnti(event.getPlayer().getUniqueId())) {
            event.setBuild(false);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBuild(BlockMultiPlaceEvent event) {
        if (refreshAnti(event.getPlayer().getUniqueId())) {
            event.setBuild(false);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBreak(BlockBreakEvent event) {
        if (refreshAnti(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlaceBucket(PlayerBucketEmptyEvent event) {
        if (refreshAnti(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onInteract(PlayerInteractEvent event) {
        if (refreshAnti(event.getPlayer().getUniqueId())) {
            if (event.getClickedBlock() != null && BLACKLISTED_BLOCKS.contains(event.getClickedBlock().getType()))
                event.setCancelled(true);
        }
    }

    @EventHandler // cleanup attack map
    private void onQuit(PlayerQuitEvent event) {
        attackMap.entrySet().removeIf(entry -> entry.getKey().first.equals(event.getPlayer().getUniqueId()));
    }

    @Override
    public long getCooldownTime() {
        return CustomTimerCreateCommand.isPurgeTimer() ? 60L : TimeUnit.MINUTES.toSeconds(2);
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.STICK)
                .name("&d&l" + getName())
                .addToLore(
                        "&7Hit a player 3 times with this item",
                        "&7to prevent all block interactions!"
                ).build();
    }

    @Override
    public String getName() {
        return "Anti-Build Stick";
    }

    @Override
    public int getAmount() {
        return 6;
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }

    @Override
    public boolean isExclusive() {
        return false;
    }

    public int getAntiTime() {
        return CustomTimerCreateCommand.isPartnerPackageHour() ? ANTI_BOOST_TIME : ANTI_TIME;
    }
}
