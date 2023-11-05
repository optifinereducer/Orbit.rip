package rip.warzone.anticheat.util.update;

import lombok.Getter;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
public class MovementUpdate {

    private final Player player;
    private final Location to;
    private final Location from;
    private final PacketPlayInFlying packet;

    public MovementUpdate(Player player, Location to, Location from, PacketPlayInFlying packet) {
        this.player=player;
        this.to=to;
        this.from=from;
        this.packet=packet;
    }
}