package rip.warzone.hub.utils.menu.server;

import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.warzone.hub.Hub;
import rip.warzone.hub.server.Server;

import java.util.HashMap;
import java.util.Map;

public class ServerSelectorMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Server Selector";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for(Server server : Hub.getInstance().getServerManager().getServers()){
            buttons.put(server.getGuiSlot(), new ServerButton(server));
        }
        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 3 * 9;
    }
}
