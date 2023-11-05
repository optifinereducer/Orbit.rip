package rip.warzone.anticheat.check.impl.inventory;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class InventoryF extends PacketCheck {

    private boolean sent;

    public InventoryF(PlayerData playerData) {
        super(playerData, "Inventory #6");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInCloseWindow) {
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
