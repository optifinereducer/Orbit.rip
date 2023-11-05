package net.frozenorb.foxtrot.lunar;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketCooldown;
import com.lunarclient.bukkitapi.nethandler.shared.LCPacketWaypointAdd;
import com.lunarclient.bukkitapi.nethandler.shared.LCPacketWaypointRemove;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by vape on 10/31/2020 at 3:44 PM.
 */
public class LunarHandler {

    @Getter
    private final boolean supported;

    public LunarHandler(JavaPlugin plugin) {
        supported = Bukkit.getPluginManager().getPlugin("LunarClientAPI") != null;

        if (supported) {
            Bukkit.getPluginManager().registerEvents(new LunarListener(), plugin);
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new LunarWaypointTask(), 0, 0);
        }
    }

    public void sendCooldown(Player player, String name, int seconds, Material material) {
        if (!supported) return;
        LunarClientAPI.getInstance().sendPacket(player, new LCPacketCooldown(name, seconds * 1000L, material.getId()));
        /*LunarClientAPI.getInstance().sendCooldown(player, new LCCooldown(
                name, seconds, TimeUnit.SECONDS, material
        ));*/
    }
}