package rip.warzone.hub.utils.menu.server;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import me.joeleoli.portal.bukkit.Portal;
import me.joeleoli.portal.shared.jedis.JedisAction;
import me.joeleoli.portal.shared.queue.Queue;
import me.joeleoli.portal.shared.queue.QueueRank;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.warzone.hub.server.Server;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ServerButton extends Button {

    private final Server server;

    @Override
    public String getName(Player player) {
        return ChatColor.translateAlternateColorCodes('&', server.getDisplayName());
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>(server.getDescription());
        for (int i = 0; i < description.size(); i++) {
            description.set(i, ChatColor.translateAlternateColorCodes('&', description.get(i)));
        }
        description.add("");
        description.add(ChatColor.GREEN + "Click to join the queue!");
        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return server.getIcon();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (Queue.getByPlayer(player.getUniqueId()) != null) {
            player.sendMessage(ChatColor.RED + "You are already in a queue!");
            return;
        }
        QueueRank queueRank = Portal.getInstance().getPriorityProvider().getPriority(player);
        JsonObject rank = new JsonObject();
        rank.addProperty("name", queueRank.getName());
        rank.addProperty("priority", queueRank.getPriority());
        JsonObject playerObject = new JsonObject();
        playerObject.addProperty("uuid", player.getUniqueId().toString());
        playerObject.add("rank", rank);
        JsonObject data = new JsonObject();
        data.addProperty("queue", server.getName());
        data.add("player", playerObject);
        Portal.getInstance().getPublisher().write("portal-independent", JedisAction.ADD_PLAYER, data);
        player.sendMessage(ChatColor.GREEN + "You have joined the " + server.getName() + " queue.");
    }
}
