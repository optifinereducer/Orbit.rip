package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PurgeListener implements Listener {

	public PurgeListener() {
		Bukkit.getServer().getScheduler().runTaskTimer(Foxtrot.getInstance(), () -> {
			if (CustomTimerCreateCommand.isPurgeTimer()) {
				for (World world : Bukkit.getWorlds()) {
					world.setTime(16_000);
				}
			}
		}, 100L, 20 * 15);
	}

	private final Map<UUID, Instant> interactCooldown = new HashMap<>();

	@EventHandler(priority = EventPriority.MONITOR)
	private void onBlockBreak(BlockBreakEvent event) {
		if (CustomTimerCreateCommand.isPurgeTimer()) {
			if (event.isCancelled()) {
				interactCooldown.put(event.getPlayer().getUniqueId(), Instant.now().plusMillis(800));
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (CustomTimerCreateCommand.isPurgeTimer()) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Block block = event.getClickedBlock();
				if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
					Instant instant = interactCooldown.get(event.getPlayer().getUniqueId());
					if (instant != null && instant.isAfter(Instant.now())) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.RED + "Block glitching isn't allowed!");
					} else {
						interactCooldown.remove(event.getPlayer().getUniqueId());
					}
				}
			}
		}
	}

}
