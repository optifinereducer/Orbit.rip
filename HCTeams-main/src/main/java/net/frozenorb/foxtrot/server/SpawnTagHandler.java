package net.frozenorb.foxtrot.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class SpawnTagHandler {

    @Getter private static Map<String, Long> spawnTags = new ConcurrentHashMap<>();

    public static void removeTag(Player player) {
        Foxtrot.getInstance().getLunarHandler().sendCooldown(player, "SpawnTag", 0, Material.DIAMOND_SWORD);
        spawnTags.remove(player.getName());
    }

    public static void addOffensiveSeconds(Player player, int seconds) {
        addSeconds(player, seconds);
    }

    public static void addPassiveSeconds(Player player, int seconds) {
        if (!Foxtrot.getInstance().getServerHandler().isPassiveTagEnabled()) {
            return;
        }

        addSeconds(player, seconds);
    }

    private static void addSeconds(Player player, int seconds) {
        if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            return;
        }

        if (Foxtrot.getInstance().getMapHandler().getGameHandler() != null) {
            if (Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame() && Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame().isPlayingOrSpectating(player.getUniqueId())) {
                return;
            }
        }

        if (Foxtrot.getInstance().getMapHandler().getGameHandler() != null &&
                Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame() &&
                Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame().isPlayingOrSpectating(player.getUniqueId())) {
            return;
        }

        int duration = seconds;

        if (isTagged(player)) {
            int secondsTaggedFor = (int) ((spawnTags.get(player.getName()) - System.currentTimeMillis()) / 1000L);
            duration = Math.min(secondsTaggedFor + seconds, getMaxTagTime());
        } else {
            player.sendMessage(ChatColor.YELLOW + "You have been spawn-tagged for §c" + seconds + " §eseconds!");
        }

        Foxtrot.getInstance().getLunarHandler().sendCooldown(player, "SpawnTag", duration, Material.DIAMOND_SWORD);
        spawnTags.put(player.getName(), System.currentTimeMillis() + (duration * 1000L));
    }

    public static long getTag(Player player) {
        return (spawnTags.get(player.getName()) - System.currentTimeMillis());
    }

    public static boolean isTagged(Player player) {
        if (player != null) {
            return spawnTags.containsKey(player.getName()) && spawnTags.get(player.getName()) > System.currentTimeMillis();
        } else {
            return false;
        }
    }

    public static int getMaxTagTime() {
        if (Foxtrot.getInstance().getServerHandler().isHardcore()) {
            return 45;
        }

        return Foxtrot.getInstance().getServerHandler().isPassiveTagEnabled() ? 45 : 60;
    }

}