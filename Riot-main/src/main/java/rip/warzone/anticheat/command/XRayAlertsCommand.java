package rip.warzone.anticheat.command;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.AntiCheat;

public class XRayAlertsCommand {
    @Command(names={"xalerts", "xrayalerts", "xray"}, permission="anticheat.xray")
    public static void xrayalerts(Player sender) {
        if (!AntiCheat.instance.getAlertsManager().getAlertsToggled().contains(sender.getUniqueId())) {
            AntiCheat.instance.getAlertsManager().getAlertsToggled().add(sender.getUniqueId());
            sender.sendMessage(ChatColor.RED + "Turned X-Ray alerts off");
        } else {
            AntiCheat.instance.getAlertsManager().getAlertsToggled().remove(sender.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Turned X-Ray alerts on");
        }
    }
}