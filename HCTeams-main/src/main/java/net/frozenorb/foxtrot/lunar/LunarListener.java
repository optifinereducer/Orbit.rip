package net.frozenorb.foxtrot.lunar;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.ByteBufWrapper;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketServerRule;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketTeammates;
import com.lunarclient.bukkitapi.nethandler.client.obj.ServerRule;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import io.netty.buffer.Unpooled;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by vape on 10/31/2020 at 3:36 PM.
 */
public class LunarListener implements Listener {

    private final Location spawn;

    public LunarListener() {
        this.spawn = Bukkit.getWorlds().get(0).getSpawnLocation().add(0.5, 0, 0.5);

        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(Foxtrot.getInstance(), () -> {
            Bukkit.getServer().getOnlinePlayers().forEach(target -> {
                if(Foxtrot.getInstance().getTeamHandler().getTeam(target) != null){
                    sendTeammates(target, Foxtrot.getInstance().getTeamHandler().getTeam(target));
                }
            });
        }, 0L, 1L);
    }

    private void sendTeammates(Player player, Team team){
        Bukkit.getServer().getOnlinePlayers().forEach(target -> {
            for(Player player1 : team.getOnlineMembers()){
                Map<UUID, Map<String, Double>> locationMap = new HashMap<>();
                Map<String, Double> locationInMap = new HashMap<>();
                locationInMap.put(player.getName(), player.getLocation().distance(player1.getLocation()));
                locationMap.put(player.getUniqueId(), locationInMap);
                LunarClientAPI.getInstance().sendPacket(player1, new LCPacketTeammates(player.getUniqueId(), System.currentTimeMillis(), locationMap));
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Toggle server rules
        LunarClientAPI.getInstance().sendPacket(player, new LCPacketServerRule(ServerRule.SERVER_HANDLES_WAYPOINTS, true));
        sendLegacyCombat(player);
        LunarClientAPI.getInstance().sendPacket(player, new LCPacketServerRule(ServerRule.MINIMAP_STATUS, false));

        // Send spawn waypoint
        LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint("Spawn", this.spawn, Color.GREEN.getRGB(), false, true));

        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team != null && team.getHQ() != null) {
            LCWaypoint waypoint = new LCWaypoint("Faction Home", team.getHQ(), Color.GREEN.getRGB(), false, true);
            LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
        }
    }

    private void sendLegacyCombat(Player player) {
        ByteBufWrapper buf = new ByteBufWrapper(Unpooled.buffer());
        buf.writeVarInt(10);

        try {
            buf.writeString("legacyCombat");
            buf.buf().writeBoolean(true);
            buf.buf().writeInt(0);
            buf.buf().writeFloat(0);
            buf.writeString("");
        } catch (Exception ex) {
            return;
        }

        player.sendPluginMessage(LunarClientAPI.getInstance(), "lunarclient:pm", buf.buf().array());
    }

}