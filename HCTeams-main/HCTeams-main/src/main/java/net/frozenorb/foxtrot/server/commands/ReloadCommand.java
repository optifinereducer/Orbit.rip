package net.frozenorb.foxtrot.server.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCommand {

    @Command(names = { "foxtrot reload" }, description = "Reload the Foxtrot configuration", permission = "op")
    public static void execute(CommandSender sender) {
        Foxtrot.getInstance().reloadConfig();
        Foxtrot.getInstance().getGemHandler().loadChances();
        sender.sendMessage(ChatColor.GREEN.toString() + "Reloaded Foxtrot!");
    }

}
