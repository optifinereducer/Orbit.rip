package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamRallyCommand {

    @Command(names = {"team rally", "t rally", "faction rally", "fac rally", "f rally"}, permission = "")
    public static void teamRally(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.getRallyPlayer() != null) {
            team.setRallyPlayer(null);
            team.sendMessage(ChatColor.RED + "Team rally has been disabled.");
        } else {
            team.setRallyPlayer(sender);
            team.sendMessage(ChatColor.GREEN + "Team rally has been enabled.");
        }
    }
}
