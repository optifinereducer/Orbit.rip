package net.frozenorb.foxtrot.map.kits.command;

import net.frozenorb.foxtrot.map.kits.DefaultKit;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitsLoadCommand {
    
    @Command(names = { "managekit load" }, permission = "op")
    public static void execute(Player sender, @Param(name = "kit", wildcard = true) DefaultKit kit) {
        kit.apply(sender);
        
        sender.sendMessage(ChatColor.YELLOW + "Applied the " + ChatColor.BLUE + kit.getName() + ChatColor.YELLOW + ".");
    }

}
