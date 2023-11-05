package net.frozenorb.foxtrot.nametag;

import com.lunarclient.bukkitapi.LunarClientAPI;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.bounty.Bounty;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.RangerClass;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.modsuite.ModUtils;
import net.frozenorb.qlib.nametag.NametagInfo;
import net.frozenorb.qlib.nametag.NametagProvider;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class FoxtrotNametagProvider extends NametagProvider {

    public FoxtrotNametagProvider() {
        super("Foxtrot Provider", 5);
    }

    // testing stuff
    private Map<ObjectId, ChatColor> teamColorMap = new HashMap<>();
    private ChatColor[] teamColors = {
            ChatColor.DARK_BLUE,
            ChatColor.DARK_GREEN,
            ChatColor.DARK_AQUA,
            ChatColor.DARK_RED,
            ChatColor.DARK_PURPLE,
            ChatColor.GOLD,
            ChatColor.BLUE,
            ChatColor.GREEN,
            ChatColor.AQUA,
            ChatColor.RED,
            ChatColor.LIGHT_PURPLE,
            ChatColor.YELLOW
    };

    @Override
    public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
        Team viewerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(refreshFor);
        NametagInfo nametagInfo = null;

        if (viewerTeam != null) {
            if (viewerTeam.isMember(toRefresh.getUniqueId())) {
                nametagInfo = createNametag(toRefresh, ChatColor.DARK_GREEN.toString(), "");
            } else if (viewerTeam.isAlly(toRefresh.getUniqueId())) {
                nametagInfo = createNametag(toRefresh, Team.ALLY_COLOR.toString(), "");
            }
        }

        // If we already found something above they override these, otherwise we can do these checks.
        if (nametagInfo == null) {
            if (RangerClass.getMarkedPlayers().containsKey(toRefresh.getUniqueId()) && RangerClass.getMarkedPlayers().get(toRefresh.getUniqueId()) > System.currentTimeMillis()) {
                nametagInfo = createNametag(toRefresh, Foxtrot.getInstance().getServerHandler().getStunTagColor().toString(), "");
            } else if (ArcherClass.getMarkedPlayers().containsKey(toRefresh.getName()) && ArcherClass.getMarkedPlayers().get(toRefresh.getName()) > System.currentTimeMillis()) {
                nametagInfo = createNametag(toRefresh, Foxtrot.getInstance().getServerHandler().getArcherTagColor().toString(), "");
            } else if (viewerTeam != null && viewerTeam.getFocused() != null && viewerTeam.getFocused().equals(toRefresh.getUniqueId())) {
                nametagInfo = createNametag(toRefresh, ChatColor.LIGHT_PURPLE.toString(), "");
            } else if (viewerTeam != null && viewerTeam.getFocusedTeam() != null && viewerTeam.getFocusedTeam().isMember(toRefresh.getUniqueId())) {
                nametagInfo = createNametag(toRefresh, ChatColor.LIGHT_PURPLE.toString(), "");
            }
        }

        // You always see yourself as green.
        if (refreshFor == toRefresh) {
            nametagInfo = createNametag(toRefresh, ChatColor.DARK_GREEN.toString(), "");
        }

        boolean runningLunar = Bukkit.getPluginManager().getPlugin("LunarClient-API") != null && LunarClientAPI.getInstance().isRunningLunarClient(refreshFor);

        if (toRefresh.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            if (!ModUtils.isModMode(refreshFor)) {
                if (viewerTeam == null || !viewerTeam.isMember(toRefresh.getUniqueId())) {
                    if (runningLunar) {
                        LunarClientAPI.getInstance().hideNametag(toRefresh, refreshFor);
                    }
                    return NametagProvider.createNametag(ChatColor.GRAY + "*", "");
                }
            }
        }

        // LC nametag stuff for test
        if (runningLunar) {
            List<String> tags = new ArrayList<>();
            Team refreshTeam = Foxtrot.getInstance().getTeamHandler().getTeam(toRefresh);

            if (ModUtils.isModMode(refreshFor)) {
                if (refreshTeam != null) {
                    teamColorMap.putIfAbsent(refreshTeam.getUniqueId(), teamColors[ThreadLocalRandom.current().nextInt(teamColors.length)]);
                    tags.add(teamColorMap.get(refreshTeam.getUniqueId()) + "[" + refreshTeam.getName() + "]");
                }
            }

            if (ModUtils.isModMode(toRefresh)) {
                tags.add(ChatColor.GRAY + "[Mod Mode]");
            }

            NametagInfo info = nametagInfo == null ? createNametag(toRefresh, Foxtrot.getInstance().getServerHandler().getDefaultRelationColor().toString(), "") : nametagInfo;
            tags.add(info.getPrefix() + toRefresh.getName() + info.getSuffix());

            LunarClientAPI.getInstance().overrideNametag(toRefresh, tags, refreshFor);
        }

        // If nothing custom was set, fall back on yellow.
        return (nametagInfo == null ? createNametag(toRefresh, Foxtrot.getInstance().getServerHandler().getDefaultRelationColor().toString(), "") : nametagInfo);
    }

    private NametagInfo createNametag(Player displayed, String prefix, String suffix) {
        String invis = ModUtils.isInvisible(displayed) ? ChatColor.GRAY + "*" : "";
        prefix = invis + prefix;

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            Bounty bounty = Foxtrot.getInstance().getMapHandler().getBountyManager().getBounty(displayed);

            if (bounty != null) {
                suffix += " " + ChatColor.GREEN + bounty.getGems() + "♦";
            }
        }

        Map<Integer, UUID> placesMap = Foxtrot.getInstance().getMapHandler().getStatsHandler() != null ? Foxtrot.getInstance().getMapHandler().getStatsHandler().getTopKills() : null;
        if (placesMap == null) {
            return createNametag(prefix, suffix);
        }

        int place = placesMap.size() == 3 ? displayed.getUniqueId().equals(placesMap.get(1)) ? 1 : displayed.getUniqueId().equals(placesMap.get(2)) ? 2 : displayed.getUniqueId().equals(placesMap.get(3)) ? 3 : 99 : 99;
        if (place == 99) {
            return createNametag(prefix, suffix);
        }

        String coloredPrefix = ChatColor.translateAlternateColorCodes('&', place == 1 ? "&8[&6#1&8] " : place == 2 ? "&8[&7#2&8] " : "&8[&f#3&8] ");

        return createNametag(coloredPrefix + prefix, suffix);
    }

}