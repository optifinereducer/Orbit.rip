package net.frozenorb.foxtrot.battlepass.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.battlepass.BattlePassProgress;
import net.frozenorb.foxtrot.util.Formats;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class BattlePassSetXPCommand {

    @Command(names = { "battlepass setxp", "bp setxp" }, description = "Set a player's BattlePass progress XP", permission = "battlepass.setxp", async = true)
    public static void execute(CommandSender sender, @Param(name = "target") UUID targetUuid, @Param(name = "xp") int xp) {
        if (Foxtrot.getInstance().getBattlePassHandler() == null) {
            sender.sendMessage(ChatColor.RED + "BattlePass is not enabled on this server!");
            return;
        }

        BattlePassProgress progress = Foxtrot.getInstance().getBattlePassHandler().getOrLoadProgress(targetUuid);
        progress.setExperience(xp);
        progress.requiresSave();

        Foxtrot.getInstance().getBattlePassHandler().saveProgress(progress);

        sender.sendMessage(ChatColor.GREEN + "Set " + ChatColor.WHITE + FrozenUUIDCache.name(targetUuid) + ChatColor.GREEN + "'s BattlePass XP to " + ChatColor.WHITE + Formats.formatNumber(xp) + ChatColor.GREEN + "!");
    }

}
