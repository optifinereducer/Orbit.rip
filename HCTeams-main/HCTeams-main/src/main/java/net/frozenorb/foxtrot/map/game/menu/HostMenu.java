package net.frozenorb.foxtrot.map.game.menu;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.game.Game;
import net.frozenorb.foxtrot.map.game.GameType;
import net.frozenorb.foxtrot.util.Formats;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Host an Event";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (GameType gameType : GameType.values()) {
            buttons.put(buttons.size(), new GameTypeButton(gameType));
        }

        return buttons;
    }

    @AllArgsConstructor
    private class GameTypeButton extends Button {
        private final GameType gameType;

        @Override
        public String getName(Player player) {
            return ChatColor.GOLD.toString() + ChatColor.BOLD + gameType.getDisplayName();
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> description = new ArrayList<>();
            description.add("");
            description.addAll(Formats.renderLines(ChatColor.GRAY.toString(), gameType.getDescription()));
            description.add("");

            if (gameType.isDisabled()) {
                description.add(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "DISABLED");
                description.add(ChatColor.DARK_RED + "This event has been disabled!");
            } else if (gameType.canHost(player)) {
                description.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "UNLOCKED");
                description.add(ChatColor.GREEN + "Click to host this event!");
            } else {
                description.add(ChatColor.RED.toString() + "LOCKED");
                description.add(ChatColor.RED + "Purchase a rank to unlock access");
                description.add(ChatColor.RED + "to this event!");
                description.add("");
                description.add(ChatColor.LIGHT_PURPLE + "store.warzone.rip");
            }

            return description;
        }

        @Override
        public Material getMaterial(Player player) {
            return gameType.getIcon().getType();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (clickType.isLeftClick()) {
                if (gameType.isDisabled()) {
                    player.sendMessage(ChatColor.RED + "That event is temporarily disabled!");
                    return;
                }
                if (!gameType.canHost(player)) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to host " + gameType.getDisplayName() + " events.");
                    return;
                }

                if (!Foxtrot.getInstance().getMapHandler().getGameHandler().canStartGame(player, gameType)) {
                    return;
                }

                try {
                    player.closeInventory();

                    Game game = Foxtrot.getInstance().getMapHandler().getGameHandler().startGame(player, gameType);
                    game.addPlayer(player);

                    player.sendMessage(ChatColor.GREEN + "Started " + gameType.getDisplayName() + "! You can use /start to forcefully start the event.");
                } catch (IllegalStateException e) {
                    player.sendMessage(ChatColor.RED.toString() + e.getMessage());
                }
            }
        }
    }

}
