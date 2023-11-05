package net.frozenorb.foxtrot.reclaim.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.stream.Collectors;

public class ReclaimCommand {

    @Command(names = { "donatorreclaim", "donorreclaim", "reclaim" }, description = "Reclaim your donator perks", permission = "")
    public static void execute(Player player) {
        if (Foxtrot.getInstance().getReclaimHandler().getHasReclaimed().contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED.toString() + "You've already reclaimed your donator perks!");
            return;
        }

        Configuration config = Foxtrot.getInstance().getConfig();

        for (String key : config.getConfigurationSection("reclaims").getKeys(false).stream().sorted(Comparator.comparingInt(key -> (int) config.getLong("reclaims." + key + ".priority", 99L))).collect(Collectors.toList())) {
            if (player.hasPermission(config.getString("reclaims." + key + ".permission"))) {
                Foxtrot.getInstance().getReclaimHandler().getHasReclaimed().add(player.getUniqueId());

                for (String command : config.getStringList("reclaims." + key + ".commands")) {
                    try {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()).replace("%uuid%", player.getUniqueId().toString()));
                    } catch (Exception e) {
                        Foxtrot.getInstance().getLogger().severe("[Reclaims] Failed to execute command: " + command + " for player " + player.getName());
                        e.printStackTrace();
                    }
                }

                player.sendMessage(ChatColor.GREEN.toString() + "You've reclaimed your " + ChatColor.BOLD + key + ChatColor.GREEN + " donator perks!");
                return;
            }
        }

        player.sendMessage(ChatColor.RED + "You have nothing to reclaim!");
    }

}
