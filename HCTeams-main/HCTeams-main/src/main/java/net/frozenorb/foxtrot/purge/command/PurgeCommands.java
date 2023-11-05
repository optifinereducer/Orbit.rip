package net.frozenorb.foxtrot.purge.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.purge.PurgeHandler;
import net.frozenorb.foxtrot.purge.reward.PurgeRewardMap;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public final class PurgeCommands {

	@Command(names = {"purge reclaim", "purge claim"}, permission = "")
	public static void purgeReclaim(Player sender) {
		PurgeHandler handler = Foxtrot.getInstance().getPurgeHandler();
		PurgeRewardMap rewardMap = handler.getPurgeRewardMap();
		if (!rewardMap.hasReward(sender.getUniqueId())) {
			sender.sendMessage(CC.RED + "You do not have any purge rewards to claim :c");
			return;
		}

		// this is a bit weird but we use `points` basically to determine the tiers to open
		// players receive points as: third 1 point, second 2 points, first 3 points
		// it will open the best available tier the person has
		// so if they place third in one purge and second in another they will have 3 points
		// and will open a tier one chest (the best)
		int remaining = rewardMap.getRewards(sender.getUniqueId()) - 3;

		int tier = 1;

		if (remaining == -2) {
			tier = 3;
			remaining = 0;
		} else if (remaining == -1) {
			tier = 2;
			remaining = 0;
		}

		rewardMap.setRewards(sender.getUniqueId(), remaining);
		handler.getCommands(sender, tier).forEach(command -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command));
	}

	@Command(names = {"purge setreclaim", "purge setclaim"}, permission = "foxtrot.developer")
	public static void purgeSetReclaim(CommandSender sender,
	                                   @Param(name = "target", defaultValue = "self") Player target,
	                                   @Param(name = "amount") int amount) {
		PurgeHandler handler = Foxtrot.getInstance().getPurgeHandler();
		PurgeRewardMap rewardMap = handler.getPurgeRewardMap();
		rewardMap.setRewards(target.getUniqueId(), amount);
		sender.sendMessage(
				CC.RED + "Set " + target.getDisplayName() + CC.RED + "'s reclaim to " + CC.YELLOW + amount + CC.RED + "!"
		);
	}

	@Command(names = {"purge checkreclaim", "purge cr"}, permission = "foxtrot.developer")
	public static void purgeCheckReclaim(CommandSender sender, @Param(name = "target", defaultValue = "self") Player target) {
		PurgeHandler handler = Foxtrot.getInstance().getPurgeHandler();
		PurgeRewardMap rewardMap = handler.getPurgeRewardMap();
		int amount = rewardMap.getRewards(target.getUniqueId());
		sender.sendMessage(target.getDisplayName() + CC.RED + " has " + CC.YELLOW + amount + CC.RED + " reclaim value!");
	}

	@Command(names = {"purge reload", "purge rl"}, permission = "foxtrot.purge")
	private static void purgeReload(CommandSender sender) {
		Foxtrot.getInstance().getPurgeHandler().reload();
		sender.sendMessage(CC.GREEN + "Reloaded purge!");
	}

	@Command(names = "purge commence", permission = "foxtrot.purge")
	public static void purgeCommence(Player sender, @Param(name = "time") String time) {
		int seconds;
		try {
			seconds = TimeUtils.parseTime(time);
		} catch (IllegalArgumentException e) {
			sender.sendMessage(CC.RED + e.getMessage());
			return;
		}
		if (seconds < 0) {
			sender.sendMessage(ChatColor.RED + "Invalid time!");
			return;
		}

		ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

			public String getPromptText(ConversationContext context) {
				return "§cType §4§lCONFIRM §cto begin the purge event! §7(/no to quit.)";
			}

			@Override
			public Prompt acceptInput(ConversationContext cc, String s) {
				if ("CONFIRM".equalsIgnoreCase(s)) {
					CustomTimerCreateCommand.getCustomTimers().put(PurgeHandler.PREFIX, System.currentTimeMillis() + (seconds * 1000));
					Foxtrot.getInstance().getPurgeHandler().commence(seconds);
					return Prompt.END_OF_CONVERSATION;
				}

				if (s.equalsIgnoreCase("no")) {
					cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Cancelled.");
					return Prompt.END_OF_CONVERSATION;
				}

				cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §bCONFIRM§a to confirm or §c/no§a to quit.");
				return this;
			}

		}).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

		Conversation con = factory.buildConversation(sender);
		sender.beginConversation(con);
	}

	@Command(names = { "purge stop" }, permission = "foxtrot.purge")
	public static void purgeCancel(CommandSender sender) {
		Long removed = CustomTimerCreateCommand.getCustomTimers().remove(PurgeHandler.PREFIX);
		if (removed != null && System.currentTimeMillis() < removed) {
			sender.sendMessage(ChatColor.GREEN + "Deactivated the Purge timer.");
			return;
		}

		sender.sendMessage(ChatColor.RED + "Purger timer is not active.");
	}
}
