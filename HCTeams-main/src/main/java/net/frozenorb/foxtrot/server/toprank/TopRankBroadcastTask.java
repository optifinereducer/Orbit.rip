package net.frozenorb.foxtrot.server.toprank;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TopRankBroadcastTask extends BukkitRunnable {

    private static final String TOP_RANK_ID = "valor";

    @Override
    public void run() {
        List<Player> onlineTopRanks = new ArrayList<>();

/*        for (Player player : Bukkit.getOnlinePlayers()) {
            BukkitProfile profile = (BukkitProfile) StarkCore.instance.getProfileHandler().getByUUID(player.getUniqueId());
            if (profile != null) {
                if (profile.getRank().getId().equalsIgnoreCase(TOP_RANK_ID)) {
                    onlineTopRanks.add(player);
                }
            }
        }*/

        if (!onlineTopRanks.isEmpty()) {
            StringBuilder builder = new StringBuilder(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Online Valors: ");

            for (Player player : onlineTopRanks) {
                builder.append(ChatColor.WHITE).append(player.getName()).append(ChatColor.GRAY).append(", ");
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("");
                player.sendMessage(builder.substring(0, builder.length() - 2));
                player.sendMessage("");
            }
        }
    }

}
