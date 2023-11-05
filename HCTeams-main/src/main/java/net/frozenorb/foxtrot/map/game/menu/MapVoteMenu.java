package net.frozenorb.foxtrot.map.game.menu;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.game.Game;
import net.frozenorb.foxtrot.map.game.arena.GameArena;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class MapVoteMenu extends Menu {

    private Game game;

    @Override
    public String getTitle(Player player) {
        return "Map Votes";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (Map.Entry<GameArena, AtomicInteger> entry : game.getArenaOptions().entrySet()) {
            buttons.put(buttons.size(), new MapButton(entry.getKey(), entry.getValue()));
        }

        return buttons;
    }

    @AllArgsConstructor
    private class MapButton extends Button {
        private GameArena arena;
        private AtomicInteger votes;

        @Override
        public String getName(Player player) {
            return ChatColor.YELLOW.toString() + ChatColor.BOLD + arena.getName();
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> description = new ArrayList<>();
            description.add(ChatColor.GRAY.toString() + "This map has " + ChatColor.GREEN + votes.get() + ChatColor.GRAY + " votes.");

            if (game.getPlayerVotes().containsKey(player.getUniqueId()) && game.getPlayerVotes().get(player.getUniqueId()) == arena) {
                description.add(ChatColor.GRAY.toString() + "You voted for this map!");
            }

            return description;
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.EMPTY_MAP;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {

        }
    }

}