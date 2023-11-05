package rip.warzone.hub.armor;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
public class Armor {

    private String name;
    private String displayName;
    private String textColor;
    private DyeColor guiColor;
    private Color armorColor;
    private String permission;

    public void apply(Player player, boolean glint){
        ItemStack helmet;
        ItemStack chestplate;
        ItemStack leggings;
        ItemStack boots;
        if(!glint) {
            helmet = ItemBuilder.of(Material.LEATHER_HELMET).color(armorColor).name(ChatColor.translateAlternateColorCodes('&', textColor + displayName + " Helmet")).build();
            chestplate = ItemBuilder.of(Material.LEATHER_CHESTPLATE).color(armorColor).name(ChatColor.translateAlternateColorCodes('&', textColor + displayName + " Chestplate")).build();
            leggings = ItemBuilder.of(Material.LEATHER_LEGGINGS).color(armorColor).name(ChatColor.translateAlternateColorCodes('&', textColor + displayName + " Leggings")).build();
            boots = ItemBuilder.of(Material.LEATHER_BOOTS).color(armorColor).name(ChatColor.translateAlternateColorCodes('&', textColor + displayName + " Boots")).build();
        }else{
            helmet = ItemBuilder.of(Material.LEATHER_HELMET).enchant(Enchantment.DURABILITY, 1).color(armorColor).name(ChatColor.translateAlternateColorCodes('&', textColor + displayName + " Helmet")).build();
            chestplate = ItemBuilder.of(Material.LEATHER_CHESTPLATE).enchant(Enchantment.DURABILITY, 1).color(armorColor).name(ChatColor.translateAlternateColorCodes('&', textColor + displayName + " Chestplate")).build();
            leggings = ItemBuilder.of(Material.LEATHER_LEGGINGS).enchant(Enchantment.DURABILITY, 1).color(armorColor).name(ChatColor.translateAlternateColorCodes('&', textColor + displayName + " Leggings")).build();
            boots = ItemBuilder.of(Material.LEATHER_BOOTS).enchant(Enchantment.DURABILITY, 1).color(armorColor).name(ChatColor.translateAlternateColorCodes('&', textColor + displayName + " Boots")).build();
        }
        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
        player.updateInventory();
    }

}
