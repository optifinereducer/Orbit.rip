package net.frozenorb.foxtrot.tab;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.frozenorb.foxtrot.util.modsuite.ModUtils;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.Event;
import net.frozenorb.foxtrot.events.EventScheduledTime;
import net.frozenorb.foxtrot.events.koth.KOTH;
import net.frozenorb.foxtrot.listener.BorderListener;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.commands.team.TeamListCommand;
import net.frozenorb.foxtrot.util.PlayerDirection;
import net.frozenorb.qlib.tab.LayoutProvider;
import net.frozenorb.qlib.tab.TabLayout;
import net.frozenorb.qlib.util.TimeUtils;

public class FoxtrotTabLayoutProvider implements LayoutProvider {

    private LinkedHashMap<Team, Integer> cachedTeamList = Maps.newLinkedHashMap();
    long cacheLastUpdated;

    @Override
    public TabLayout provide(Player player) {
        TabListMode mode = Foxtrot.getInstance().getTabListModeMap().getTabListMode(player.getUniqueId());

        TabLayout layout = TabLayout.create(player);
        
        
       if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
           kitmap(player, layout);
           return layout;
       }
        
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        String serverName = Foxtrot.getInstance().getServerHandler().getTabServerName();
        String titleColor = Foxtrot.getInstance().getServerHandler().getTabSectionColor();
        String infoColor = Foxtrot.getInstance().getServerHandler().getTabInfoColor();

        layout.set(1, 0, serverName);

        int y = -1;

        if (team != null) {
            layout.set(0, ++y, titleColor + "Home:");

            if (team.getHQ() != null) {
                String homeLocation = infoColor.toString() + team.getHQ().getBlockX() + ", " + team.getHQ().getBlockY() + ", " + team.getHQ().getBlockZ();
                layout.set(0, ++y, homeLocation);
            } else {
                layout.set(0, ++y, infoColor + "Not Set");
            }

            ++y; // blank

            int balance = (int) team.getBalance();
            layout.set(0, ++y, titleColor + "Team Info:");
            layout.set(0, ++y, infoColor + "DTR: " + (team.isRaidable() ? ChatColor.DARK_RED : infoColor) + Team.DTR_FORMAT.format(team.getDTR()));
            layout.set(0, ++y, infoColor + "Online: " + team.getOnlineMemberAmount() + "/" + team.getMembers().size());
            layout.set(0, ++y, infoColor + "Balance: $" + balance);

            ++y; // blank
        }

        layout.set(0, ++y, titleColor + "Player Info:");
        layout.set(0, ++y, infoColor + "Kills: " + Foxtrot.getInstance().getKillsMap().getKills(player.getUniqueId()));
        layout.set(0, ++y, infoColor + "Deaths: " + Foxtrot.getInstance().getDeathsMap().getDeaths(player.getUniqueId()));

        ++y; // blank

        layout.set(0, ++y, titleColor + "Your Location:");

        String location;

        Location loc = player.getLocation();
        Team ownerTeam = LandBoard.getInstance().getTeam(loc);

        if (ownerTeam != null) {
            location = ownerTeam.getName(player.getPlayer());
        } else if (!Foxtrot.getInstance().getServerHandler().isWarzone(loc)) {
            location = ChatColor.GRAY + "The Wilderness";
        } else if (LandBoard.getInstance().getTeam(loc) != null && LandBoard.getInstance().getTeam(loc).getName().equalsIgnoreCase("citadel")) {
            location = titleColor + "Citadel";
        } else {
            location = ChatColor.RED + "Warzone";
        }

        layout.set(0, ++y, location);

        /* Getting the direction 4 times a second for each player on the server may be intensive.
        We may want to cache the entire location so it is accessed no more than 1 time per second.
        FIXME, WIP */
        String direction = PlayerDirection.getCardinalDirection(player);
        if (direction != null) {
            layout.set(0, ++y, ChatColor.GRAY + "(" + loc.getBlockX() + ", " + loc.getBlockZ() + ") [" + direction + "]");
        } else {
            layout.set(0, ++y, ChatColor.GRAY + "(" + loc.getBlockX() + ", " + loc.getBlockZ() + ")");
        }
        ++y; // blank

