package net.frozenorb.foxtrot.gem.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gem.GemHandler;
import net.frozenorb.foxtrot.gem.menu.GemMenu;
import net.frozenorb.foxtrot.persist.maps.GemMap;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public class GemCommand {

    @Command(names = {"gem help", "gems help", "gemshelp", "gemhelp"}, permission = "")
    public static void help(CommandSender sender) {
        String[] msg = {
                "§8§m-----------------------------------------------------",
                "§c§lGem Help",
                " ",
                "§7/gems §f- View your gem balance.",
                "§7/gems <player> §f- View a player's gem balance.",
                "§7/gemshop §f- Open the gem shop.",
                "§7/gem pay <player> <amount> §f- Send a player gems.",

                "§8§m-----------------------------------------------------",
        };

        sender.sendMessage(msg);
    }

    @Command(names = {"gem", "gems", "gem balance", "gems balance"}, description = "Check another player's balance", permission = "", async = true)
    public static void gems(Player sender, @Param(name = "player", defaultValue = "self") Player target) {
        GemMap gemMap = Foxtrot.getInstance().getGemMap();

        if (sender == target) {
            sender.sendMessage(CC.DARK_GREEN + "Gems: " + CC.GREEN + gemMap.getGems(target));
        } else {
            sender.sendMessage(CC.DARK_GREEN + target.getName() + "'s Gems: " + CC.GREEN + gemMap.getGems(target));
        }
    }

    @Command(names = {"gem shop", "gems shop", "gemshop"}, permission = "")
    public static void gemShop(Player sender) {
        if (SpawnTagHandler.isTagged(sender)) {
            sender.sendMessage(CC.RED + "You cannot do this while spawn tagged!");
            return;
        }

        if (Foxtrot.getInstance().getInDuelPredicate().test(sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to HQ during a duel!");
            return;
        }

        if (Foxtrot.getInstance().getMapHandler().getGameHandler() != null
                && Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()
                && Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame().isPlayingOrSpectating(sender.getUniqueId())
        ) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to HQ during a game!");
            return;
        }

        new GemMenu().openMenu(sender);
    }

    @Command(names = {"gem pay", "gems send", "gempay"}, permission = "")
    public static void gemPay(Player sender, @Param(name = "target") Player target, @Param(name = "amount") int amount) {
        if (amount <= 0) {
            sender.sendMessage(CC.RED + "Invalid gem amount.");
            return;
        }

        if (sender == target) {
            sender.sendMessage(CC.RED + "You cannot pay yourself!");
            return;
        }

        if (!Foxtrot.getInstance().getGemMap().removeGems(sender.getUniqueId(), amount)) {
            sender.sendMessage(CC.RED + "You do not have enough gems for this!");
            return;
        }

        Foxtrot.getInstance().getGemMap().addGems(target.getUniqueId(), amount, true);
        sender.sendMessage(CC.GREEN + "You paid " + target.getDisplayName() + " " + CC.DARK_GREEN + amount + CC.GREEN + " gems!");
        target.sendMessage(sender.getDisplayName() + CC.GREEN + " has sent you " + CC.DARK_GREEN + amount + CC.GREEN + " gems!");
    }

    @Command(names = {"gem set", "gems set"}, permission = "op")
    public static void gemsSet(CommandSender sender, @Param(name = "target", defaultValue = "self") Player player, @Param(name = "amount") int amount) {
        Foxtrot.getInstance().getGemMap().setValue(player, amount);
        player.sendMessage(CC.GREEN + "Your gems have been set to " + amount + ".");
        sender.sendMessage(CC.GREEN + "Set " + player.getName() + " gems to " + amount);
    }

    @Command(names = {"gem add", "gems add"}, permission = "op")
    public static void gemsAdd(CommandSender sender,
                               @Param(name = "target") OfflinePlayer target,
                               @Param(name = "amount") int amount) {
        GemMap gemMap = Foxtrot.getInstance().getGemMap();
        long newAmount = gemMap.getGems(target.getUniqueId()) + amount;
        gemMap.setValue(target.getUniqueId(), newAmount);
        sender.sendMessage(CC.GREEN + "Added " + amount + " gems to " + target.getName() + " total: " + newAmount);
    }

    @Command(names = {"gem hour", "gem double"}, permission = "foxtrot.gem.double")
    public static void gemHour(Player sender, @Param(name = "time") String time) {
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
                return "§cType §4§lCONFIRM §cto begin the " + GemHandler.DOUBLE_GEM_PREFIX.replace('&', '§') + " event! §7(/no to quit.)";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if ("CONFIRM".equalsIgnoreCase(s)) {
                    CustomTimerCreateCommand.getCustomTimers().put(GemHandler.DOUBLE_GEM_PREFIX, System.currentTimeMillis() + (seconds * 1000));
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

    @Command(names = {"gem hour stop", "gem double stop"}, permission = "foxtrot.gem.double")
    public static void gemHour(Player sender) {
        Long removed = CustomTimerCreateCommand.getCustomTimers().remove(GemHandler.DOUBLE_GEM_PREFIX);
        if (removed != null && System.currentTimeMillis() < removed) {
            sender.sendMessage(ChatColor.GREEN + "Deactivated the double gem timer.");
            return;
        }

        sender.sendMessage(ChatColor.RED + "Purger timer is not active.");
    }

}
