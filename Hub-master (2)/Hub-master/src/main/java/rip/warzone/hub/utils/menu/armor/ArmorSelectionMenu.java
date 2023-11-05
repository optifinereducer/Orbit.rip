package rip.warzone.hub.utils.menu.armor;

import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.pagination.PaginatedMenu;
import org.bukkit.entity.Player;
import rip.warzone.hub.Hub;
import rip.warzone.hub.armor.Armor;

import java.util.HashMap;
import java.util.Map;

public class ArmorSelectionMenu extends PaginatedMenu {

    public ArmorSelectionMenu(){
        setAutoUpdate(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Armor Selector";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(3, new ArmorButton(null));
        buttons.put(5, new ArmorGlintButton());
        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;
        for(Armor armor : Hub.getInstance().getArmorManager().getArmors()){
            buttons.put(index++, new ArmorButton(armor));
        }
        return buttons;
    }


}
