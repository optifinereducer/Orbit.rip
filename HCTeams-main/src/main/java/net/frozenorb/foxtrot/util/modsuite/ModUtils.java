package net.frozenorb.foxtrot.util.modsuite;

import com.lunarclient.bukkitapi.LunarClientAPI;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import net.frozenorb.qlib.visibility.FrozenVisibilityHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ModUtils {

	public static final String MOD_MODE_META = "modmode";
	public static final String INVISIBILITY_META = "invisible";

	public static final Set<UUID> hideStaff;
	private static final Map<String, ItemStack[]> playerInventories;
	private static final Map<String, ItemStack[]> playerArmor;
	private static final Map<String, GameMode> playerGameModes;

	static {
		playerInventories = new HashMap<>();
		playerArmor = new HashMap<>();
		playerGameModes = new HashMap<>();
		hideStaff = new HashSet<>();
	}

	public static boolean isModMode(final Player player) {
		return player.hasMetadata(MOD_MODE_META);
	}

	public static boolean isInvisible(final Player player) {
		return player.hasMetadata(INVISIBILITY_META);
	}

	public static void enableModMode(final Player player) {
		enableModMode(player, false);
	}

	public static void setModMode(final boolean modMode, final Player player) {
		if (modMode) {
			enableModMode(player);
		} else {
			disableModMode(player);
		}
	}

	public static void enableModMode(final Player player, final boolean silent) {
		if (!silent) {
			player.sendMessage(ChatColor.GOLD + "Mod Mode: " + ChatColor.GREEN + "Enabled");
		}

		player.setMetadata(MOD_MODE_META, new FixedMetadataValue(Foxtrot.getInstance(), true));

		playerInventories.put(player.getName(), player.getInventory().getContents());
		playerArmor.put(player.getName(), player.getInventory().getArmorContents());
		playerGameModes.put(player.getName(), player.getGameMode());

		enableInvisibility(player);

		player.getInventory().clear();
		player.getInventory().setArmorContents(null);

		if (player.hasPermission("basic.gamemode")) {
			player.setGameMode(GameMode.CREATIVE);
		} else {
			Foxtrot.getInstance().getLogger().info("Setting " + player.getName() + " to fly mode!");

			player.setGameMode(GameMode.SURVIVAL);
			player.setAllowFlight(true);
			player.setFlying(true);
		}

		player.getInventory().setItem(0, StaffItems.COMPASS);
		player.getInventory().setItem(1, StaffItems.INSPECT_BOOK);

		if (player.hasPermission("worldedit.wand")) {
			player.getInventory().setItem(2, StaffItems.WAND);
			player.getInventory().setItem(3, StaffItems.CARPET);
			player.getInventory().setItem(4, StaffItems.RANDOM_TELEPORT);
		} else {
			player.getInventory().setItem(2, StaffItems.CARPET);
			player.getInventory().setItem(3, StaffItems.RANDOM_TELEPORT);
		}

		final ItemStack onlineStaff = StaffItems.ONLINE_STAFF.clone();
		player.getInventory().setItem(7, onlineStaff);
		player.getInventory().setItem(8, StaffItems.GO_VIS);
		player.updateInventory();

		if (Bukkit.getPluginManager().getPlugin("LunarClient-API") != null) {
			LunarClientAPI.getInstance().giveAllStaffModules(player);
		}
	}

	public static void disableModMode(final Player player) {
		player.sendMessage(ChatColor.GOLD + "Mod Mode: " + ChatColor.RED + "Disabled");
		player.removeMetadata(MOD_MODE_META, Foxtrot.getInstance());

		disableInvisibility(player);

		player.getInventory().setContents(playerInventories.remove(player.getName()));
		player.getInventory().setArmorContents(playerArmor.remove(player.getName()));
		player.setGameMode(playerGameModes.remove(player.getName()));

		if (player.getGameMode() != GameMode.CREATIVE) {
			player.setAllowFlight(false);
		}

		player.updateInventory();

		if (Bukkit.getPluginManager().getPlugin("LunarClient-API") != null) {
			LunarClientAPI.getInstance().disableAllStaffModules(player);
		}
	}

	public static void enableInvisibility(final Player player) {
		player.setMetadata(INVISIBILITY_META, new FixedMetadataValue(Foxtrot.getInstance(), true));

		FrozenNametagHandler.reloadPlayer(player);
		FrozenVisibilityHandler.update(player);

		player.spigot().setCollidesWithEntities(false);
		player.getInventory().setItem(8, StaffItems.GO_VIS);
		player.updateInventory();
		player.spigot().setCollidesWithEntities(false);
	}

	public static void disableInvisibility(final Player player) {
		player.removeMetadata(INVISIBILITY_META, Foxtrot.getInstance());

		FrozenVisibilityHandler.update(player);
		FrozenNametagHandler.reloadPlayer(player);

		player.spigot().setCollidesWithEntities(!isModMode(player));

		player.getInventory().setItem(8, StaffItems.GO_INVIS);
		player.updateInventory();

		player.spigot().setCollidesWithEntities(true);
	}

	public static void showStaff(final Player player) {
		hideStaff.remove(player.getUniqueId());
		FrozenVisibilityHandler.updateAllTo(player);
	}

	public static void hideStaff(final Player player) {
		hideStaff.add(player.getUniqueId());
		FrozenVisibilityHandler.updateAllTo(player);
	}

	public static void randomTeleport(final Player player) {
		List<Player> players = new ArrayList<>();

		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (onlinePlayer == player || isModMode(onlinePlayer)) continue;
			players.add(onlinePlayer);
		}

		if (players.isEmpty()) {
			player.sendMessage(ChatColor.RED + "No players to teleport to.");
			return;
		}

		player.teleport(players.get(ThreadLocalRandom.current().nextInt(players.size())));
	}

}