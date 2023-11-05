package rip.warzone.anticheat.command;

import com.google.common.collect.Lists;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.UUIDUtils;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import rip.warzone.anticheat.AntiCheat;
import rip.warzone.anticheat.log.Log;
import rip.warzone.anticheat.util.HastebinAPI;
import rip.warzone.anticheat.util.LinkedList;
import rip.warzone.anticheat.util.TimeUtils;

import java.io.IOException;
import java.util.*;

public class LogsCommand {

    @Command(names = {"logs", "records"}, permission = "anticheat.logs", async = true)
    public static void execute(CommandSender sender, @Param(name = "target")  UUID target, @Param(name = "page", defaultValue = "1")  int page) {
        Iterable<Document> mongoDocs = AntiCheat.instance.getMongoDatabase().getCollection("logs").find(new Document("player", target.toString()));
        Iterable<Log> sessionLogs = AntiCheat.instance.getLogManager().getLogQueue();
        List<Document> logs = new ArrayList<>();
        Set<Long> caught = new HashSet<>();

        for ( Document mongoDocument : mongoDocs) {
            long time = mongoDocument.getLong("time");
            logs.add(mongoDocument);
            caught.add(time);
        }

        for (Log log : sessionLogs) {
            if (log.getPlayer().equals(target) && caught.add(log.getTimestamp())) {
                logs.add(log.toDocument());
            }
        }

        if (logs.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No records for " + FrozenUUIDCache.name(target) + " found.");
            return;
        }

        logs = Lists.reverse(logs);

        List<Document> finalLogs = logs;
        try {
            LinkedList<String> logs1 = new LinkedList<>();
            for(Document log1 : finalLogs){
                logs1.add(" - [" + TimeUtils.formatIntoMMSS((int) ((System.currentTimeMillis() - log1.getLong("time")) / 1000L)) + " ago on " + log1.getString("server") + "] " +  ((sender.hasPermission("anticheat.logs.view") ? log1.getString("log") : "You do not have permission to view this log.")));
            }
            logs1.addFirst("Logs for " + UUIDUtils.name(target) + " (Total " + logs1.size() + "):");
            sender.sendMessage(ChatColor.GREEN + "Logs for " + UUIDUtils.name(target) + ": " + new HastebinAPI().post(String.join("\n", logs1), true));
        } catch (IOException e) {
            sender.sendMessage(ChatColor.RED + "Failed to retrieve logs of " + UUIDUtils.name(target) + ".");
            e.printStackTrace();
        }
    }

}