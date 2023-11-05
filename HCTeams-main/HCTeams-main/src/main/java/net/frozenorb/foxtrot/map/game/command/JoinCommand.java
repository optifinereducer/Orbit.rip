package net.frozenorb.foxtrot.map.game.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.game.Game;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.util.ItemUtils;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class JoinCommand {

    @Command(names = { "join", "game join" }, description = "Join an ongoing event", permission = "")
    public static void execute(Player player) {
        if (!Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            player.sendMessage(ChatColor.RED + "There is no ongoing event.");
            return;
        }

        Game ongoingGame = Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame();
        if (ongoingGame.isPlayingOrSpectating(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already in the event.");
            return;
        }

        try {
            ongoingGame.addPlayer(player);
        } catch (IllegalStateException e) {
            player.sendMessage(ChatColor.RED.toString() + e.getMessage());
        }
    }

}
