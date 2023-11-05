package net.frozenorb.foxtrot.team;

import net.frozenorb.foxtrot.team.button.claimbuildbuttons.CobbleStoneButton;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ClaimBuildMenu extends Menu {
    public String getTitle(final Player player) {
        return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Team Build";
    }

    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons = new HashMap<Integer, Button>();
        buttons.put(13, new CobbleStoneButton());
        return buttons;
    }
}
