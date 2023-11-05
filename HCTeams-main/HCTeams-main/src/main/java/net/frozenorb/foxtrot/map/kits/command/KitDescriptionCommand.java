package net.frozenorb.foxtrot.map.kits.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.kits.DefaultKit;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitDescriptionCommand {

    @Command(names = { "managekit setdesc" }, description = "Sets the description of a kit", permission = "op")
    public static void execute(Player player, @Param(name = "kit") DefaultKit kit, @Param(name = "description", wildcard = true) String description) {
        kit.setDescription(description);
        Foxtrot.getInstance().getMapHandler().getKitManager().saveDefaultKits();

        player.sendMessage(ChatColor.GREEN + "Set description of " + kit.getName() + "!");
    }

}
