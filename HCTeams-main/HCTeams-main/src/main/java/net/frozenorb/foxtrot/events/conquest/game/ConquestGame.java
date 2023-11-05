package net.frozenorb.foxtrot.events.conquest.game;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.Event;
import net.frozenorb.foxtrot.events.EventType;
import net.frozenorb.foxtrot.events.conquest.ConquestHandler;
import net.frozenorb.foxtrot.events.conquest.enums.ConquestCapzone;
import net.frozenorb.foxtrot.events.events.EventCapturedEvent;
import net.frozenorb.foxtrot.events.koth.KOTH;
import net.frozenorb.foxtrot.events.koth.events.EventControlTickEvent;
import net.frozenorb.foxtrot.events.koth.events.KOTHControlLostEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ConquestGame implements Listener {

    @Getter private LinkedHashMap<ObjectId, Integer> teamPoints = new LinkedHashMap<>();

    public ConquestGame() {
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());

        for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (event.getType() != EventType.KOTH) continue;
            KOTH koth = (KOTH) event;
            if (koth.getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)) {
                if (!koth.isHidden()) {
                    koth.setHidden(true);
                }

                if (koth.getCapTime() != ConquestHandler.TIME_TO_CAP) {
                    koth.setCapTime(ConquestHandler.TIME_TO_CAP);
                }

                koth.activate();
            }
        }

        Foxtrot.getInstance().getServer().broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Conquest has started! Use /conquest for more information.");
        Foxtrot.getInstance().getConquestHandler().setGame(this);
    }

    public void endGame(final Team winner) {
        if (winner == null) {
            Foxtrot.getInstance().getServer().broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Conquest has ended.");
        } else {
            Foxtrot.getInstance().getServer().broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD.toString() + ChatColor.BOLD + winner.getName() + ChatColor.GOLD + " has won Conquest!");
        }

        for (Event koth : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (koth.getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)) {
                koth.deactivate();
            }
        }

        HandlerList.unregisterAll(this);
        Foxtrot.getInstance().getConquestHandler().setGame(null);
    }

    @EventHandler
    public void onKOTHCaptured(final EventCapturedEvent event) {
        if (!event.getEvent().getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)) {
            return;
        }

        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());
        ConquestCapzone capzone = ConquestCapzone.valueOf(event.getEvent().getName().replace(ConquestHandler.KOTH_NAME_PREFIX, "").toUpperCase());

        if (team == null) {
            return;
        }

        if (teamPoints.containsKey(team.getUniqueId())) {
            teamPoints.put(team.getUniqueId(), teamPoints.get(team.getUniqueId()) + 1);
        } else {
            teamPoints.put(team.getUniqueId(), 1);
        }

        teamPoints = sortByValues(teamPoints);
        Foxtrot.getInstance().getServer().broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + team.getName() + ChatColor.GOLD + " captured " + capzone.getColor() + capzone.getName() + ChatColor.GOLD + " and earned a point!" + ChatColor.AQUA + " (" + teamPoints.get(team.getUniqueId()) +
                "/" + ConquestHandler.getPointsToWin() + ")");

        if (teamPoints.get(team.getUniqueId()) >= ConquestHandler.getPointsToWin()) {
            endGame(team);
            ItemStack conquestKey = InventoryUtils.generateKOTHRewardKey("Conquest");
            conquestKey.setAmount(5);
            event.getPlayer().getInventory().addItem(conquestKey);
            if (!event.getPlayer().getInventory().contains(conquestKey)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), conquestKey);
            }
            Foxtrot.getInstance().getGemMap().addGems(event.getPlayer().getUniqueId(), 15);
            event.getPlayer().sendMessage(CC.GREEN + "You have received " + CC.DARK_GREEN + "+15" + CC.GREEN + " for capturing koth!");
        } else {
            new BukkitRunnable() {

                public void run() {
                    if (Foxtrot.getInstance().getConquestHandler().getGame() != null) {
                        event.getEvent().activate();
                    }
                }

            }.runTaskLater(Foxtrot.getInstance(), 10L);
        }
    }

    @EventHandler
    public void onKOTHControlLost(KOTHControlLostEvent event) {
        if (!event.getKOTH().getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)) {
            return;
        }

        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(event.getKOTH().getCurrentCapper()));
        ConquestCapzone capzone = ConquestCapzone.valueOf(event.getKOTH().getName().replace(ConquestHandler.KOTH_NAME_PREFIX, "").toUpperCase());

        if (team == null) {
            return;
        }

        team.sendMessage(ConquestHandler.PREFIX + ChatColor.GOLD + " " + event.getKOTH().getCurrentCapper() + " was knocked off of " + capzone.getColor() + capzone.getName() + ChatColor.GOLD + "!");
    }
    @EventHandler
    public void onKOTHControlTick(EventControlTickEvent event) {
        
        if (!event.getKOTH().getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX) || event.getKOTH().getRemainingCapTime() % 5 != 0) {
            return;
        }

        ConquestCapzone capzone = ConquestCapzone.valueOf(event.getKOTH().getName().replace(ConquestHandler.KOTH_NAME_PREFIX, "").toUpperCase());
        Player capper = Foxtrot.getInstance().getServer().getPlayerExact(event.getKOTH().getCurrentCapper());

        if (capper != null) {
            capper.sendMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Attempting to capture " + capzone.getColor() + capzone.getName() + ChatColor.GOLD + "!" + ChatColor.AQUA + " (" + event.getKOTH().getRemainingCapTime() + "s)");
        }
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity());

        if (team == null || !teamPoints.containsKey(team.getUniqueId())) {
            return;
        }

        teamPoints.put(team.getUniqueId(), Math.max(0, teamPoints.get(team.getUniqueId()) - ConquestHandler.POINTS_DEATH_PENALTY));
        teamPoints = sortByValues(teamPoints);
        team.sendMessage(ConquestHandler.PREFIX + ChatColor.GOLD + " Your team has lost " + ConquestHandler.POINTS_DEATH_PENALTY + " points because of " + event.getEntity().getName() + "'s death!" + ChatColor.AQUA + " (" + teamPoints.get(team.getUniqueId()) + "/" + ConquestHandler.getPointsToWin() + ")");
    }

    private static LinkedHashMap<ObjectId, Integer> sortByValues(Map<ObjectId, Integer> map) {
        LinkedList<Map.Entry<ObjectId, Integer>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        LinkedHashMap<ObjectId, Integer> sortedHashMap = new LinkedHashMap<>();
        Iterator<Map.Entry<ObjectId, Integer>> iterator = list.iterator();

        while (iterator.hasNext()) {
            java.util.Map.Entry<ObjectId, Integer> entry = iterator.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return sortedHashMap;
    }

}