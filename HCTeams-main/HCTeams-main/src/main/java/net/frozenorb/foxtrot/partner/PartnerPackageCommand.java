package net.frozenorb.foxtrot.partner;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.impl.purge.MaskPurgePackage;
import net.frozenorb.foxtrot.purge.PurgeHandler;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.EffectUtil;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.TimeUtils;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class PartnerPackageCommand {

	@Command(names = "partneritems", permission = "")
	public static void partnerItems(Player sender) {
		new PartnerPackageMenu(false).openMenu(sender);
	}

	@Command(names = "purgeitems", permission = "")
	public static void purgeItems(Player sender) {
		new PartnerPackageMenu(true).openMenu(sender);
	}

	@Command(names = {"purgepackage all"}, permission = "foxtrot.pp.admin")
	public static void purgepall(CommandSender sender) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			ItemStack crateItem = PartnerCrateHandler.PURGE_PACKAGE.clone();
			if (!InventoryUtils.addAmountToInventory(player.getInventory(), crateItem, 1)) {
				player.getWorld().dropItemNaturally(player.getLocation(), crateItem);
			}
			player.sendMessage(CC.GREEN + "You have received 1 " + crateItem.getItemMeta().getDisplayName());
		}
		sender.sendMessage(CC.GREEN + "Gave one purge package to all players!");
	}

	@Command(names = "pp hour stop", permission = "foxtrot.package.hour")
	public static void partnerHourStop(CommandSender sender) {
		String prefix = PartnerPackageHandler.PREFIX;
		Long removed = CustomTimerCreateCommand.getCustomTimers().remove(prefix);
		if (removed != null && System.currentTimeMillis() < removed) {
			sender.sendMessage(ChatColor.GREEN + "Deactivated the " + ChatColor.translateAlternateColorCodes('&', prefix) + CC.GREEN + " timer.");
			return;
		}

		sender.sendMessage(ChatColor.RED + "Not active");
	}

	@Command(names = "pp hour", permission = "foxtrot.package.hour")
	public static void partnerHourCommence(Player sender, @Param(name = "time") String time) {
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
				return "§cType §4§lCONFIRM §cto begin the partner package event! §7(/no to quit.)";
			}

			@Override
			public Prompt acceptInput(ConversationContext cc, String s) {
				if ("CONFIRM".equalsIgnoreCase(s)) {
					CustomTimerCreateCommand.getCustomTimers().put(PartnerPackageHandler.PREFIX, System.currentTimeMillis() + (seconds * 1000));
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

	@Command(names = "purgeprotlevel", permission = "op")
	public static void changeProtLevel(CommandSender sender, @Param(name = "amount") int level) {
		sender.sendMessage(CC.RED + "Changed level from " + MaskPurgePackage.protectionLevel + " to " + level);
		MaskPurgePackage.protectionLevel = level;
	}

	@Command(names = {"purgepackage"}, permission = "foxtrot.pp.give")
	public static void purgeGive(CommandSender sender, @Param(name = "player") Player target, @Param(name = "amount") int amount) {
		ItemStack item = PartnerCrateHandler.PURGE_PACKAGE.clone();
		item.setAmount(amount);
		target.getInventory().addItem(item);
		sender.sendMessage(CC.GREEN + "Gave " + CC.DARK_GREEN + amount + " " + CC.PINK + CC.BOLD +
				item.getItemMeta().getDisplayName() + CC.GREEN + " to " + CC.DARK_GREEN + target.getName() + CC.GREEN + "!");
	}

	@Command(names = {"ppitem", "partnerpackage", "pp give", "partnerpackage give"}, permission = "foxtrot.pp.give")
	public static void give(CommandSender sender, @Param(name = "player") Player target,
	                        @Param(name = "package") PartnerPackage partnerPackage,
	                        @Param(name = "amount") int amount) {
		ItemStack item = partnerPackage.getPartnerItem();
		item.setAmount(amount);
		target.getInventory().addItem(item);
		sender.sendMessage(CC.GREEN + "Gave " + CC.DARK_GREEN + amount + " " + CC.PINK + CC.BOLD +
				partnerPackage.getName() + CC.GREEN + " to " + CC.DARK_GREEN + target.getName() + CC.GREEN + "!");
	}

	@Command(names = {"pp"}, permission = "foxtrot.pp.give")
	public static void ppGive(CommandSender sender, @Param(name = "player") Player target, @Param(name = "amount") int amount) {
		ItemStack item = Foxtrot.getInstance().getPartnerCrateHandler().getCrateItem().clone();
		item.setAmount(amount);
		target.getInventory().addItem(item);
		sender.sendMessage(CC.GREEN + "Gave " + CC.DARK_GREEN + amount + " " + CC.PINK + CC.BOLD +
				item.getItemMeta().getDisplayName() + CC.GREEN + " to " + CC.DARK_GREEN + target.getName() + CC.GREEN + "!");
	}

	@Command(names = {"pp reset"}, permission = "foxtrot.pp.admin")
	public static void reset(CommandSender sender, @Param(name = "player") Player target,
	                        @Param(name = "package") PartnerPackage partnerPackage) {
		partnerPackage.resetCooldown(target);
		sender.sendMessage(CC.GREEN + "Reset package cooldown for player " + CC.DARK_GREEN + target.getName() + CC.GREEN + "!");
	}

	@Command(names = {"pp all"}, permission = "foxtrot.pp.admin")
	public static void ppall(CommandSender sender) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			ItemStack crateItem = Foxtrot.getInstance().getPartnerCrateHandler().getCrateItem();
			if (!InventoryUtils.addAmountToInventory(player.getInventory(), crateItem, 1)) {
				player.getWorld().dropItemNaturally(player.getLocation(), crateItem);
			}
			player.sendMessage(CC.GREEN + "You have received 1 " + crateItem.getItemMeta().getDisplayName());
		}
		sender.sendMessage(CC.GREEN + "Gave one partner package to all players!");
	}

	@Command(names = {"pp setabsorb"}, permission = "foxtrot.pp.admin")
	public static void setAbsorb(Player sender, @Param(name = "hearts") float hearts) {
		EntityPlayer entity = ((CraftPlayer) sender).getHandle();
		sender.sendMessage(CC.GREEN + "Before: " + CC.DARK_GREEN + entity.getAbsorptionHearts());
		entity.setAbsorptionHearts(hearts);
		entity.triggerHealthUpdate();
		sender.sendMessage(CC.GREEN + "After: " + CC.DARK_GREEN + entity.getAbsorptionHearts());
	}

	@Command(names = {"pp bleed"}, permission = "foxtrot.pp.admin")
	public static void bleed(Player sender, @Param(name = "target", defaultValue = "self") Player target) {
		EffectUtil.bleed(target);
		sender.sendMessage(CC.GREEN + "Bleeding: " + CC.DARK_GREEN + target.getName());
	}

}
