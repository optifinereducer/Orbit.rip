package rip.warzone.anticheat.check.impl.badpackets;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import net.minecraft.server.v1_7_R4.PacketPlayInTransaction;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

//Detects most freecams/blinks almost instantly
public class BadPacketsN extends PacketCheck {

    public BadPacketsN(PlayerData playerData) {
        super(playerData, "BadPackets #13");
    }

    private long lastFlying;
    private int vl;

    @Override
    public void handleCheck(Player player, Packet type) {
        if (type instanceof PacketPlayInTransaction) {
            if (Math.abs(System.currentTimeMillis() - lastFlying) > 250L) {
                this.alert(player, AlertType.RELEASE, new AlertData[]{new AlertData("LF",lastFlying)}, true);

                //TODO: Make sure it doesnt false (it shouldnt)
                //if(++vl > 10) this.ban(player);
            }
        } else if (type instanceof PacketPlayInFlying) {
            lastFlying=System.currentTimeMillis(); //Could use ticks, but it really doesnt matter
        }
    }

}
