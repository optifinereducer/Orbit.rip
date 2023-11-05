package rip.warzone.anticheat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class StaffAlertTask
extends BukkitRunnable {
    private final Map<UUID, AtomicInteger> playerPacketCounts;

    public StaffAlertTask(Map<UUID, AtomicInteger> playerPacketCounts) {
        this.playerPacketCounts = playerPacketCounts;
    }

    public void run() {
        this.playerPacketCounts.forEach((uuid, packetCount) -> {
            if (packetCount.get() > 400) {
                this.alert(uuid, packetCount.get());
            }
            packetCount.set(0);
        });
    }

    private void alert(UUID uuid, int packetCount) {
        String playerName = Bukkit.getPlayer(uuid) == null ? uuid.toString() : Bukkit.getPlayer(uuid).getName();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.hasPermission("anticheat.display.low")) continue;
            onlinePlayer.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + playerName + " is sending too many packets! (" + packetCount + " > " + 400 + ")");
        }
    }
}

