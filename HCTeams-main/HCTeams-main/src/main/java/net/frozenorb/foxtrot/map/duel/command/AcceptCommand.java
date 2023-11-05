package net.frozenorb.foxtrot.map.duel.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.duel.DuelHandler;
import net.frozenorb.foxtrot.map.duel.DuelInvite;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AcceptCommand {

    @Command(names = { "accept" }, permission = "")
    public static void accept(Player sender, @Param(name = "player") Player target) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "This command is only available on KitMap!");
            return;
        }

        DuelHandler duelHandler = Foxtrot.getInstance().getMapHandler().getDuelHandler();

        if (!duelHandler.canAccept(sender, target)) {
            return;
        }

        DuelInvite invite = duelHandler.getInvite(target.getUniqueId(), sender.getUniqueId());
        duelHandler.acceptDuelRequest(invite);
    }

}
