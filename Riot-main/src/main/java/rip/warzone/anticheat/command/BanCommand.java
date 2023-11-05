package rip.warzone.anticheat.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.AntiCheat;
import rip.warzone.anticheat.event.PlayerBanEvent;
import rip.warzone.anticheat.player.PlayerData;

public class BanCommand {

    @Command(names={"ac ban"}, permission="op")
    public static void execute(CommandSender player, @Param(name="target") Player target) {
        PlayerData playerData= AntiCheat.instance.getPlayerDataManager().getPlayerData(target);
        playerData.setBanning(true);

        PlayerBanEvent event=new PlayerBanEvent(target, "Manually banned by " + player.getName());
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

}