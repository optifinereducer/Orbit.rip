package rip.warzone.anticheat.util;

import lombok.Getter;
import rip.warzone.anticheat.player.PlayerData;

/**
 * Created on 22/07/2020 Package me.jumba.sparky.util.time
 */
@Getter
public class EventTimer {

    private int tick;
    private final int max;

    private final PlayerData user;

    public EventTimer(int max, PlayerData user) {
        this.tick=0;
        this.max=max;
        this.user=user;
    }

    public boolean hasNotPassed(int ctick) {
        return (this.user.currentTick > ctick && (this.user.currentTick - tick) < ctick);
    }

    public boolean passed(int ctick) {
        return (this.user.currentTick > ctick && (this.user.currentTick - tick) > ctick);
    }

    public boolean hasNotPassed() {
        return (this.user.currentTick > this.max && (this.user.currentTick - tick) < this.max);
    }

    public boolean passed() {
        return (this.user.currentTick > this.max && (this.user.currentTick - tick) > this.max);
    }

    public void reset() {
        this.tick=this.user.currentTick;
    }
}
