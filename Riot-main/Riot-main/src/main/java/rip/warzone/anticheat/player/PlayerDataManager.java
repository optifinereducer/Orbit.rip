package rip.warzone.anticheat.player;

import org.bukkit.entity.Player;
import rip.warzone.anticheat.AntiCheat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private AntiCheat plugin;
    private final Map<UUID, PlayerData> playerDataMap;

    public PlayerDataManager() {
        this.playerDataMap=new HashMap<UUID, PlayerData>();
    }

    public AntiCheat getPlugin() {
        return this.plugin;
    }

    public void addPlayerData(Player player) {
        this.playerDataMap.put(player.getUniqueId(), new PlayerData(player.getUniqueId()));
    }

    public void removePlayerData(Player player) {
        this.playerDataMap.remove(player.getUniqueId());
    }

    public boolean hasPlayerData(Player player) {
        return this.playerDataMap.containsKey(player.getUniqueId());
    }


    public PlayerData getPlayerData(Player player) {
        return this.playerDataMap.get(player.getUniqueId());
    }

    public PlayerData getPlayerData(UUID playerUUID) {
        return this.playerDataMap.get(playerUUID);
    }
}