        KOTH activeKOTH = null;
        for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (!(event instanceof KOTH)) continue;
            KOTH koth = (KOTH) event;
            if (koth.isActive() && !koth.isHidden()) {
                activeKOTH = koth;
                break;
            }
        }

        if (activeKOTH == null) {
            Date now = new Date();

            String nextKothName = null;
            Date nextKothDate = null;

            for (Map.Entry<EventScheduledTime, String> entry : Foxtrot.getInstance().getEventHandler().getEventSchedule().entrySet()) {
                if (entry.getKey().toDate().after(now)) {
                    if (nextKothDate == null || nextKothDate.getTime() > entry.getKey().toDate().getTime()) {
                        nextKothName = entry.getValue();
                        nextKothDate = entry.getKey().toDate();
                    }
                }
            }

            if (nextKothName != null) {
                layout.set(0, ++y, titleColor + "Next KOTH:");
                layout.set(0, ++y, infoColor + nextKothName);

                Event event = Foxtrot.getInstance().getEventHandler().getEvent(nextKothName);

                if (event != null && event instanceof KOTH) {
                    KOTH koth = (KOTH) event;
                    layout.set(0, ++y, infoColor.toString() + koth.getCapLocation().getBlockX() + ", " + koth.getCapLocation().getBlockY() + ", " + koth.getCapLocation().getBlockZ()); // location

                    int seconds = (int) ((nextKothDate.getTime() - System.currentTimeMillis()) / 1000);
                    layout.set(0, ++y, titleColor + "Goes active in:");

                    String time = formatIntoDetailedString(seconds)
                            .replace("minutes", "min").replace("minute", "min")
                            .replace("seconds", "sec").replace("second", "sec");

                    layout.set(0, ++y, infoColor + time);
                }
            }
        } else {
            layout.set(0, ++y, titleColor + activeKOTH.getName());
            layout.set(0, ++y, infoColor + TimeUtils.formatIntoHHMMSS(activeKOTH.getRemainingCapTime()));
            layout.set(0, ++y, infoColor.toString() + activeKOTH.getCapLocation().getBlockX() + ", " + activeKOTH.getCapLocation().getBlockY() + ", " + activeKOTH.getCapLocation().getBlockZ()); // location
        }

        if (team != null) {
            layout.set(1, mode == TabListMode.DETAILED_WITH_FACTION_INFO ? 5 : 2, titleColor + team.getName());

            String watcherName = ChatColor.DARK_GREEN + player.getName();
            if (team.isOwner(player.getUniqueId())) {
                watcherName += ChatColor.GRAY + "**";
            } else if (team.isCoLeader(player.getUniqueId())) {
                watcherName += ChatColor.GRAY + "**";
            } else if (team.isCaptain(player.getUniqueId())) {
                watcherName += ChatColor.GRAY + "*";
            }

            layout.set(1, mode == TabListMode.DETAILED_WITH_FACTION_INFO ? 6 : 3, watcherName, ((CraftPlayer) player).getHandle().ping); // the viewer is always first on the list

            Player owner = null;
            List<Player> coleaders = Lists.newArrayList();
            List<Player> captains = Lists.newArrayList();
            List<Player> members = Lists.newArrayList();
            for (Player member : team.getOnlineMembers()) {
                if (team.isOwner(member.getUniqueId())) {
                    owner = member;
                } else if (team.isCoLeader(member.getUniqueId())) {
                    coleaders.add(member);
                } else if (team.isCaptain(member.getUniqueId())) {
                    captains.add(member);
                } else {
                    members.add(member);
                }
            }

            int x = 1;
            y = mode == TabListMode.DETAILED ? 4 : 7;

            // then the owner
            if (owner != null && owner != player) {
                layout.set(x, y, ChatColor.DARK_GREEN + owner.getName() + ChatColor.GRAY + "**", ((CraftPlayer) owner).getHandle().ping);

                y++;

                if (y >= 20) {
                    y = 0;
                    x++;
                }
            }

            // then the coleaders
            for (Player coleader : coleaders) {
                if (coleader == player) continue;

                layout.set(x, y, ChatColor.DARK_GREEN + coleader.getName() + ChatColor.GRAY + "**", ((CraftPlayer) coleader).getHandle().ping);

                y++;

                if (y >= 20) {
                    y = 0;
                    x++;
                }
            }


            // then the captains
            for (Player captain : captains) {
                if (captain == player) continue;

                layout.set(x, y, ChatColor.DARK_GREEN + captain.getName() + ChatColor.GRAY + "*", ((CraftPlayer) captain).getHandle().ping);

                y++;

                if (y >= 20) {
                    y = 0;
                    x++;
                }
            }

            // and only then, normal members.
            for (Player member : members) {
                if (member == player) continue;

                layout.set(x, y, ChatColor.DARK_GREEN + member.getName(), ((CraftPlayer) member).getHandle().ping);

                y++;

                if (y >= 20) {
                    y = 0;
                    x++;
                }
            }

            // basically, if we're not on the third column yet, set the y to 0, and go to the third column.
            // if we're already there, just place whatever we got under the last player's name
            if (x < 2) {
                y = 0;
            } else {
                y++; // comment this out if you don't want a space in between the last player and the info below:
            }
        }

        if (team == null) {
            y = 0;
        }

        if (mode == TabListMode.DETAILED) {
            String endPortalLocation = Foxtrot.getInstance().getMapHandler().getEndPortalLocation();
            if (endPortalLocation != null && (!endPortalLocation.equals("N/A") && !endPortalLocation.isEmpty())) {
                layout.set(2, y, titleColor + "End Portals:");
                layout.set(2, ++y, infoColor + endPortalLocation);
                layout.set(2, ++y, infoColor + "in each quadrant");

                ++y;
                layout.set(2, ++y, titleColor + "Kit:");
                layout.set(2, ++y, infoColor + Foxtrot.getInstance().getServerHandler().getEnchants());
            } else {
                layout.set(2, y, titleColor + "Kit:");
                layout.set(2, ++y, infoColor + Foxtrot.getInstance().getServerHandler().getEnchants());
            }

            ++y;
            layout.set(2, ++y, titleColor + "Border:");
            layout.set(2, ++y, infoColor + String.valueOf(BorderListener.BORDER_SIZE));

            ++y;
            layout.set(2, ++y, titleColor + "Players Online:");
            layout.set(2, ++y, infoColor + String.valueOf(Bukkit.getOnlinePlayers().size()));

            Set<ObjectId> cappers = Foxtrot.getInstance().getCitadelHandler().getCappers();

            if (!cappers.isEmpty()) {
                Set<String> capperNames = new HashSet<>();

                for (ObjectId capper : cappers) {
                    Team capperTeam = Foxtrot.getInstance().getTeamHandler().getTeam(capper);

                    if (capperTeam != null) {
                        capperNames.add(capperTeam.getName());
                    }
                }

                if (!capperNames.isEmpty()) {
                    ++y;
                    layout.set(2, ++y, titleColor + "Citadel Cappers:");

                    for (String capper : capperNames) {
                        layout.set(2, ++y, infoColor + capper);
                    }
                }
            }
        } else {
            layout.set(1, 2, titleColor + "Players Online:");
            layout.set(1, 3, infoColor + String.valueOf(Bukkit.getOnlinePlayers().size()));

            // faction list (10 entries)
            boolean shouldReloadCache = cachedTeamList == null || (System.currentTimeMillis() - cacheLastUpdated > 2000);

            y = 1;

            Map<Team, Integer> teamPlayerCount = new HashMap<>();

            if (shouldReloadCache) {
                // Sort of weird way of getting player counts, but it does it in the least iterations (1), which is what matters!
                for (Player other : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                    if (ModUtils.isInvisible(other)) {
                        continue;
                    }

                    Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(other);

                    if (playerTeam != null) {
                        if (teamPlayerCount.containsKey(playerTeam)) {
                            teamPlayerCount.put(playerTeam, teamPlayerCount.get(playerTeam) + 1);
                        } else {
                            teamPlayerCount.put(playerTeam, 1);
                        }
                    }
                }
            }

            LinkedHashMap<Team, Integer> sortedTeamPlayerCount;

            if (shouldReloadCache) {
                sortedTeamPlayerCount = TeamListCommand.sortByValues(teamPlayerCount);
                cachedTeamList = sortedTeamPlayerCount;
                cacheLastUpdated = System.currentTimeMillis();
            } else {
                sortedTeamPlayerCount = cachedTeamList;
            }

            int index = 0;

            boolean title = false;

            for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {
                index++;

                if (index > 19) {
                    break;
                }

                if (!title) {
                    title = true;
                    layout.set(2, 0, titleColor + "Team List:");
                }

                String teamName = teamEntry.getKey().getName();
                String teamColor = teamEntry.getKey().isMember(player.getUniqueId()) ? ChatColor.GREEN.toString() : infoColor;

                if (teamName.length() > 10) teamName = teamName.substring(0, 10);

                layout.set(2, y++, teamColor + teamName + ChatColor.GRAY + " (" + teamEntry.getValue() + ")");
            }
        }

        /* This works, but it's probably not efficient...
        if (player.hasPermission("basic.staff")) {
            ++y;
            layout.set(2, ++y, titleColor + "Online Staff:");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.hasPermission("basic.staff")) {
                    continue;
                }
                layout.set(2, ++y, infoColor + onlinePlayer.getKitName());
            }
        }
        */

        return layout;
    }
    
    private void kitmap(Player player, TabLayout layout) {
        layout.set(1, 1, "&4&lWarzone Kits");
        layout.set(0, 4, "&cMap Info");
        layout.set(0, 5, "&7Map Kit: P" + Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel() + " S" + Integer.toString(Enchantment.DAMAGE_ALL.getMaxLevel()));
        layout.set(0, 6, "&7Faction Size: " + Foxtrot.getInstance().getMapHandler().getTeamSize());
        layout.set(0, 7, "&7Border: 1500");
        
        int y = 8;
        String titleColor = "&c&l";
        String infoColor = "&7";
        
        KOTH activeKOTH = null;
        for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (!(event instanceof KOTH)) continue;
            KOTH koth = (KOTH) event;
            if (koth.isActive() && !koth.isHidden()) {
                activeKOTH = koth;
                break;
            }
        }

        if (activeKOTH == null) {
            Date now = new Date();

            String nextKothName = null;
            Date nextKothDate = null;

            for (Map.Entry<EventScheduledTime, String> entry : Foxtrot.getInstance().getEventHandler().getEventSchedule().entrySet()) {
                if (entry.getKey().toDate().after(now)) {
                    if (nextKothDate == null || nextKothDate.getTime() > entry.getKey().toDate().getTime()) {
                        nextKothName = entry.getValue();
                        nextKothDate = entry.getKey().toDate();
                    }
                }
            }

            if (nextKothName != null) {
                layout.set(0, ++y, titleColor + "Next KOTH:");
                layout.set(0, ++y, infoColor + nextKothName);

                Event event = Foxtrot.getInstance().getEventHandler().getEvent(nextKothName);

                if (event != null && event instanceof KOTH) {
                    KOTH koth = (KOTH) event;
                    layout.set(0, ++y, infoColor.toString() + koth.getCapLocation().getBlockX() + ", " + koth.getCapLocation().getBlockY() + ", " + koth.getCapLocation().getBlockZ()); // location

                    int seconds = (int) ((nextKothDate.getTime() - System.currentTimeMillis()) / 1000);
                    layout.set(0, ++y, titleColor + "Goes active in:");

                    String time = formatIntoDetailedString(seconds)
                            .replace("minutes", "min").replace("minute", "min")
                            .replace("seconds", "sec").replace("second", "sec");

                    layout.set(0, ++y, infoColor + time);
                }
            }
        } else {
            layout.set(0, ++y, titleColor + activeKOTH.getName());
            layout.set(0, ++y, infoColor + TimeUtils.formatIntoHHMMSS(activeKOTH.getRemainingCapTime()));
            layout.set(0, ++y, infoColor + activeKOTH.getCapLocation().getBlockX() + ", " + activeKOTH.getCapLocation().getBlockY() + ", " + activeKOTH.getCapLocation().getBlockZ()); // location
        }

        layout.set(1, 2, "&cOnline&7: " + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
        
        layout.set(1, 4, titleColor + "Faction Info");

        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
        if (team != null) {
            layout.set(1, 5, "&7Name: " + team.getName());
            if (team.getHQ() != null) {
                String homeLocation = infoColor.toString() + "Home: " + team.getHQ().getBlockX() + ", " + team.getHQ().getBlockY() + ", " + team.getHQ().getBlockZ();
                layout.set(1, 6, homeLocation);
            } else {
                layout.set(1, 6, infoColor + "Home: Not Set");
            }
            
            layout.set(1, 7, "&7Balance: $" + (int) team.getBalance());
        } else {
            layout.set(1, 5, "&7None");
        }
        
        layout.set(2, 4, titleColor + "Player Info");
        layout.set(2, 5, "&7Kills: " + Foxtrot.getInstance().getKillsMap().getKills(player.getUniqueId()));
        layout.set(2, 6, "&7Deaths: " + Foxtrot.getInstance().getDeathsMap().getDeaths(player.getUniqueId()));
        layout.set(2, 7, "&7Balance: $" + (int) Foxtrot.getInstance().getWrappedBalanceMap().getBalance(player.getUniqueId()));
        
        layout.set(2, 9, titleColor + "Location");
        
        String location;

        Location loc = player.getLocation();
        Team ownerTeam = LandBoard.getInstance().getTeam(loc);

        if (ownerTeam != null) {
            location = ownerTeam.getName(player.getPlayer());
        } else if (!Foxtrot.getInstance().getServerHandler().isWarzone(loc)) {
            location = ChatColor.GRAY + "The Wilderness";
        } else if (LandBoard.getInstance().getTeam(loc) != null && LandBoard.getInstance().getTeam(loc).getName().equalsIgnoreCase("citadel")) {
            location = titleColor + "Citadel";
        } else {
            location = ChatColor.RED + "Warzone";
        }

        layout.set(2, 11, location);

        /* Getting the direction 4 times a second for each player on the server may be intensive.
        We may want to cache the entire location so it is accessed no more than 1 time per second.
        FIXME, WIP */
        String direction = PlayerDirection.getCardinalDirection(player);
        if (direction != null) {
            layout.set(2, 10, ChatColor.GRAY + "(" + loc.getBlockX() + ", " + loc.getBlockZ() + ") [" + direction + "]");
        } else {
            layout.set(2, 10, ChatColor.GRAY + "(" + loc.getBlockX() + ", " + loc.getBlockZ() + ")");
        }
        
    }

    public static String formatIntoDetailedString(int secs) {
        if (secs <= 60) {
            return "1 minute";
        } else {
            int remainder = secs % 86400;
            int days = secs / 86400;
            int hours = remainder / 3600;
            int minutes = remainder / 60 - hours * 60;
            String fDays = days > 0 ? " " + days + " day" + (days > 1 ? "s" : "") : "";
            String fHours = hours > 0 ? " " + hours + " hour" + (hours > 1 ? "s" : "") : "";
            String fMinutes = minutes > 0 ? " " + minutes + " minute" + (minutes > 1 ? "s" : "") : "";
            return (fDays + fHours + fMinutes).trim();
        }

    }

}
