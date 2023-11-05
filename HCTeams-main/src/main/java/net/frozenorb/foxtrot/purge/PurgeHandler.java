package net.frozenorb.foxtrot.purge;

import lombok.Getter;
import lombok.SneakyThrows;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.purge.reward.PurgeRewardMap;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.FileConfig;
import net.frozenorb.qlib.util.ItemBuilder;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public final class PurgeHandler {

	public static final String PREFIX = "&4&lPurge";

	private final Map<UUID, Integer> killMap = new HashMap<>();

	@Getter private final PurgeRewardMap purgeRewardMap = new PurgeRewardMap();

	private final FileConfig fileConfig = new FileConfig(Foxtrot.getInstance(), "purge_rewards.yml");

	public PurgeHandler() {
		purgeRewardMap.loadFromRedis();
		loadConfig();
	}

	private void loadConfig() {
		FileConfiguration config = fileConfig.getConfig();
		config.addDefault("purge-reward-commands-tier-1", Arrays.asList("/raw %player% is raw tier one", "/raw %player% is raw af"));
		config.addDefault("purge-reward-commands-tier-2", Arrays.asList("/raw %player% is raw tier two", "/raw %player% is raw af"));
		config.addDefault("purge-reward-commands-tier-3", Arrays.asList("/raw %player% is raw tier three", "/raw %player% is raw af"));
		config.options().copyDefaults(true);
		fileConfig.save();
	}

	@SneakyThrows public void reload() {
		fileConfig.getConfig().load(fileConfig.getFile());
	}

	public List<String> getCommands(Player player, int tier) {
		return fileConfig.getConfig().getStringList("purge-reward-commands-tier-" + tier)
				.stream()
				.map(string -> string.startsWith("/") ? string.substring(1) : string)
				.map(string -> string.replace("%player%", player.getName()))
				.collect(Collectors.toList());
	}


	public void trackKill(Player player) {
		int kills = killMap.getOrDefault(player.getUniqueId(), 0);
		killMap.put(player.getUniqueId(), ++kills);
	}

	public List<Map.Entry<UUID, Integer>> getLeaderBoard() {
		List<Map.Entry<UUID, Integer>> list = killMap.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toList());
		Collections.reverse(list);
		return list;
	}

	// this code is terrible but oh well
	private BukkitTask active = null;

	public void commence(int seconds) {
		if (active != null) {
			active.cancel();
		}

		active = new BukkitRunnable() {
			@Override
			public void run() {
				List<String> endMessage = buildEndMessage();
				Sound sound = Sound.NOTE_PLING;
				for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
					onlinePlayer.playSound(onlinePlayer.getLocation(), sound, 1f, 1f);
					PURGE_END_MESSAGES.forEach(message -> message.send(onlinePlayer));
					onlinePlayer.sendMessage(" ");
					endMessage.forEach(onlinePlayer::sendMessage);
				}
				killMap.clear();
				active = null;
			}
		}.runTaskLater(Foxtrot.getInstance(), 20 * seconds);

		killMap.clear();

		new BukkitRunnable() {
			int ticks = 1;
			@Override
			public void run() {
				ticks++;

				Sound sound = (ticks % 2 == 0) ? Sound.ENDERDRAGON_GROWL : Sound.ENDERDRAGON_WINGS;

				for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
					onlinePlayer.playSound(onlinePlayer.getLocation(), sound, 1f, 1f);
				}

				if (ticks > 6) {
					cancel();
				}
			}
		}.runTaskTimer(Foxtrot.getInstance(), 0L, 15L);

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			PURGE_COMMENCE_MESSAGES.forEach(message -> message.send(player));
		}
	}

	private List<String> buildEndMessage() {
		List<String> strings = new ArrayList<>();
		strings.add("&7&m--------------------------");
		strings.add("&c» &4&lPurge Leaderboard &c«");
		strings.add(" ");

		List<Map.Entry<UUID, Integer>> leaderBoard = getLeaderBoard();
		for (int i = 0; i < leaderBoard.size(); i++) {
			if (i > 4) break;
			Map.Entry<UUID, Integer> entry = leaderBoard.get(i);
			UUID uuid = entry.getKey();

			if (i < 3) {
				int tier = i == 0 ? 3 : i == 1 ? 2 : 1;
				purgeRewardMap.addRewards(uuid, tier);
			}

			Player player = Bukkit.getPlayer(uuid);
			String name = null;
			if (player != null) {
				name = player.getDisplayName();
			}

			if (name == null) {
				name = FrozenUUIDCache.name(uuid);
			}

			strings.add("&c" + (i + 1) + ". &7" + name + " &f- " + entry.getValue() + " Kills");
		}
		strings.add("&7&m--------------------------");

		return strings.stream()
				.map(CC::translate)
				.collect(Collectors.toList());
	}

	private static final List<FancyMessage> PURGE_COMMENCE_MESSAGES = Arrays.asList(
			new FancyMessage("███████").color(ChatColor.RED),
			new FancyMessage("")
					.then("█").color(ChatColor.RED)
					.then("█████").color(ChatColor.DARK_RED)
					.then("█").color(ChatColor.RED),
			new FancyMessage("")
					.then("█").color(ChatColor.RED)
					.then("█").color(ChatColor.DARK_RED)
					.then("███").color(ChatColor.RED)
					.then("█").color(ChatColor.DARK_RED)
					.then("█").color(ChatColor.RED).then(" [PURGE]").color(ChatColor.RED),
			new FancyMessage("")
					.then("█").color(ChatColor.RED)
					.then("█████").color(ChatColor.DARK_RED)
					.then("█").color(ChatColor.RED).then(" Purge has commenced.").color(ChatColor.RED).style(ChatColor.BOLD),
			new FancyMessage("")
					.then("█").color(ChatColor.RED)
					.then("█").color(ChatColor.DARK_RED)
					.then("█████").color(ChatColor.RED).then(" Faction claims are now compromised.").color(ChatColor.RED),
			new FancyMessage("")
					.then("█").color(ChatColor.RED)
					.then("█").color(ChatColor.DARK_RED)
					.then("█████").color(ChatColor.RED),
			new FancyMessage("███████").color(ChatColor.RED)
	);

	private static final List<FancyMessage> PURGE_END_MESSAGES = Arrays.asList(
			new FancyMessage("███████").color(ChatColor.RED),
			new FancyMessage("")
					.then("█").color(ChatColor.RED)
					.then("█████").color(ChatColor.DARK_RED)
					.then("█").color(ChatColor.RED),
			new FancyMessage("")
					.then("█").color(ChatColor.RED)
					.then("█").color(ChatColor.DARK_RED)
					.then("███").color(ChatColor.RED)
					.then("█").color(ChatColor.DARK_RED)
					.then("█").color(ChatColor.RED).then(" [PURGE]").color(ChatColor.RED),
			new FancyMessage("")
					.then("█").color(ChatColor.RED)
					.then("█████").color(ChatColor.DARK_RED)
					.then("█").color(ChatColor.RED).then(" Purge has ended!").color(ChatColor.GREEN).style(ChatColor.BOLD),
			new FancyMessage("")
					.then("█").color(ChatColor.RED)
					.then("█").color(ChatColor.DARK_RED)
					.then("█████").color(ChatColor.RED),
			new FancyMessage("")
					.then("█").color(ChatColor.RED)
					.then("█").color(ChatColor.DARK_RED)
					.then("█████").color(ChatColor.RED),
			new FancyMessage("███████").color(ChatColor.RED)
	);
}
