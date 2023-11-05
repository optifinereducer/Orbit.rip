package net.frozenorb.foxtrot.reclaim.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class ResetCommand {

    @Command(names = { "reclaim reset" }, description = "Reset a player's reclaim status", permission = "op")
    public static void reset(CommandSender sender, @Param(name = "target") UUID target) {
        if (Foxtrot.getInstance().getReclaimHandler().getHasReclaimed().remove(target)) {
            sender.sendMessage(ChatColor.GREEN + "Reset " + FrozenUUIDCache.name(target) + "'s reclaim status!");
        } else {
            sender.sendMessage(ChatColor.RED + FrozenUUIDCache.name(target) + " hasn't claimed their reclaim!");
        }
    }

}
