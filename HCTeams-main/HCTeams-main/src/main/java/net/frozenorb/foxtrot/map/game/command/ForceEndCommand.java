package net.frozenorb.foxtrot.map.game.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.game.Game;
import net.frozenorb.foxtrot.map.game.GameState;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceEndCommand {

    @Command(names = { "game forceend" }, description = "Force end an event", permission = "op")
    public static void execute(Player player) {
        if (!Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            player.sendMessage(ChatColor.RED.toString() + "There is no ongoing event.");
            return;
        }

        Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame().endGame();
        player.sendMessage(ChatColor.GREEN + "Successfully ended the ongoing event!");
    }

}
