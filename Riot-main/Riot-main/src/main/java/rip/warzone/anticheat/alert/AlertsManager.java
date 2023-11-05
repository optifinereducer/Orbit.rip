package rip.warzone.anticheat.alert;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AlertsManager {

    @Getter
    private final Set<UUID> alertsToggled=new HashSet<>();

    public boolean hasAlertsToggled(Player player) {
        return this.alertsToggled.contains(player.getUniqueId());
    }

    public void toggleAlerts(Player player) {
        if (!this.alertsToggled.remove(player.getUniqueId())) {
            this.alertsToggled.add(player.getUniqueId());
        }
    }

}