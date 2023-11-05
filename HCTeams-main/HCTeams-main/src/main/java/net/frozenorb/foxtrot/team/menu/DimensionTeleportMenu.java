package net.frozenorb.foxtrot.team.menu;

import net.frozenorb.foxtrot.dimension.AbstractDimension;
import net.frozenorb.foxtrot.listener.DimensionListener;
import net.frozenorb.foxtrot.team.menu.button.BackButton;
import net.frozenorb.foxtrot.team.menu.button.DimensionTeleportButton;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.pagination.PaginatedMenu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DimensionTeleportMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Dimension Teleporter";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(4, new BackButton());
        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;
        for(AbstractDimension dimension : DimensionListener.getDimensions()){
            buttons.put(index++, new DimensionTeleportButton(dimension));
        }
        return buttons;
    }

}
