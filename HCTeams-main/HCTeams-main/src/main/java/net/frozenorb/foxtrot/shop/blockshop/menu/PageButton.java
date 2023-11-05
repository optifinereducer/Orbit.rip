package net.frozenorb.foxtrot.shop.blockshop.menu;

import lombok.RequiredArgsConstructor;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PageButton extends Button {
    private final int mod;
    private final BlockShopMenu menu;

    public void clicked(Player player, int i, ClickType clickType) {
        if (this.hasNext()) {
            this.menu.changePage(mod);
            Button.playNeutral(player);
        } else {
            Button.playFail(player);
        }

    }

    private boolean hasNext() {
        int pg = this.menu.getCurrentPage() + this.mod;
        return pg > 0 && this.menu.getTotalPages() >= pg;
    }

    public String getName(Player player) {
        if (!this.hasNext()) {
            return this.mod > 0 ? "§7Last page" : "§7First page";
        } else {
            return this.mod > 0 ? "§a⟶" : "§c⟵";
        }
    }

    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    public byte getDamageValue(Player player) {
        return (byte) (this.hasNext() ? 11 : 7);
    }

    public Material getMaterial(Player player) {
        return Material.CARPET;
    }
}