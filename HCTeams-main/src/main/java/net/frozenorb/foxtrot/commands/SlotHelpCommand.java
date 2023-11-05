package net.frozenorb.foxtrot.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SlotHelpCommand {

    @Command(
            names = { "slothelp" },
            description = "Sets each slot of your inventory to an item with an amount of the slot number",
            permission = "op"
    )
    public static void execute(Player player) {
        for (int i = 0; i < 36; i++) {
            player.getInventory().setItem(i, new ItemStack(Material.STONE, i));
        }
        player.updateInventory();
    }

}
