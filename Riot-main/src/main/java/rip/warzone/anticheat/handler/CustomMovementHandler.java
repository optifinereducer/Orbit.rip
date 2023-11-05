package rip.warzone.anticheat.handler;

import lombok.AllArgsConstructor;
import net.hylist.handler.MovementHandler;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.AntiCheat;
import rip.warzone.anticheat.check.ICheck;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.BlockPos;
import rip.warzone.anticheat.util.BlockUtil;
import rip.warzone.anticheat.util.update.PositionUpdate;
import rip.warzone.anticheat.util.update.RotationUpdate;

@AllArgsConstructor
public class CustomMovementHandler implements MovementHandler {

    private final AntiCheat plugin;

    public void handleUpdateLocation(Player player, Location to, Location from, PacketPlayInFlying packet) {
        if (player.getAllowFlight()) {
            return;
        }

        if (player.isInsideVehicle()) {
            return;
        }

//		if (to.getY() < 2.0) {
//			return;
//		}

        if (!player.getWorld().isChunkLoaded(to.getBlockX() >> 4, to.getBlockZ() >> 4)) {
            return;
        }

        PlayerData playerData=this.plugin.getPlayerDataManager().getPlayerData(player);

        if (playerData == null) {
            return;
        }

        playerData.updateTimers();

        playerData.setWasOnGround(playerData.isOnGround());
        playerData.setWasInLiquid(playerData.isInLiquid());
        playerData.setWasUnderBlock(playerData.isUnderBlock());
        playerData.setWasInWeb(playerData.isInWeb());
        playerData.setOnGround(BlockUtil.isOnGround(to, 0) || BlockUtil.isOnGround(to, 1));

        if (!playerData.isOnGround()) {
            positions:
            for ( BlockPos position : playerData.getFakeBlocks() ) {
                int x=position.getX();
                int z=position.getZ();

                int blockX=to.getBlock().getX();
                int blockZ=to.getBlock().getZ();

                for ( int xOffset=-1; xOffset <= 1; xOffset++ ) {
                    for ( int zOffset=-1; zOffset <= 1; zOffset++ ) {
                        if (x == blockX + xOffset && z == blockZ + zOffset) {
                            int y=position.getY();
                            int pY=to.getBlock().getY();

                            if (pY - y <= 1 && pY > y) {
                                playerData.setOnGround(true);
                            }

                            if (playerData.isOnGround()) {
                                break positions;
                            }
                        }
                    }
                }
            }
        }

        if (playerData.isOnGround()) {
            playerData.setLastGroundY(to.getY());
            playerData.setAirTicks(0);
        } else playerData.setAirTicks(playerData.getAirTicks() + 1);

        playerData.setInLiquid(BlockUtil.isOnLiquid(to, 0) || BlockUtil.isOnLiquid(to, 1) || BlockUtil.isOnLiquid(to, -1));
        playerData.setInWeb(BlockUtil.isOnWeb(to, 0));
        playerData.setOnIce(BlockUtil.isOnIce(to, 1) || BlockUtil.isOnIce(to, 2));

        if (playerData.isOnIce()) {
            playerData.setMovementsSinceIce(0);
        } else {
            playerData.setMovementsSinceIce(playerData.getMovementsSinceIce() + 1);
        }

        playerData.setOnStairs(BlockUtil.isOnStairs(to, 0) || BlockUtil.isOnStairs(to, 1));
        playerData.setOnLadder(BlockUtil.isLadder(to.getBlock()));
        playerData.setOnCarpet(BlockUtil.isOnCarpet(to, 0) || BlockUtil.isOnCarpet(to, 1));
        playerData.setUnderBlock(BlockUtil.isOnGround(to, -2));

        if (playerData.isUnderBlock()) {
            playerData.setMovementsSinceUnderBlock(0);
        } else {
            playerData.setMovementsSinceUnderBlock(playerData.getMovementsSinceUnderBlock() + 1);
        }

        if (to.getY() != from.getY() && playerData.getVelocityV() > 0) {
            playerData.setVelocityV(playerData.getVelocityV() - 1);
        }

        if (Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ()) > 0.0 && playerData.getVelocityH() > 0) {
            playerData.setVelocityH(playerData.getVelocityH() - 1);
        }

        Class<? extends ICheck>[] checks;
        for ( int length=(checks=PlayerData.CHECKS).length, i=0; i < length; ++i ) {
            Class<? extends ICheck> checkClass=checks[i];
            if (!AntiCheat.instance.getDisabledChecks().contains(checkClass.getSimpleName().toUpperCase())) {
                ICheck check=playerData.getCheck(checkClass);
                if (check != null && check.getType() == PositionUpdate.class) {
                    check.handleCheck(player, new PositionUpdate(player, to, from, packet));
                }
            }
        }

        if (playerData.getVelocityY() > 0.0 && to.getY() > from.getY()) {
            playerData.setVelocityY(0.0);
        }
    }

    public void handleUpdateRotation(Player player, Location to, Location from, PacketPlayInFlying packet) {
        if (player.getAllowFlight()) {
            return;
        }

        PlayerData playerData=this.plugin.getPlayerDataManager().getPlayerData(player);

        if (playerData == null) {
            return;
        }

        double x=Math.abs(Math.abs(to.getX()) - Math.abs(from.getX()));
        double z=Math.abs(Math.abs(to.getZ()) - Math.abs(from.getZ()));
        playerData.setMovementSpeed(Math.sqrt(x * x + z * z));

        for ( Class<? extends ICheck> checkClass : PlayerData.CHECKS ) {
            if (!AntiCheat.instance.getDisabledChecks().contains(checkClass.getSimpleName().toUpperCase())) {
                ICheck check=playerData.getCheck(checkClass);

                if (check != null && check.getType() == RotationUpdate.class) {
                    check.handleCheck(player, new RotationUpdate(player, to, from, packet));
                }
            }
        }
    }

}
