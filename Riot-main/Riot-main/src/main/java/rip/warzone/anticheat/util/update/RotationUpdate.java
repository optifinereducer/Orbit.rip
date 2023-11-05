package rip.warzone.anticheat.util.update;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;


@Getter
@Setter
public class RotationUpdate extends MovementUpdate {

    public RotationUpdate(Player player, Location to, Location from, PacketPlayInFlying packet) {
        super(player, to, from, packet);
    }

}