package net.frozenorb.foxtrot.map.kits.editor.setup;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.kits.DefaultKit;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class KitEditorItemsMenu extends Menu {

    private DefaultKit kit;

    @Override
    public String getTitle(Player player) {
        return "Edit " + kit.getName() + " Editor Items";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (ItemStack itemStack : kit.getEditorItems()) {
            buttons.put(buttons.size(), new EditorItemButton(itemStack));
        }

        return buttons;
    }

    @AllArgsConstructor
    private class EditorItemButton extends Button {
        private ItemStack itemStack;

        @Override
        public String getName(Player player) {
            return "";
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.AIR;
        }

        @Override
        public List<String> getDescription(Player player) {
            return ImmutableList.of();
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return itemStack.clone();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (clickType.isRightClick()) {
                kit.getEditorItems().remove(itemStack);
                Foxtrot.getInstance().getMapHandler().getGameHandler().saveConfig();
            }
        }
    }

}
