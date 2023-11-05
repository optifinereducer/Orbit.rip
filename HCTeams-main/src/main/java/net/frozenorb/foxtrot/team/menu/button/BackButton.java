package net.frozenorb.foxtrot.team.menu.button;

import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

public class BackButton extends Button {

    private Menu back;

    public Material getMaterial(Player player) {
        return Material.BED;
    }

    public byte getDamageValue(Player player) {
        return 0;
    }

    public String getName(Player player) {
        return back == null ? "§cClose" : "§cGo back";
    }

    public List<String> getDescription(Player player) {
        return new ArrayList();
    }

    public void clicked(Player player, int i, ClickType clickType) {
        Button.playNeutral(player);
        if(back != null) {
            this.back.openMenu(player);
        }else{
            player.closeInventory();
        }
    }

    public BackButton(Menu back) {
        this.back = back;
    }

    public BackButton() {
    }

}
