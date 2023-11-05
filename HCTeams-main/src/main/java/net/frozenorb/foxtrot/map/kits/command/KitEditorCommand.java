package net.frozenorb.foxtrot.map.kits.command;

import net.frozenorb.foxtrot.map.kits.editor.menu.KitsMenu;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitEditorCommand {

    @Command(names = { "managekit editor" }, description = "Opens the Kit Editor", permission = "")
    public static void execute(Player player) {
        if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            player.sendMessage(ChatColor.RED + "You can only open the Kit Editor while in Spawn!");
            return;
        }

        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You can't open the Kit Editor while spawn-tagged!");
            return;
        }

        new KitsMenu().openMenu(player);
    }

}
