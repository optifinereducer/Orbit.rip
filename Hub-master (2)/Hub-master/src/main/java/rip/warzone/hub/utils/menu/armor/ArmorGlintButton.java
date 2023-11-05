package rip.warzone.hub.utils.menu.armor;

import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.warzone.hub.Hub;

import java.util.List;

public class ArmorGlintButton extends Button {

    @Override
    public String getName(Player player) {
        return (Hub.getInstance().getArmorManager().isGlint(player) ? ChatColor.RED + "Toggle off glint" : ChatColor.GREEN + "Toggle on glint");
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.INK_SACK;
    }

    @Override
    public byte getDamageValue(Player player) {
        return Hub.getInstance().getArmorManager().isGlint(player) ? DyeColor.LIME.getDyeData() : DyeColor.GRAY.getDyeData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        Hub.getInstance().getArmorManager().setActiveArmor(player, Hub.getInstance().getArmorManager().getActiveArmor(player), !Hub.getInstance().getArmorManager().isGlint(player));
        player.updateInventory();
    }
}
