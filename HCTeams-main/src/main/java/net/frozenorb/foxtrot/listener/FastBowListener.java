package net.frozenorb.foxtrot.listener;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FastBowListener implements Listener {

    private Map<UUID, Long> lastFire = Maps.newHashMap();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileLaunch(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player shooter = (Player) event.getEntity();
        Long lastFired = this.lastFire.get(shooter.getUniqueId());
        if (lastFired != null && System.currentTimeMillis() - lastFired.longValue() < 500L) {
            event.setCancelled(true);
            this.lastFire.put(shooter.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
            return;
        }

        this.lastFire.put(shooter.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.lastFire.remove(event.getPlayer().getUniqueId());
    }

}
