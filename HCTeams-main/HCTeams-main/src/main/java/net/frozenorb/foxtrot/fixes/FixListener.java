package net.frozenorb.foxtrot.fixes;

import me.badbones69.crazyenchantments.multisupport.armorequip.ArmorEquipEvent;
import me.badbones69.crazyenchantments.multisupport.armorequip.ArmorType;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.foxtrot.util.modsuite.ModUtils;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.PotionEffectAddEvent;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * a listener class that represents bootleg fixes to things out of our control
 */
public final class FixListener implements Listener {

    private static final List<String> SPAWN_COMMANDS = Arrays.asList(
            "pv", "vault", "chest"
    );
    private static final List<String> DISALLOWED_COMMANDS = Arrays.asList(
            "pv", "vault", "chest", "tinker", "tinkerer", "rename"
    );

    public static void refreshCustomEnchants(Player player) {
        Plugin crazyEnchantments = Bukkit.getPluginManager().getPlugin("CrazyEnchantments");
        if (crazyEnchantments == null || !crazyEnchantments.isEnabled())
            return;
        PlayerInventory inventory = player.getInventory();
        for (ItemStack armor : inventory.getArmorContents()) {
            if (armor == null || armor.getType() == Material.AIR)
                continue;
            ArmorType type = ArmorType.matchType(armor);
            if (type == null)
                continue;
            ArmorEquipEvent equipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.SHIFT_CLICK, type, new ItemStack(Material.AIR), armor);
            Bukkit.getPluginManager().callEvent(equipEvent);
        }
    }

    private final List<DTRBitmask> bitmasks = Arrays.asList(
            DTRBitmask.KOTH,
            DTRBitmask.SAFE_ZONE,
            DTRBitmask.ROAD,
            DTRBitmask.CITADEL,
            DTRBitmask.CONQUEST
    );

    @EventHandler
    private void onEnchant(EnchantItemEvent event) {
        Map<Enchantment, Integer> enchantsToAdd = event.getEnchantsToAdd();
        enchantsToAdd.entrySet().removeIf(entry -> entry.getKey().equals(Enchantment.FIRE_ASPECT) || entry.getKey().equals(Enchantment.KNOCKBACK));
    }

    // prevent players from opening chests if they aren't looking directly at them
    // not 100% accurate so only used during purge
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onChestInteract(PlayerInteractEvent event) {
        if (CustomTimerCreateCommand.isPurgeTimer()) {
            Player player = event.getPlayer();
            if (event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof Chest) {
                Block targetBlock = player.getTargetBlock(null, 5);
                // BOOTLEG ASS FIX TEMP
                if (targetBlock != null && targetBlock.getState() instanceof Sign) {
                    return;
                }
                if (targetBlock != null && !targetBlock.getLocation().equals(event.getClickedBlock().getLocation())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    private void onLeavesDecay(LeavesDecayEvent event) {
        for (DTRBitmask bitmask : bitmasks) {
            if (bitmask.appliesAt(event.getBlock().getLocation())) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler // crazy enchantments bootleg fix for equipping armor
    private void onArmorEquip(PlayerInteractEvent event) {
        if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (InventoryUtils.isArmor(event.getItem())) {
                refreshCustomEnchants(event.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPearl(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.ENDER_PEARL) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location location = event.getPlayer().getLocation();
                if (DTRBitmask.CITADEL.appliesAt(location)) {
                    if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(CC.RED + "You cannot use this in citadel.");
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPearl(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof EnderPearl && event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            Location location = player.getLocation();
            if (DTRBitmask.CITADEL.appliesAt(location)) {
                if (player.getGameMode() != GameMode.CREATIVE) {
                    event.setCancelled(true);
                    player.sendMessage(CC.RED + "You cannot use this in citadel.");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCombatCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase();
        String cmd = command.split(" ")[0].replace("/", "");

        if (SPAWN_COMMANDS.contains(cmd) && !DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can only do this at spawn.");
            return;
        }

        if (SpawnTagHandler.isTagged(event.getPlayer()) && DISALLOWED_COMMANDS.contains(cmd)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot use this command in combat.");
            return;
        }

        if (Foxtrot.getInstance().getMapHandler().getGameHandler() != null
                && Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()
                && Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame().isPlayingOrSpectating(player.getUniqueId())
                && DISALLOWED_COMMANDS.contains(cmd)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot use this command in a game.");
        }
    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();

        boolean slashKit = message.startsWith("/kit");
        if (slashKit || message.startsWith("/gkit") || message.startsWith("/gkits") || message.startsWith("/gkitz")) {
            Location location = event.getPlayer().getLocation();
            if (DTRBitmask.CITADEL.appliesAt(location) || DTRBitmask.KOTH.appliesAt(location)) {
                if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(CC.RED + "You cannot use kits in citadel/koth.");
                }
            }
            if (!event.getPlayer().isOp() && message.split(" ").length > 2) {
                event.setCancelled(true);
            }

            if (slashKit && !(ModUtils.isModMode(event.getPlayer()) && message.startsWith("/kits"))) {
                event.setMessage(message.replace("/kit", "/gkit"));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPotionEffectAdd(PotionEffectAddEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        if (event.getEffect().getType() != PotionEffectType.INVISIBILITY)
            return;

        FrozenNametagHandler.reloadOthersFor(((Player) event.getEntity()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPotionEffectExpire(PotionEffectExpireEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        if (event.getEffect().getType() != PotionEffectType.INVISIBILITY)
            return;

        FrozenNametagHandler.reloadOthersFor(((Player) event.getEntity()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPotionEffectRemove(PotionEffectExpireEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        refreshCustomEnchants(((Player) event.getEntity()));
    }

    // called in BardClass instead to restore
//	@EventHandler // refresh effects whenever expire (i.e. a bard give them bard effects)
//	private void onEffectExpire(PotionEffectExpireEvent event) {
//		if (event.getEntity() instanceof Player) {
//			refreshCustomEnchants(((Player) event.getEntity()));
//		}
//	}

    @EventHandler // call equip armor for custom enchants that don't register it for some reason
    private void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> refreshCustomEnchants(event.getPlayer()), 20L);
    }

}
