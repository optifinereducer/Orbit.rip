package net.frozenorb.foxtrot.map.kits;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;

@Getter
@Setter
public class Kit {

    private String name;
    private String original;

    private ItemStack[] inventoryContents = new ItemStack[36];
    private ItemStack[] armorContents = new ItemStack[4];

    public Kit(String name) {
        this.name = name;
    }

    public Kit(DefaultKit original) {
        if (original != null) {
            this.name = original.getName();
            this.original = original.getName();
            this.inventoryContents = Arrays.copyOf(original.getInventoryContents(), original.getInventoryContents().length);
            this.armorContents = Arrays.copyOf(original.getArmorContents(), original.getArmorContents().length);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void apply(Player player) {
        player.getInventory().setContents(inventoryContents);
        player.getInventory().setArmorContents(armorContents);

        player.updateInventory();
    }

    public void update(PlayerInventory inventory) {
        inventoryContents = inventory.getContents();
        armorContents = inventory.getArmorContents();
    }

    public int countHeals() {
        return ItemUtils.countStacksMatching(inventoryContents, ItemUtils.INSTANT_HEAL_POTION_PREDICATE);
    }

    public int countDebuffs() {
        return ItemUtils.countStacksMatching(inventoryContents, ItemUtils.DEBUFF_POTION_PREDICATE);
    }

    public int countFood() {
        return ItemUtils.countStacksMatching(inventoryContents, ItemUtils.EDIBLE_PREDICATE);
    }

    public int countPearls() {
        return ItemUtils.countStacksMatching(inventoryContents, v -> v.getType() == Material.ENDER_PEARL);
    }

    public DefaultKit getOriginal() {
        if (original == null) {
            return null;
        }

        return Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit(original);
    }

}
