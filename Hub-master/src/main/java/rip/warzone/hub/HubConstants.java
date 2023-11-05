package rip.warzone.hub;

import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class HubConstants {

    public static final String VERTICAL_LINE = "⎜";
    public static final String SCOREBOARD_TITLE = "&cWarzone &7" + VERTICAL_LINE + " &f%server_name%".replace("%server_name%", Bukkit.getServerName());
    public static final ItemStack COMPASS_ITEM = ItemBuilder.of(Material.COMPASS).name(ChatColor.RED + "Server Selector").build();
    public static ItemStack ENDERPEARL = ItemBuilder.of(Material.ENDER_PEARL).name(ChatColor.DARK_PURPLE + "Enderpearl").build();
    public static ItemStack ARMOR_SELECTOR = ItemBuilder.of(Material.CHEST).name(ChatColor.GOLD + "Armor Selector").build();

}
