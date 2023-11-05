package net.frozenorb.foxtrot.map.kits.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.kits.DefaultKit;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitIconCommand {

    @Command(names = { "managekit seticon" }, description = "Sets the icon of a kit", permission = "op")
    public static void execute(Player player, @Param(name = "kit") DefaultKit kit) {
        if (player.getItemInHand() == null) {
            player.sendMessage(ChatColor.RED + "You have no item in your hand!");
            return;
        }

        kit.setIcon(player.getItemInHand());
        Foxtrot.getInstance().getMapHandler().getKitManager().saveDefaultKits();

        player.sendMessage(ChatColor.GREEN + "Set icon of " + kit.getName() + "!");
    }

}
