package net.frozenorb.foxtrot.map.kits.command;

import net.frozenorb.qlib.command.Param;
import org.bukkit.entity.Player;

import net.frozenorb.qlib.command.Command;

public class KitCommand {
    
    @Command(names = { "kits", "kit" }, permission = "")
    public static void execute(Player sender, @Param(name = "kit") String forward) {
        sender.performCommand("gkitz " + forward);
    }

}
