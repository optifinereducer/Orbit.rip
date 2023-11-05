package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamHQCommand {

    @Command(names = {"team hq", "t hq", "f hq", "faction hq", "fac hq", "team home", "t home", "f home", "faction home", "fac home", "home", "hq"}, permission = "")
    public static void teamHQ(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.DARK_AQUA + "You are not in a faction!");
            return;
        }

        if (team.getHQ() == null) {
            sender.sendMessage(ChatColor.RED + "HQ not set.");
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isEOTW()) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your faction headquarters during the End of the World!");
            return;
        }

        if (sender.hasMetadata("frozen")) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your faction headquarters while you're frozen!");
            return;
        }

        if (Foxtrot.getInstance().getInDuelPredicate().test(sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to HQ during a duel!");
            return;
        }

        if (Foxtrot.getInstance().getMapHandler().getGameHandler() != null
                && Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()
                && Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame().isPlayingOrSpectating(sender.getUniqueId())
        ) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to HQ during a game!");
            return;
        }

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Use /pvp enable to toggle your PvP Timer off!");
            return;
        }

        Team locationTeam = LandBoard.getInstance().getTeam(sender.getLocation());
        boolean enemyClaim = team != locationTeam;
        if (locationTeam == null || Foxtrot.getInstance().getServerHandler().isWarzone(sender.getLocation()) || DTRBitmask.ROAD.appliesAt(sender.getLocation())) {
            enemyClaim = false;
        }
        Foxtrot.getInstance().getServerHandler().beginHQWarp(sender, team, enemyClaim ? 15 : 10, false);
    }
}