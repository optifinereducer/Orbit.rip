package rip.warzone.hub.scoreboard;

import me.clip.placeholderapi.PlaceholderAPI;
import me.joeleoli.portal.shared.queue.Queue;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.profile.Profile;
import net.frozenorb.qlib.scoreboard.ScoreGetter;
import net.frozenorb.qlib.util.LinkedList;
import org.bukkit.entity.Player;

public class HubScoreGetter implements ScoreGetter {

    @Override
    public void getScores(LinkedList<String> scores, Player player) {
        Profile profile = Hydrogen.getInstance().getProfileHandler().getProfile(player.getUniqueId()).get();

        Queue queue = Queue.getByPlayer(player.getUniqueId());

        String online = PlaceholderAPI.setPlaceholders(player, "%bungee_total%");
        scores.add("&7&m----------------------");
        scores.add("&4Online&7:");
        scores.add("&f" + online);
        scores.add("&r");
        scores.add("&4Rank&7:");
        scores.add("&f" + profile.getBestDisplayRank().getGameColor() + profile.getBestDisplayRank().getDisplayName());
        if(queue != null){
            scores.add("&9&3&e");
            scores.add("&4Queue&7:");
            scores.add("&c" + queue.getName());
            scores.add("Position " + queue.getPosition(player.getUniqueId()) + "/" + queue.getPlayers().size());
        }
        scores.add("&e&a&e");
        scores.add("&7warzone.rip");
        scores.add("&7&m------------&b&r&7&m----------");
    }

}
