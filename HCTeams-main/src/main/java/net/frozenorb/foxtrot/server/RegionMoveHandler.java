package net.frozenorb.foxtrot.server;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.util.modsuite.ModUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.player.PlayerMoveEvent;

public interface RegionMoveHandler {

    public static final RegionMoveHandler ALWAYS_TRUE = new RegionMoveHandler() {

        @Override
        public boolean handleMove(PlayerMoveEvent event) {
            return (true);
        }

    };

    RegionMoveHandler PVP_TIMER = event -> {
        Team team = LandBoard.getInstance().getTeam(event.getTo());
        boolean deny = team != null && team.isClaimLocked() && !team.isMember(event.getPlayer().getUniqueId());
        if (CustomTimerCreateCommand.isSOTWTimer() && deny && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            if (!ModUtils.isModMode(event.getPlayer())) {
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot do this that claim is locked and SOTW is active!");
                event.setTo(event.getFrom());
                return (false);
            }
        }

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
            event.getPlayer().sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
            event.setTo(event.getFrom());
            return (false);
        }

        return (true);
    };

    public boolean handleMove(PlayerMoveEvent event);

}