package net.frozenorb.foxtrot.partner.impl.bard;

import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public final class PortableBardMenu extends Menu {

    public static final String TITLE = CC.PINK + "Select an effect";

    private final ItemStack itemStack;

    @Override
    public String getTitle(Player player) {
        return TITLE;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 10;

        for (int i = 0; i < 27; i++) {
            if (i > 9 && i < 17) continue;
            buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14));
        }
        for (PortableBardEffect effect : PortableBard.PORTABLE_BARD_EFFECTS) {
            if (index == 18) index++;
            buttons.put(index++, new EffectButton(effect));
        }
        return buttons;
    }

    @RequiredArgsConstructor
    private class EffectButton extends Button {

        private final PortableBardEffect portableBardEffect;

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            // Rowin was here - Temporary(?) fix
            if (player.getItemOnCursor().getType() != Material.AIR) {
                return;
            }

            player.closeInventory();

            if (!player.getInventory().contains(itemStack)) {
                player.sendMessage(CC.RED + "That is not allowed!");
                return;
            }

            InventoryUtils.removeAmountFromInventory(player.getInventory(), itemStack, 1);
            ItemStack itemStack = portableBardEffect.toItemStack();
            itemStack.setAmount(5);
            player.getInventory().addItem(itemStack);
        }

        @Override
        public String getName(Player player) {
            return CC.PINK + CC.BOLD + portableBardEffect.getNiceName();
        }

        @Override
        public List<String> getDescription(Player player) {
            return Arrays.asList(
                    CC.GRAY + "Right-click to apply the " + CC.UNDERLINE + portableBardEffect.getNiceName() +
                            CC.GRAY + " effect to your faction!"
            );
        }

        @Override
        public Material getMaterial(Player player) {
            return portableBardEffect.getMaterial();
        }
    }
}
