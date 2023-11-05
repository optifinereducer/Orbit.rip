package net.frozenorb.foxtrot.lunar;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.*;

public class LunarWaypointTask implements Runnable {

    @Override
    public void run() {
        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            for (Player online : team.getOnlineMembers()) {
                Location rally = team.getRallyPlayer() == null ? online.getLocation() : team.getRallyPlayer().getLocation();
                Location focusedHq = team.getFocusedTeam() == null
                        ? online.getLocation()
                        : team.getFocusedTeam().getHQ() == null
                        ? online.getLocation()
                        : team.getFocusedTeam().getHQ();

                LCWaypoint rallyWaypoint = new LCWaypoint("Faction Rally", rally, Color.CYAN.getRGB(), false, true);
                LCWaypoint focusedWaypoint = new LCWaypoint("Focused Home", focusedHq, Color.RED.getRGB(), false, true);

                LunarClientAPI.getInstance().removeWaypoint(online, rallyWaypoint);
                LunarClientAPI.getInstance().removeWaypoint(online, focusedWaypoint);

                if (team.getRallyPlayer() != null && team.isMember(team.getRallyPlayer().getUniqueId())) {
                    LunarClientAPI.getInstance().sendWaypoint(online, rallyWaypoint);
                }

                if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null) {
                    LunarClientAPI.getInstance().sendWaypoint(online, focusedWaypoint);
                }
            }
        }
    }
}
