package rip.warzone.anticheat.check;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.AntiCheat;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.event.PlayerAlertEvent;
import rip.warzone.anticheat.event.PlayerBanEvent;
import rip.warzone.anticheat.player.PlayerData;

@AllArgsConstructor
@Getter
public abstract class AbstractCheck<T> implements ICheck<T> {

    protected PlayerData playerData;
    private final Class<T> clazz;
    private final String name;

    @Override
    public Class<? extends T> getType() {
        return this.clazz;
    }

    protected AntiCheat getPlugin() {
        return AntiCheat.instance;
    }

    protected Player getPlayer() {
        return AntiCheat.instance.getServer().getPlayer(this.playerData.getUuid());
    }

    protected double getVl() {
        return this.playerData.getCheckVl(this);
    }

    protected void setVl(double vl) {
        this.playerData.setCheckVl(vl, this);
    }

    protected boolean alert(Player player, AlertType alertType, AlertData[] data, boolean violation) {
        PlayerAlertEvent event=new PlayerAlertEvent(alertType, player, this.name, data);

        playerData.flaggedChecks.add(this);
        this.playerData.addViolation(this);
        getPlugin().getServer().getPluginManager().callEvent(event);

//        if (!event.isCancelled()) {
//            if (violation) {
//                playerData.flaggedChecks.add(this);
//                this.playerData.addViolation(this);
//            }
//            return true;
//        }

        return true;
    }

    protected boolean ban(Player player) {
        this.playerData.setBanning(true);

        PlayerBanEvent event=new PlayerBanEvent(player, this.name);

        this.getPlugin().getServer().getPluginManager().callEvent(event);

        return !event.isCancelled();
    }

    protected void randomBan(Player player, double rate) {
        this.playerData.setRandomBanRate(rate);
        this.playerData.setRandomBanReason(this.name);
        this.playerData.setRandomBan(true);

        getPlugin().getServer().getPluginManager().callEvent(new PlayerAlertEvent(AlertType.RELEASE, player, this.name));
    }

}
