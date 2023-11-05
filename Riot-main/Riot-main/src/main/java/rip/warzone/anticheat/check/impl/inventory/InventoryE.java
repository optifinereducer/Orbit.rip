package rip.warzone.anticheat.check.impl.inventory;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class InventoryE extends PacketCheck {

    private boolean sent;

    public InventoryE(PlayerData playerData) {
        super(playerData, "Inventory #5");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInWindowClick) {
            if (this.sent) {
                this.alert(player, AlertType.EXPERIMENTAL, new AlertData[0], true);
            }
        } else if (packet instanceof PacketPlayInClientCommand && ((PacketPlayInClientCommand) packet).c() == EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
            this.sent=true;
        } else if (packet instanceof PacketPlayInFlying) {
            this.sent=false;
        }
    }

}
