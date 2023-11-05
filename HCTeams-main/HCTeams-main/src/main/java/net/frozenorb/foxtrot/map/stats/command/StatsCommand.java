package net.frozenorb.foxtrot.map.stats.command;

import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.stats.StatsEntry;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.TimeUtils;
import net.frozenorb.qlib.util.UUIDUtils;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StatsCommand {

    @Command(names = {"statstop"}, permission = "")
    public static void statstop(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "Loading stats...");

        List<Object> messages = new ArrayList<>();
        messages.add("§7§m---------------------------");
        messages.add("§7» §c§lStats Leaderboard §7«");
        messages.add(" ");
        for (int place = 1; place < 11; place++) {
            StatsEntry stats = Foxtrot.getInstance().getMapHandler().getStatsHandler().get(StatsTopCommand.StatsObjective.KILLS, place);
            if (stats == null)
                continue;
            String name = UUIDUtils.name(stats.getOwner());
            FancyMessage message = new FancyMessage("§c" + place + ". §7" + name);
            message.tooltip(
                    "§c§l" + name,
                    " ",
                    "§4Kills§7: §c" + stats.getKills(),
                    "§4Deaths§7: §c" + stats.getDeaths(),
                    " ",
                    "§aClick to view stats info"
            );
            message.command("/stats " + name);
            messages.add(message);
        }
        messages.add("§7§m---------------------------");

        messages.forEach(message -> {
            if (message instanceof FancyMessage) {
                ((FancyMessage) message).send(sender);
            } else {
                sender.sendMessage((String) message);
            }
        });
    }

    @Command(names = {"stats"}, permission = "")
    public static void stats(CommandSender sender, @Param(name = "player", defaultValue = "self") UUID uuid) {
        StatsEntry stats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(uuid);

        if (stats == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }

        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
        sender.sendMessage(ChatColor.RED + UUIDUtils.name(uuid));

        sender.sendMessage(ChatColor.DARK_RED + "Kills: " + ChatColor.RED + stats.getKills());
        sender.sendMessage(ChatColor.DARK_RED + "Deaths: " + ChatColor.RED + stats.getDeaths());
        sender.sendMessage(ChatColor.DARK_RED + "KDR: " + ChatColor.RED + (stats.getDeaths() == 0 ? "Infinity" : Team.DTR_FORMAT.format((double) stats.getKills() / (double) stats.getDeaths())));
        sender.sendMessage(ChatColor.DARK_RED + "Gems: " + ChatColor.RED + Foxtrot.getInstance().getGemMap().getGems(uuid));
        String playtime = TimeUtils.formatIntoDetailedString((int) Foxtrot.getInstance().getPlaytimeMap().getPlaytime(uuid));
        sender.sendMessage(ChatColor.DARK_RED + "Playtime: " + ChatColor.RED + playtime);

        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
    }

    @Command(names = {"clearallstats"}, permission = "op")
    public static void clearallstats(Player sender) {
        ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to clear all stats? Type §byes§a to confirm or §cno§a to quit.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    Foxtrot.getInstance().getMapHandler().getStatsHandler().clearAll();
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "All stats cleared!");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Cancelled.");
                    return Prompt.END_OF_CONVERSATION;
                }

                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §b/yes§a to confirm or §c/no§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }

}
