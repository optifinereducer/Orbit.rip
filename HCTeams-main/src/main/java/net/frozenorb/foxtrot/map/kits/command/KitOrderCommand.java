package net.frozenorb.foxtrot.map.kits.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.kits.DefaultKit;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitOrderCommand {

    @Command(names = { "managekit setorder" }, description = "Sets the order of a kit", permission = "op")
    public static void execute(Player player, @Param(name = "kit") DefaultKit kit, @Param(name = "order") int order) {
        kit.setOrder(order);
        Foxtrot.getInstance().getMapHandler().getKitManager().saveDefaultKits();

        player.sendMessage(ChatColor.GREEN + "Set order of " + kit.getName() + " to " + order + "!");
    }

}
