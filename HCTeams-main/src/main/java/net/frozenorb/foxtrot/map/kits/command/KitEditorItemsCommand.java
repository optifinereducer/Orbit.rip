package net.frozenorb.foxtrot.map.kits.command;

import net.frozenorb.foxtrot.map.kits.DefaultKit;
import net.frozenorb.foxtrot.map.kits.editor.setup.KitEditorItemsMenu;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.entity.Player;

public class KitEditorItemsCommand {

    @Command(names = { "managekit editoritems" }, description = "Edit a kit's editor items", permission = "op")
    public static void execute(Player player, @Param(name = "kit") DefaultKit kit) {
        new KitEditorItemsMenu(kit).openMenu(player);
    }

}
