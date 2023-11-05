package net.frozenorb.foxtrot.map.stats.command;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.stats.StatsEntry;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.UUIDUtils;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class StatsTopCommand {

    @Command(names = {"statstop", "leaderboards", "lb"}, permission = "")
    public static void statstop(CommandSender sender, @Param(name = "objective", defaultValue = "kills") StatsObjective objective) {
        sender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
        sender.sendMessage(ChatColor.YELLOW + "» " + ChatColor.AQUA + ChatColor.BOLD + objective.getName() + " Leaderboard" + ChatColor.YELLOW + " «");
        sender.sendMessage("");

        int index = 0;
        for (Map.Entry<StatsEntry, String> entry : Foxtrot.getInstance().getMapHandler().getStatsHandler().getLeaderboards(objective, 10).entrySet()) {
            index++;
            sender.sendMessage((index == 1 ? ChatColor.RED + "1 " : ChatColor.GRAY.toString() + index + " ") + ChatColor.LIGHT_PURPLE.toString() + UUIDUtils.name(entry.getKey().getOwner()) + ": " + ChatColor.WHITE + entry.getValue());
        }

        sender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
    }

    @Getter
    public enum StatsObjective {

        KILLS("Kills", "k"),
        DEATHS("Deaths", "d"),
        KD("KD", "kdr"),
        HIGHEST_KILLSTREAK("Highest Killstreak", "killstreak", "highestkillstreak", "ks", "highestks", "hks");

        private String name;
        private String[] aliases;

        StatsObjective(String name, String... aliases) {
            this.name = name;
            this.aliases = aliases;
        }

    }

}
