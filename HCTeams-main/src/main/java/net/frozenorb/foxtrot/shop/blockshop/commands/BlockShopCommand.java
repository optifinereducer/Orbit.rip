package net.frozenorb.foxtrot.shop.blockshop.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.shop.blockshop.BlockShopHandler;
import net.frozenorb.foxtrot.shop.blockshop.menu.BlockShopMenu;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by vape on 10/30/2020 at 3:40 PM.
 */
public class BlockShopCommand {

    private static final BlockShopHandler HANDLER = Foxtrot.getInstance().getMapHandler().getBlockShopHandler();

    @Command(names = {"blockshop", "bs"}, permission = "")
    public static void blockShop(Player player) {
        if (!player.hasPermission("foxtrot.admin") && !HANDLER.isEnabled()) {
            player.sendMessage(ChatColor.RED + "/blockshop is currently disabled.");
            return;
        }

        new BlockShopMenu(HANDLER).openMenu(player);
    }

}