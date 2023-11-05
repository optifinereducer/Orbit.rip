package rip.warzone.anticheat.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.minecraft.server.v1_7_R4.Packet;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.AntiCheat;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.CC;

import java.util.Map;

public class PacketsCommand {

    @Command(names = {"ac packetlog"}, permission = "op")
    public static void packetLog(CommandSender sender, @Param(name = "target", defaultValue = "self") Player target){
        PlayerData data = AntiCheat.instance.getPlayerDataManager().getPlayerData(target);
        sender.sendMessage(CC.translate("&7&m---------------------------------"));
        sender.sendMessage(CC.translate("&cPackets this session for &r" + target.getDisplayName() + "&r&7:"));
        for(Map.Entry<Class<? extends Packet>, Integer> map : data.getPackets().entrySet()){
            sender.sendMessage(CC.translate(" &7* &e" + map.getKey().getSimpleName() + "&r &7(x" + map.getValue() + ")"));
        }
        sender.sendMessage(CC.translate("&7&m---------------------------------"));
    }


}
