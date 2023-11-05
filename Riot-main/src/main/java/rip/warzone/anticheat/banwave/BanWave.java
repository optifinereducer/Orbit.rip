package rip.warzone.anticheat.banwave;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.AntiCheat;
import rip.warzone.anticheat.util.CC;
import rip.warzone.sprite.profile.Profile;
import rip.warzone.sprite.utils.SpriteAPI;

import java.util.UUID;

public class BanWave {

    public BanWave() {
        Bukkit.getScheduler().runTaskTimer(AntiCheat.instance, new Runnable() {
            @Override
            public void run() {
                runBanWave();
            }
        }, 0, ((20 * 60) * 60) * 5); // 20 = 1second, so with this math it should run every 5 hours

    }

    private static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    //Executes the banwave and bans anyone that is in the banwavelist
    public static void runBanWave() {
        AntiCheat.instance.getBanWaveManager().runningBanwave=true;
        int max=AntiCheat.instance.getBanWaveManager().getPlayersToBan().size();

        if (max > 0) {
            long last=0;
            while (AntiCheat.instance.getBanWaveManager().counter <= max - 1) {
                if(System.currentTimeMillis()-last > 2500){
                    Profile profile = SpriteAPI.INSTANCE.getProfile(AntiCheat.instance.getBanWaveManager().getPlayersToBan().get(AntiCheat.instance.getBanWaveManager().counter));
                    Bukkit.broadcastMessage(CC.translate(SpriteAPI.INSTANCE.formatName(profile.getUuid()) + ChatColor.RED + " was caught cheating and was removed in a ban wave."));
                    AntiCheat.instance.getServer().dispatchCommand(AntiCheat.instance.getServer().getConsoleSender(), "ban " + profile.getUsername() + " [Riot] Ban-Wave");
                    AntiCheat.instance.getBanWaveManager().counter++;
                    last = System.currentTimeMillis();
                }
            }
        }

        AntiCheat.instance.getBanWaveManager().counter=0;
        AntiCheat.instance.getBanWaveManager().runningBanwave=false;
    }

}

