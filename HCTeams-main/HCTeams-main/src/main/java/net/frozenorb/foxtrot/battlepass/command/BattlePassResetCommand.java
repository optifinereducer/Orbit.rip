package net.frozenorb.foxtrot.battlepass.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class BattlePassResetCommand {

    @Command(names = { "battlepass reset", "bp reset" }, description = "Resets a player's BattlePass progress", permission = "battlepass.reset", async = true)
    public static void execute(CommandSender player, @Param(name = "player") UUID targetUuid) {
        if (Foxtrot.getInstance().getBattlePassHandler() == null) {
            player.sendMessage(ChatColor.RED + "BattlePass is not enabled on this server!");
            return;
        }

        Foxtrot.getInstance().getBattlePassHandler().clearProgress(targetUuid);
        player.sendMessage(ChatColor.GREEN + "Cleared BattlePass progress of " + ChatColor.WHITE + FrozenUUIDCache.name(targetUuid) + ChatColor.GREEN + "!");
    }

}
