package rip.warzone.anticheat.check.impl.inventory;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class InventoryG extends PacketCheck {

    private boolean sent;
    private boolean vehicle;

    public InventoryG(PlayerData playerData) {
        super(playerData, "Inventory #7");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInFlying) {
            if (this.sent) {
                this.alert(player, AlertType.EXPERIMENTAL, new AlertData[0], true);
            }
            this.vehicle=false;
            this.sent=false;
        } else if (packet instanceof PacketPlayInClientCommand && ((PacketPlayInClientCommand) packet).c() == EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
            if (this.playerData.isSprinting() && !this.vehicle) {
                this.sent=true;
            }
        } else if (packet instanceof PacketPlayInEntityAction && ((PacketPlayInEntityAction) packet).d() == InventoryG.STOP_SPRINTING) {
            this.sent=false;
        } else if (packet instanceof PacketPlayInSteerVehicle) {
            this.vehicle=true;
        }
    }

}
