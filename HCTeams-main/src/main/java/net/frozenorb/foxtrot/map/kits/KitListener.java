package net.frozenorb.foxtrot.map.kits;

import com.google.common.collect.Maps;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.modsuite.ModUtils;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class KitListener implements Listener {

    private static final Map<UUID, Long> REFILL_PLAYER_MAP = Maps.newHashMap();
    private static final long REFILL_DELAY = TimeUnit.MINUTES.toMillis(10L);

    private static final Map<UUID, Long> LAST_CLICKED = Maps.newHashMap();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        REFILL_PLAYER_MAP.remove(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        REFILL_PLAYER_MAP.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Wolf) {
            ((Wolf) event.getRightClicked()).setSitting(false);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getClickedBlock() == null || !(event.getClickedBlock().getState() instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) event.getClickedBlock().getState();

        // Potion refill sign
        if (sign.getLine(0).startsWith(ChatColor.DARK_RED + "- Refill")) {
            openRefillInventory(event.getClickedBlock().getLocation(), player);
            return;
        }

        if (!sign.getLine(0).startsWith(ChatColor.DARK_RED + "- Kit")) {
            return;
        }

        DefaultKit originalKit = Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit(sign.getLine(1));
        if (originalKit != null) {
            Kit kit = Foxtrot.getInstance().getMapHandler().getKitManager().getUserKit(player.getUniqueId(), originalKit);
            if (kit != null) {
                attemptApplyKit(player, kit);
            } else {
                attemptApplyKit(player, originalKit);
            }
        }
    }

    private void openRefillInventory(Location signLocation, Player player) {
        if (DTRBitmask.SAFE_ZONE.appliesAt(signLocation) && !DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation(signLocation))) return; // prevent players from using signs inside spawn outside spawn

        if (!DTRBitmask.SAFE_ZONE.appliesAt(signLocation)) { // put them on a cooldown
            long diff = REFILL_PLAYER_MAP.getOrDefault(player.getUniqueId(), 0L) - System.currentTimeMillis();

            if (diff > 0) {
                player.sendMessage(ChatColor.RED + "You have to wait " + TimeUtils.formatIntoDetailedString((int) (diff / 1000)) + " before using this again.");
                return;
            }

            REFILL_PLAYER_MAP.put(player.getUniqueId(), System.currentTimeMillis() + REFILL_DELAY);
            player.sendMessage(ChatColor.YELLOW + "You have been put on a Refill Sign cooldown for " + ChatColor.RED + TimeUtils.formatIntoDetailedString((int) (REFILL_DELAY / 1000)) + ChatColor.YELLOW + ".");
        }

        Inventory inventory = Bukkit.createInventory(player, 45, "Refill");

        Potion healPotion = new Potion(PotionType.INSTANT_HEAL);
        healPotion.setLevel(2);
        healPotion.setSplash(true);
        ItemStack healItem = new ItemStack(Material.POTION, 1);
        healPotion.apply(healItem);

        Potion speedPotion = new Potion(PotionType.SPEED);
        speedPotion.setLevel(2);
        ItemStack speedItem = new ItemStack(Material.POTION, 1);
        speedPotion.apply(speedItem);

        ItemStack enderPeal = new ItemStack(Material.ENDER_PEARL, 16);
        ItemStack steak = new ItemStack(Material.COOKED_BEEF, 64);
        ItemStack goldSword = new ItemStack(Material.GOLD_SWORD, 1);

        inventory.setItem(0, speedItem);
        inventory.setItem(1, speedItem);
        inventory.setItem(9, speedItem);
        inventory.setItem(10, speedItem);
        inventory.setItem(18, speedItem);
        inventory.setItem(19, speedItem);

        inventory.setItem(27, enderPeal);
        inventory.setItem(28, steak);

        inventory.setItem(2, goldSword);
        inventory.setItem(11, goldSword);
        inventory.setItem(20, goldSword);
        inventory.setItem(29, goldSword);

        while (inventory.firstEmpty() != -1) {
            inventory.addItem(healItem);
        }

        player.openInventory(inventory);
    }

    public static void attemptApplyKit(Player player, Kit kit) {
        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Unknown kit!");
            return;
        }

        if (ModUtils.isModMode(player)) {
            player.sendMessage(ChatColor.RED + "You cannot use this while in mod mode.");
            return;
        }

        if (LAST_CLICKED.containsKey(player.getUniqueId()) && (System.currentTimeMillis() - LAST_CLICKED.get(player.getUniqueId()) < TimeUnit.SECONDS.toMillis(5))) {
            player.sendMessage(ChatColor.RED + "Please wait before using this again.");
            return;
        }

        kit.apply(player);

        LAST_CLICKED.put(player.getUniqueId(), System.currentTimeMillis());
    }

}
