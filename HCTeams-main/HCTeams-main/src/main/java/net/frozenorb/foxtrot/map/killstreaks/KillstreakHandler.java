package net.frozenorb.foxtrot.map.killstreaks;

import java.util.List;
import java.util.stream.Collectors;

import net.frozenorb.foxtrot.map.killstreaks.valortypes.GemKillstreak;
import net.frozenorb.foxtrot.map.killstreaks.velttypes.*;
import net.frozenorb.foxtrot.map.stats.StatsEntry;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.util.ClassUtils;
import org.bukkit.event.Listener;

public class KillstreakHandler implements Listener {

    @Getter private List<Killstreak> killstreaks = Lists.newArrayList();
    @Getter private List<PersistentKillstreak> persistentKillstreaks = Lists.newArrayList();

    public KillstreakHandler() {
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());
        FrozenCommandHandler.registerClass(this.getClass());

        killstreaks.add(new Debuffs());
        killstreaks.add(new Gapple());
        killstreaks.add(new GoldenApples());
        killstreaks.add(new PotionRefillToken());
        killstreaks.add(new GemKillstreak());

        persistentKillstreaks.add(new FireRes());
        persistentKillstreaks.add(new Invis());
        persistentKillstreaks.add(new PermSpeed2());
        persistentKillstreaks.add(new Speed2());
        persistentKillstreaks.add(new Strength());

        killstreaks.sort((first, second) -> {
            int firstNumber = first.getKills()[0];
            int secondNumber = second.getKills()[0];

            if (firstNumber < secondNumber) {
                return -1;
            }
            return 1;

        });
        
        persistentKillstreaks.sort((first, second) -> {
            int firstNumber = first.getKillsRequired();
            int secondNumber = second.getKillsRequired();

            if (firstNumber < secondNumber) {
                return -1;
            }
            return 1;

        });
    }

    public Killstreak check(int kills) {
        for (Killstreak killstreak : killstreaks) {
            for (int kill : killstreak.getKills()) {
                if (kills == kill) {
                    return killstreak;
                }
            }
        }

        return null;
    }
    
    public List<PersistentKillstreak> getPersistentKillstreaks(Player player, int count) {
        return persistentKillstreaks.stream().filter(s -> s.check(count)).collect(Collectors.toList());
    }

    @Command(names = "setks", permission = "hcteams.setkillstreak")
    public static void setKillstreak(Player player, @Param(name = "killstreak") int killstreak) {
        StatsEntry statsEntry = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(player);
        statsEntry.setKillstreak(killstreak);

        player.sendMessage(ChatColor.GREEN + "You set your killstreak to: " + killstreak);
    }

}
