package rip.warzone.anticheat.log;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class Log {

    private final long timestamp;
    private final UUID player;
    private final String log;
    private final double tps;

    public Document toDocument() {
        Document document=new Document();
        document.put("player", this.player.toString());
        document.put("server", Bukkit.getServer().getServerName());
        document.put("serverGroup", Bukkit.getServerGroup());
        document.put("log", this.log);
        document.put("time", this.timestamp);
        return document;
    }

    public Log(UUID player, String log, double tps) {
        this.timestamp=System.currentTimeMillis();
        this.player=player;
        this.log=log;
        this.tps=tps;
    }
}