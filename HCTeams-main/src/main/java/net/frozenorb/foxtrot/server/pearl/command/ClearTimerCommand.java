package net.frozenorb.foxtrot.server.pearl.command;

import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.entity.Player;

public class ClearTimerCommand {

    @Command(names = { "enderpearl remove" }, description = "Clears your enderpearl timer", permission = "op")
    public static void execute(Player player, @Param(name = "player", defaultValue = "self") Player target) {
        EnderpearlCooldownHandler.clearEnderpearlTimer(target);
        player.sendMessage(CC.GOLD + "Cleared Enderpearl timer for " + target.getName() + CC.GOLD + ".");
    }

}
