package rip.warzone.anticheat.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.AntiCheat;
import rip.warzone.anticheat.player.PlayerData;

public class ClientCommand {

    @Command(names={"ac client", "client"}, permission="sprite.staff")
    public static void execute(CommandSender sender, @Param(name="target") Player target) {
        PlayerData targetData= AntiCheat.instance.getPlayerDataManager().getPlayerData(target);

        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + "------------------------------");
        sender.sendMessage(ChatColor.YELLOW + "AC client lookup for " + target.getDisplayName());
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "Player Information:");
        sender.sendMessage(ChatColor.GRAY + " Client Brand: " + ChatColor.RED + targetData.getClient().getName());
        sender.sendMessage(ChatColor.GRAY + " Client Version: " + ChatColor.RED + getVersion((CraftPlayer) target));
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "Statistics:");
        sender.sendMessage(ChatColor.GRAY + " Average Ping: " + targetData.getPing());
        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + "------------------------------");
    }

    private static String getVersion(CraftPlayer player) {
        int i=player.getHandle().playerConnection.networkManager.getVersion();
        return i == 5 ? "1.7.10" : (i == 47 ? "1.8" : "N/A");
    }
}
