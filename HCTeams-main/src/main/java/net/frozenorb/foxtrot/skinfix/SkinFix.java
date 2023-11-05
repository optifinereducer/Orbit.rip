package net.frozenorb.foxtrot.skinfix;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import net.frozenorb.foxtrot.Foxtrot;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class SkinFix extends PacketAdapter implements Listener {

    /*
        private int action;
        private GameProfile player;
        private int gamemode;
        private int ping;
        private String username;
     */

    private final Map<UUID, Set<UUID>> sentInfo = new HashMap<>();

    public SkinFix() {
        super(Foxtrot.getInstance(), PacketType.Play.Server.PLAYER_INFO);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        int action = event.getPacket().getIntegers().read(0);
        WrappedGameProfile updated = event.getPacket().getGameProfiles().read(0);

        if (action == 0) { // add
            Player updatedPlayer = Bukkit.getPlayer(updated.getUUID());
            if (updatedPlayer == null) {
                return;
            }

            if (!sentInfo.containsKey(event.getPlayer().getUniqueId())) {
                sentInfo.put(event.getPlayer().getUniqueId(), new HashSet<>());
            }

            Set<UUID> sent = sentInfo.get(event.getPlayer().getUniqueId());

            if (sent.add(updated.getUUID())) {
                Bukkit.getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> sendUpdate(event.getPlayer(), updatedPlayer), 10L);
            }
        } else if (action == 4) { // remove
            if (sentInfo.containsKey(event.getPlayer().getUniqueId())) {
                sentInfo.get(event.getPlayer().getUniqueId()).remove(updated.getUUID());
            }
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Bukkit.getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> sendUpdate(event.getPlayer(), event.getPlayer()), 10L);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        sentInfo.remove(event.getPlayer().getUniqueId());
    }

    public void sendUpdate(Player viewer, Player player) {
        PacketPlayOutPlayerInfo remove = PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer) player).getHandle());
        PacketPlayOutPlayerInfo add = PacketPlayOutPlayerInfo.addPlayer(((CraftPlayer) player).getHandle());

        ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(remove);
        ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(add);
    }

}