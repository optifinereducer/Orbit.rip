package rip.warzone.anticheat.command;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.AntiCheat;

public class StaffAlertsCommand {


    @Command(names={"alerts", "ac alerts"}, permission="anticheat.alerts")
    public static void execute(Player sender) {
        AntiCheat.instance.getAlertsManager().toggleAlerts(sender);
        AntiCheat.instance.getPlayerDataManager().getPlayerData(sender).staffalerts=!AntiCheat.instance.getPlayerDataManager().getPlayerData(sender).staffalerts;
        sender.sendMessage(AntiCheat.instance.getAlertsManager().hasAlertsToggled(sender) ? (ChatColor.GREEN + "Subscribed to Riot alerts.") : (ChatColor.RED + "Unsubscribed from Riot alerts."));
    }
}
