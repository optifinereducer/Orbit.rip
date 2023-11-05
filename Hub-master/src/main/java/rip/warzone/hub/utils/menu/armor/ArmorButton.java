package rip.warzone.hub.utils.menu.armor;

import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.profile.Profile;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.warzone.hub.Hub;
import rip.warzone.hub.armor.Armor;

import java.util.List;

public class ArmorButton extends Button {

    private final Armor armor;

    public ArmorButton(Armor armor){
        this.armor = armor;
    }

    @Override
    public String getName(Player player) {
        return "";
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return armor == null ? Material.REDSTONE : Material.LEATHER_CHESTPLATE;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        if(armor == null) return ItemBuilder.of(Material.REDSTONE).name(ChatColor.RED + "Remove Active Armor Cosmetic").build();
        return ItemBuilder.of(Material.LEATHER_CHESTPLATE).color(armor.getArmorColor()).name(ChatColor.translateAlternateColorCodes('&', armor.getTextColor() + armor.getDisplayName())).addToLore(Hub.getInstance().getArmorManager().getActiveArmor(player) != null && Hub.getInstance().getArmorManager().getActiveArmor(player).getName().equalsIgnoreCase(armor.getName()) ? ChatColor.RED + "Click to unequip this armor cosmetic." : ChatColor.GREEN + "Click to equip this armor cosmetic.").build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        Profile profile = Hydrogen.getInstance().getProfileHandler().getProfile(player.getUniqueId()).get();
        if(armor == null){
            Hub.getInstance().getArmorManager().setActiveArmor(player, null, Hub.getInstance().getArmorManager().isGlint(player));
            player.sendMessage(ChatColor.GREEN + "You have removed your active armor cosmetic.");
            return;
        }
        if(!profile.getPermissions().containsKey(armor.getPermission())){
            player.sendMessage(ChatColor.RED + "You do not have permission to equip this armor cosmetic! Purchase it on " + ChatColor.BOLD + "https://store.warzone.rip" + ChatColor.RED + ".");
            return;
        }
        if(Hub.getInstance().getArmorManager().getActiveArmor(player) != null && armor.getName().equalsIgnoreCase(Hub.getInstance().getArmorManager().getActiveArmor(player).getName())){
            Hub.getInstance().getArmorManager().setActiveArmor(player, null, Hub.getInstance().getArmorManager().isGlint(player));
            player.sendMessage(ChatColor.GREEN + "You have removed your active armor cosmetic.");
            return;
        }
        Hub.getInstance().getArmorManager().setActiveArmor(player, armor, Hub.getInstance().getArmorManager().isGlint(player));
        player.sendMessage(ChatColor.GREEN + "Successfully equipped the " + ChatColor.translateAlternateColorCodes('&', armor.getTextColor() + armor.getDisplayName()) + ChatColor.GREEN + " armor cosmetic!");
    }
}
