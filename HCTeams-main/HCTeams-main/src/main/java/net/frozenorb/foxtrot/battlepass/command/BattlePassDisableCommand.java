package net.frozenorb.foxtrot.battlepass.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class BattlePassDisableCommand {

    @Command(names = { "battlepass disable", "bp disable" }, description = "Disable the BattlePass", permission = "battlepass.disable", async = true)
    public static void execute(CommandSender sender) {
        if (Foxtrot.getInstance().getBattlePassHandler() == null) {
            sender.sendMessage(ChatColor.RED + "BattlePass is not enabled on this server!");
            return;
        }

        Foxtrot.getInstance().getBattlePassHandler().setAdminDisabled(!Foxtrot.getInstance().getBattlePassHandler().isAdminDisabled());

        if (Foxtrot.getInstance().getBattlePassHandler().isAdminDisabled()) {
            sender.sendMessage(ChatColor.RED + "BattlePass has been temporarily disabled!");
        } else {
            sender.sendMessage(ChatColor.GREEN + "BattlePass has been enabled!");
        }
    }

}
