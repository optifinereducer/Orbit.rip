package net.frozenorb.foxtrot.map.game.command;

import net.frozenorb.foxtrot.map.game.menu.HostMenu;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HostCommand {

    @Command(names = { "host", "game host" }, description = "Host a KitMap Event", permission = "", async = true)
    public static void execute(Player player) {
        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You can't host an event while spawn-tagged!");
            return;
        }

        new HostMenu().openMenu(player);
    }

}
