package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.DimensionTeleportMenu;
import net.frozenorb.qlib.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamDimensionTeleportCommand {

    @Command(names = {"team dimensiontp", "faction dimensiontp", "f dimensiontp", "team dtp", "faction dtp", "f dtp"}, permission = "")
    public static void teleport(Player player){
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
        if(team == null){
            player.sendMessage(ChatColor.GRAY + "You are not in a team!");
            return;
        }
        Bukkit.getServer().getScheduler().runTask(Foxtrot.getInstance(), () -> new DimensionTeleportMenu().openMenu(player));
    }

}
