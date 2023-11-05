package net.frozenorb.foxtrot.redeem;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.qLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class RedeemCreatorCommand {

	@Command(names = {"redeem", "redeemcreator", "creatorredeem"}, description = "Redeem a creator", permission = "", async = true)
	public static void execute(Player player, @Param(name = "creator") String creator) {
		List<String> commands = Foxtrot.getInstance().getRedeemCreatorHandler().getPartnerCommands(creator);

		if (commands == null) {
			player.sendMessage(CC.RED + "No creator by the name of " + CC.YELLOW + creator + CC.RED + " exists!");
			return;
		}

		if (hasRedeemed(player)) {
			player.sendMessage(CC.RED + "You have already redeemed a creator this map!");
			return;
		}

		redeem(player, creator);
		Bukkit.getServer().getScheduler().runTask(Foxtrot.getInstance(), () -> {
			for (String command : commands) {
				command = command.startsWith("/") ? command.substring(1) : command;
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
			}
		});
	}

	@Command(names = {"redeemclear"}, description = "Clear a players", permission = "redeem.creator.clear", async = true)
	public static void excuteClear(Player sender, @Param(name = "target", defaultValue = "self") Player target) {
		qLib.getInstance().runRedisCommand(jedis -> jedis.del("redeemed_creator:" + target.getUniqueId().toString()));
		sender.sendMessage(CC.GREEN + "Reset");
	}

	@Command(names = {"redeemview", "redeembreakdown"}, description = "Review redeem stats", permission = "redeem.creator.stats", async = true)
	public static void execute(CommandSender sender) {
		sender.sendMessage(CC.RED + "Breakdancing...");
		qLib.getInstance().runRedisCommand(jedis -> {
			Set<String> keys = jedis.keys("redeemed_creator:*");
			Map<String, Integer> countMap = new HashMap<>();
			for (String key : keys) {
				String creator = jedis.get(key).toLowerCase();

				Integer value = countMap.computeIfAbsent(creator, $ -> 0);
				countMap.put(creator, ++value);
			}

			LinkedHashMap<String, Integer> sorted = countMap.entrySet()
					.stream()
					.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

			sorted.forEach((creator, amount) -> {
				sender.sendMessage(creator + " - " + amount);
			});

			return null;
		});
	}

	private static boolean hasRedeemed(Player player) {
		return qLib.getInstance().runRedisCommand(jedis -> jedis.exists("redeemed_creator:" + player.getUniqueId().toString()));
	}

	private static void redeem(Player player, String creator) {
		qLib.getInstance().runRedisCommand(jedis -> jedis.set("redeemed_creator:" + player.getUniqueId().toString(), creator));
	}
}
