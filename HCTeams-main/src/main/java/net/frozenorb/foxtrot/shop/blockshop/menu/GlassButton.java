package net.frozenorb.foxtrot.shop.blockshop.menu;

import lombok.RequiredArgsConstructor;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@RequiredArgsConstructor
public class GlassButton extends Button {

    private final int glassData;

    @Override
    public ItemStack getButtonItem(Player player) {
        return ItemBuilder.of(Material.STAINED_GLASS_PANE)
                .name(" ")
                .data((short) glassData)
                .build();
    }

    @Override
    public String getName(Player player) {
        return null;
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return null;
    }
}