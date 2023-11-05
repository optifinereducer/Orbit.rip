package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ToggleClaimMessagesCommand {

    @Command(names = {"ToggleClaimMessages", "tcm"}, permission = "")
    public static void toggleClaims(Player sender) {
        boolean val = !Foxtrot.getInstance().getToggleClaimMessageMap().areClaimMessagesEnabled(sender.getUniqueId());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see Claim Messages!");
        Foxtrot.getInstance().getToggleClaimMessageMap().setClaimMessagesEnabled(sender.getUniqueId(), val);
    }
}