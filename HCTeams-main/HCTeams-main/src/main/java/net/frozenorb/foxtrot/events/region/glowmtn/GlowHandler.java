package net.frozenorb.foxtrot.events.region.glowmtn;

import java.io.File;
import java.io.IOException;

import net.frozenorb.foxtrot.events.region.glowmtn.listeners.GlowListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.qlib.qLib;
import net.minecraft.util.org.apache.commons.io.FileUtils;

public class GlowHandler {

    private static File file;
    @Getter private final static String glowTeamName = "Glowstone";
    @Getter @Setter private GlowMountain glowMountain;

    private long lastReset = System.currentTimeMillis();

    public GlowHandler() {
        try {
            file = new File(Foxtrot.getInstance().getDataFolder(), "glowmtn.json");

            if (!file.exists()) {
                glowMountain = null;

                if (file.createNewFile()) {
                    Foxtrot.getInstance().getLogger().warning("Created a new glow mountain json file.");
                }
            } else {
                glowMountain = qLib.GSON.fromJson(FileUtils.readFileToString(file), GlowMountain.class);
                Foxtrot.getInstance().getLogger().info("Successfully loaded the glow mountain from file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int secs = getBroadcastInterval() * 20;

        Foxtrot.getInstance().getServer().getScheduler().runTaskTimer(Foxtrot.getInstance(), () -> {
            getGlowMountain().reset();

            // Broadcast the reset
            Bukkit.broadcastMessage(ChatColor.GOLD + "[Glowstone Mountain]" + ChatColor.GREEN + " All glowstone has been reset!");
            lastReset = System.currentTimeMillis();
        }, secs, secs);

        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new GlowListener(), Foxtrot.getInstance());
    }

    public void save() {
        try {
            FileUtils.write(file, qLib.GSON.toJson(glowMountain));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasGlowMountain() {
        return glowMountain != null;
    }

    public static Claim getClaim() {
        return Foxtrot.getInstance().getTeamHandler().getTeam(glowTeamName).getClaims().get(0); // null if no glowmtn is set!
    }

    private int getBroadcastInterval() {
        return Foxtrot.getInstance().getServerHandler().isHardcore() ? (90 * 60) : 600;
    }

    public long getResetTime() {
        long timeSinceLast = (System.currentTimeMillis() - lastReset) / 1000;

        return getBroadcastInterval() - timeSinceLast;
    }
}