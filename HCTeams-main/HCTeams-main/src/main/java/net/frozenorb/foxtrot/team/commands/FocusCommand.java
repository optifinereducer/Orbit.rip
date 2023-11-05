package net.frozenorb.foxtrot.team.commands;


import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FocusCommand {

    @Command(names = {"focus"}, description = "Focus a player", permission = "")
    public static void focus(Player sender, @Param(name = "player") Player target) {
        Team senderTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
        Team targetTeam = Foxtrot.getInstance().getTeamHandler().getTeam(target);

        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        // There's a few ways this can go:
        // a. Target's team is null, in which case they can be targeted.
        // b. Target's team is not null, in which case...
        //      1. The teams are equal, where they can't be targeted.
        //      2. They aren't equal, in which case they can be targeted.
        // This if statement really isn't as complex as the above
        // comment made it sound, but it took me a few minutes of
        // thinking through, so this is just to save time.
        if (senderTeam == targetTeam) {
            sender.sendMessage(ChatColor.RED + "You cannot target a player on your faction.");
            return;
        }

        senderTeam.setFocused(target.getUniqueId());
        senderTeam.sendMessage(ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + " has been focused by " + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + ".");

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (senderTeam.isMember(onlinePlayer.getUniqueId())) {
                FrozenNametagHandler.reloadOthersFor(onlinePlayer);
            }
        }
    }

    @Command(names = "unfocus", description = "Remove player focus", permission = "")
    public static void unfocus(Player sender) {
        Team senderTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        UUID focused = senderTeam.getFocused();
        if (focused == null) {
            sender.sendMessage(ChatColor.RED + "You are not focusing anyone!");
            return;
        }

        senderTeam.setFocused(null);
        senderTeam.sendMessage(ChatColor.LIGHT_PURPLE + FrozenUUIDCache.name(focused) + ChatColor.YELLOW + " has been unfocused by " + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + ".");

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (senderTeam.isMember(onlinePlayer.getUniqueId())) {
                FrozenNametagHandler.reloadOthersFor(onlinePlayer);
            }
        }
    }

    @Command(names = "f focus", description = "Focus an entire factions", permission = "")
    public static void factionFocus(Player sender, @Param(name = "team") final Team targetTeam) {
        Team senderTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender.getUniqueId());

        if (senderTeam == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction!");
            return;
        }

        if (senderTeam == targetTeam) {
            sender.sendMessage(ChatColor.RED + "You cannot focus your own faction!");
            return;
        }

        senderTeam.setFocusedTeam(targetTeam);
        senderTeam.sendMessage(ChatColor.LIGHT_PURPLE + targetTeam.getName() + ChatColor.YELLOW + " faction has been focused by " + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + ".");

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (senderTeam.isMember(onlinePlayer.getUniqueId())) {
                FrozenNametagHandler.reloadOthersFor(onlinePlayer);
            }
        }
    }

    @Command(names = "f unfocus", description = "Remove the faction focus", permission = "")
    public static void factionUnfocus(Player sender) {
        Team senderTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender.getUniqueId());

        if (senderTeam == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction!");
            return;
        }

        Team focused = senderTeam.getFocusedTeam();
        if (focused == null) {
            sender.sendMessage(ChatColor.RED + "You are not focusing a faction!");
            return;
        }

        senderTeam.setFocusedTeam(null);
        senderTeam.sendMessage(ChatColor.LIGHT_PURPLE + focused.getName() + ChatColor.YELLOW + " faction has been unfocused by " + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + ".");

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (senderTeam.isMember(onlinePlayer.getUniqueId())) {
                FrozenNametagHandler.reloadOthersFor(onlinePlayer);
            }
        }
    }

}