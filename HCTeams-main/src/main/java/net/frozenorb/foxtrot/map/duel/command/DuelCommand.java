package net.frozenorb.foxtrot.map.duel.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.duel.DuelHandler;
import net.frozenorb.foxtrot.map.duel.menu.SelectWagerMenu;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DuelCommand {

    @Command(names = { "duel" }, permission = "")
    public static void duel(Player sender, @Param(name = "player") Player target) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "This command is only available on KitMap!");
            return;
        }

        DuelHandler duelHandler = Foxtrot.getInstance().getMapHandler().getDuelHandler();

        if (!duelHandler.canDuel(sender, target)) {
            return;
        }

        new SelectWagerMenu(wager -> {
            sender.closeInventory();
            duelHandler.sendDuelRequest(sender, target, wager);
        }).openMenu(sender);
    }

}
