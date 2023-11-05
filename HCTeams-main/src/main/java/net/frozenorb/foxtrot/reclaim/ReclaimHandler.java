package net.frozenorb.foxtrot.reclaim;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.qLib;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class ReclaimHandler {

    private final File dataFile = new File(Foxtrot.getInstance().getDataFolder(), "reclaimed.json");
    private final Type dataType = new TypeToken<Set<UUID>>() {}.getType();

    private Set<UUID> hasReclaimed = new HashSet<>();

    public ReclaimHandler() {
        loadData();
    }

    public void loadData() {
        if (dataFile.exists()) {
            try (Reader reader = Files.newReader(dataFile, Charsets.UTF_8)) {
                hasReclaimed = qLib.PLAIN_GSON.fromJson(reader, dataType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(Foxtrot.getInstance(), this::saveData, 20L * 60L, 20L * 60L);
    }

    private void saveData() {
        try {
            Files.write(qLib.PLAIN_GSON.toJson(hasReclaimed), dataFile, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
