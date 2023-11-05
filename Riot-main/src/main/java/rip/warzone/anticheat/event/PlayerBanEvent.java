package rip.warzone.anticheat.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class PlayerBanEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST;

    static {
        HANDLER_LIST=new HandlerList();
    }

    private final Player player;
    private final String reason;
    @Setter
    private boolean cancelled;

    public PlayerBanEvent(Player player, String reason) {
        this.player=player;
        this.reason=reason;
    }

    public static HandlerList getHandlerList() {
        return PlayerBanEvent.HANDLER_LIST;
    }

    public HandlerList getHandlers() {
        return PlayerBanEvent.HANDLER_LIST;
    }

}
