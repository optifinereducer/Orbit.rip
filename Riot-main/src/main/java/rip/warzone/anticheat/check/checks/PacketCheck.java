package rip.warzone.anticheat.check.checks;

import net.minecraft.server.v1_7_R4.Packet;
import rip.warzone.anticheat.check.AbstractCheck;
import rip.warzone.anticheat.player.PlayerData;

public abstract class PacketCheck extends AbstractCheck<Packet> {

    public static int START_SNEAKING=1;
    public static int STOP_SNEAKING=2;
    public static int START_SPRINTING=4;
    public static int STOP_SPRINTING=5;
    public static int START_DESTROY_BLOCK=0;
    public static int ABORT_DESTROY_BLOCK=1;
    public static int STOP_DESTROY_BLOCK=2;
    public static int DROP_ALL_ITEMS=3;
    public static int DROP_ITEM=4;
    public static int RELEASE_USE_ITEM=5;

    public PacketCheck(PlayerData playerData, String name) {
        super(playerData, Packet.class, name);
    }

}
