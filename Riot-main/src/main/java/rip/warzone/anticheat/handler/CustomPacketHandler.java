package rip.warzone.anticheat.handler;

import lombok.AllArgsConstructor;
import net.hylist.handler.PacketHandler;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.AntiCheat;
import rip.warzone.anticheat.check.ICheck;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.client.EnumClientType;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.event.PlayerAlertEvent;
import rip.warzone.anticheat.event.PlayerBanEvent;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.BlockPos;
import rip.warzone.anticheat.util.CustomLocation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class CustomPacketHandler implements PacketHandler {

    private final AntiCheat plugin;
    private static final List<Material> instantBreakTypes=Arrays.asList(Material.LONG_GRASS, Material.WATER_LILY, Material.DEAD_BUSH, Material.YELLOW_FLOWER, Material.RED_ROSE, Material.DOUBLE_PLANT, Material.SUGAR_CANE, Material.SUGAR_CANE_BLOCK);

    @Override
    public void handleReceivedPacket(PlayerConnection playerConnection, Packet packet) {
        try {
            Player player=playerConnection.getPlayer();
            PlayerData playerData=this.plugin.getPlayerDataManager().getPlayerData(player);

            playerData.getPackets().put(packet.getClass(), playerData.getPackets().getOrDefault(packet.getClass(), 0) + 1);

            if (playerData == null) {
                return;
            }

            if (playerData.isSniffing()) {
                this.handleSniffedPacket(packet, playerData);
            }

            String simpleName=packet.getClass().getSimpleName();

            switch (simpleName) {
                case "PacketPlayInCustomPayload": {
                    if (!playerData.getClient().isHacked()) {
                        this.handleCustomPayload((PacketPlayInCustomPayload) packet, playerData, player);
                        break;
                    }

                    break;
                }
                case "PacketPlayInPosition":
                case "PacketPlayInPositionLook":
                case "PacketPlayInLook":
                case "PacketPlayInFlying": {
                    this.handleFlyPacket((PacketPlayInFlying) packet, playerData);
                    break;
                }
                case "PacketPlayInKeepAlive": {
                    this.handleKeepAlive((PacketPlayInKeepAlive) packet, playerData, player);
                    break;
                }
                case "PacketPlayInUseEntity": {
                    this.handleUseEntity((PacketPlayInUseEntity) packet, playerData, player);
                    break;
                }
                case "PacketPlayInBlockPlace": {
                    playerData.setPlacing(true);
                    break;
                }
                case "PacketPlayInCloseWindow": {
                    playerData.setInventoryOpen(false);
                    break;
                }
                case "PacketPlayInClientCommand": {
                    if (((PacketPlayInClientCommand) packet).c() == EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
                        playerData.setInventoryOpen(true);
                    }

                    break;
                }
                case "PacketPlayInEntityAction": {
                    int action=((PacketPlayInEntityAction) packet).d();

                    if (action == PacketCheck.START_SPRINTING) {
                        playerData.setSprinting(true);
                        break;
                    }

                    if (action == PacketCheck.STOP_SPRINTING) {
                        playerData.setSprinting(false);
                    }

                    break;
                }
                case "PacketPlayInBlockDig": {
                    PacketPlayInBlockDig blockDig=(PacketPlayInBlockDig) packet;
                    int digType=blockDig.g();
                    int x=blockDig.c();
                    int y=blockDig.d();
                    int z=blockDig.e();

                    if (playerData.getFakeBlocks().contains(new BlockPos(x, y, z))) {
                        playerData.setFakeDigging(true);
                        playerData.setDigging(false);
                    } else {
                        playerData.setFakeDigging(false);

                        if (digType == PacketCheck.START_DESTROY_BLOCK) {
                            Material type=player.getWorld().isChunkLoaded(x >> 4, z >> 4) ? player.getWorld().getBlockAt(x, y, z).getType() : null;
                            playerData.setInstantBreakDigging(instantBreakTypes.contains(type));
                            playerData.setDigging(true);
                        } else if (digType == PacketCheck.ABORT_DESTROY_BLOCK || digType == PacketCheck.STOP_DESTROY_BLOCK) {
                            playerData.setInstantBreakDigging(false);
                            playerData.setDigging(false);
                        }
                    }

                    break;
                }
                case "PacketPlayInArmAnimation": {
                    playerData.setLastAnimationPacket(System.currentTimeMillis());
                    break;
                }
            }

            for ( Class<? extends ICheck> checkClass : PlayerData.CHECKS ) {
                if (!AntiCheat.instance.getDisabledChecks().contains(checkClass.getSimpleName().toUpperCase())) {
                    ICheck check=(playerData.getCheck(checkClass));

                    if (check != null && check.getType() == Packet.class) {
                        check.handleCheck(playerConnection.getPlayer(), packet);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleSentPacket(PlayerConnection playerConnection, Packet packet) {
        try {
            Player player=playerConnection.getPlayer();
            PlayerData playerData=this.plugin.getPlayerDataManager().getPlayerData(player);

            if (playerData == null) {
                return;
            }

            String simpleName=packet.getClass().getSimpleName();

            switch (simpleName) {
                case "PacketPlayOutRelEntityMoveLook": {
                    break;
                }
                case "PacketPlayOutEntityLook": {
                    break;
                }
                case "PacketPlayOutRelEntityMove": {
                    break;
                }
                case "PacketPlayOutEntity": {
                    break;
                }
                case "PacketPlayOutEntityVelocity": {
                    this.handleVelocityOut((PacketPlayOutEntityVelocity) packet, playerData, player);
                    return;
                }
                case "PacketPlayOutCloseWindow": {
                    if (!playerData.keepAliveExists(-1)) {
                        playerConnection.sendPacket(new PacketPlayOutKeepAlive(-1));
                    }
                    return;
                }
                case "PacketPlayOutPosition": {
                    this.handlePositionPacket((PacketPlayOutPosition) packet, playerData);
                    return;
                }
                case "PacketPlayOutBlockChange": {
                    PacketPlayOutBlockChange blockChange=(PacketPlayOutBlockChange) packet;

                    if (!blockChange.fake) {
                        return;
                    }

                    if (blockChange.block != Blocks.AIR) {
                        playerData.getFakeBlocks().add(new BlockPos(blockChange.a, blockChange.b, blockChange.c));
                        return;
                    }

                    playerData.getFakeBlocks().remove(new BlockPos(blockChange.a, blockChange.b, blockChange.c));
                    return;
                }
                case "PacketPlayOutExplosion": {
                    this.handleExplosionPacket((PacketPlayOutExplosion) packet, playerData);
                    return;
                }
                case "PacketPlayOutEntityTeleport": {
                    this.handleTeleportPacket((PacketPlayOutEntityTeleport) packet, playerData, player);
                    return;
                }
                case "PacketPlayOutKeepAlive": {
                    playerData.addKeepAliveTime(((PacketPlayOutKeepAlive) packet).getA());
                    return;
                }
            }

            if (PacketPlayOutEntity.class.isAssignableFrom(packet.getClass())) {
                this.handleEntityPacket((PacketPlayOutEntity) packet, playerData, player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSniffedPacket(Packet packet, PlayerData playerData) {
        try {
            StringBuilder builder=new StringBuilder();

            builder.append(packet.getClass().getSimpleName());
            builder.append(" (timestamp = ");
            builder.append(System.currentTimeMillis());

            List<Field> fieldsList=new ArrayList<>();
            fieldsList.addAll(Arrays.asList(packet.getClass().getDeclaredFields()));
            fieldsList.addAll(Arrays.asList(packet.getClass().getSuperclass().getDeclaredFields()));

            for ( Field field : fieldsList ) {
                if (field.getName().equalsIgnoreCase("timestamp")) {
                    continue;
                }

                field.setAccessible(true);
                builder.append(", ");
                builder.append(field.getName());
                builder.append(" = ");
                builder.append(field.get(packet));
            }

            builder.append(")");

            playerData.getSniffedPacketBuilder().append(builder.toString());
            playerData.getSniffedPacketBuilder().append("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCustomPayload(PacketPlayInCustomPayload packet, PlayerData playerData, Player player) {
        String payloadTag=packet.c();

        for ( EnumClientType clientType : EnumClientType.values() ) {
            if (clientType.getPayloadTag() != null) {
                if (clientType.getPayloadTag().equals(payloadTag)) {
                    playerData.setClient(clientType);
                    break;
                }
            }
        }

        if (payloadTag.equals("REGISTER")) {
            try {
                String registerType=new String(packet.e());

                if (registerType.contains("CB-Client")) {
                    playerData.setClient(EnumClientType.CHEAT_BREAKER);
                } else if (registerType.contains("Lunar-Client")) {
                    playerData.setClient(EnumClientType.Lunar_Client);
                } else if (registerType.equalsIgnoreCase("CC")) {
                    playerData.setClient(EnumClientType.COSMIC_CLIENT);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        if (playerData.getClient() != null && playerData.getClient().isHacked()) {
            plugin.getServer().getPluginManager().callEvent(new PlayerAlertEvent(AlertType.RELEASE, player, playerData.getClient().getName()));

            playerData.setRandomBanRate(500.0);
            playerData.setRandomBanReason(playerData.getClient().getName());
            playerData.setRandomBan(true);
        }
    }

    private void handleFlyPacket(PacketPlayInFlying packet, PlayerData playerData) {
        CustomLocation toLocation=new CustomLocation(packet.c(), packet.d(), packet.e(), packet.g(), packet.h());
        CustomLocation fromLocation=playerData.getLastMovePacket();

        playerData.currentTick++;

        if (playerData.getTeleportTicks() > 0) {
            playerData.setTeleportTicks(playerData.getTeleportTicks() - 1);
        }
        if (playerData.getTeleportTicks() < 0) {
            playerData.setTeleportTicks(0);
        }

        if (playerData.getRespawnTicks() > 0) {
            playerData.setRespawnTicks(playerData.getRespawnTicks() - 1);
        }
        if (playerData.getRespawnTicks() < 0) {
            playerData.setRespawnTicks(0);
        }
        if (packet.j()) {
            playerData.setStandTicks(0);

        } else {
            playerData.setStandTicks(playerData.getStandTicks() + 1);
        }

        if (fromLocation != null) {
            if (!packet.j()) {
                toLocation.setX(fromLocation.getX());
                toLocation.setY(fromLocation.getY());
                toLocation.setZ(fromLocation.getZ());
            }

            if (!packet.k()) {
                toLocation.setYaw(fromLocation.getYaw());
                toLocation.setPitch(fromLocation.getPitch());
            }

            if (System.currentTimeMillis() - fromLocation.getTimestamp() > 110L) {
                playerData.setLastDelayedMovePacket(System.currentTimeMillis());
            }
        }

        if (playerData.isSetInventoryOpen()) {
            playerData.setInventoryOpen(false);
            playerData.setSetInventoryOpen(false);
        }

        playerData.setLastMovePacket(toLocation);
        playerData.setPlacing(false);
        playerData.setAllowTeleport(false);

        if (packet instanceof PacketPlayInPositionLook && playerData.allowTeleport(toLocation)) {
            playerData.setAllowTeleport(true);
            playerData.setTeleportTicks(10);
        }
    }


    private void handleKeepAlive(PacketPlayInKeepAlive packet, PlayerData playerData, Player player) {
        int id=packet.c();

        if (playerData.keepAliveExists(id)) {
            if (id == -1) {
                playerData.setSetInventoryOpen(true);
            } else {
                playerData.setPing(System.currentTimeMillis() - playerData.getKeepAliveTime(id));
            }

            playerData.removeKeepAliveTime(id);
        } else if (id != 0) {
            //Fixed falses on join
            if (playerData.InvalidKeepAlivesVerbose++ > 0) {
                PlayerAlertEvent alertEvent=new PlayerAlertEvent(AlertType.RELEASE, player, "Illegal Keep Alive");
                plugin.getServer().getPluginManager().callEvent(alertEvent);
            }
        }
    }

    private void handleUseEntity(PacketPlayInUseEntity packet, PlayerData playerData, Player player) {


        if (packet.c() == EnumEntityUseAction.ATTACK) {
            playerData.setLastAttackPacket(System.currentTimeMillis());

            if (!playerData.isAttackedSinceVelocity()) {
                playerData.setVelocityX(playerData.getVelocityX() * 0.6);
                playerData.setVelocityZ(playerData.getVelocityZ() * 0.6);
                playerData.setAttackedSinceVelocity(true);
            }

            if (!playerData.isBanning() && playerData.isRandomBan() && Math.random() * playerData.getRandomBanRate() < 1.0) {
                playerData.setBanning(true);

                PlayerBanEvent event=new PlayerBanEvent(player, playerData.getRandomBanReason());
                plugin.getServer().getPluginManager().callEvent(event);
            }

            Entity targetEntity=packet.a(((CraftPlayer) player).getHandle().getWorld());

            if (targetEntity instanceof EntityPlayer) {
                Player target=(Player) targetEntity.getBukkitEntity();
                playerData.setLastTarget(target.getUniqueId());
                playerData.setLastTargetEntity(target);
            }
        }
    }

    private void handleVelocityOut(PacketPlayOutEntityVelocity packet, PlayerData playerData, Player player) {
        if (packet.a == player.getEntityId()) {
            double x=Math.abs(packet.b / 8000.0);
            double y=packet.c / 8000.0;
            double z=Math.abs(packet.d / 8000.0);

            if (x > 0.0 || z > 0.0) {
                playerData.setVelocityH((int) (((x + z) / 2.0 + 2.0) * 15.0));
            }

            if (y > 0.0) {
                playerData.setVelocityV((int) (Math.pow(y + 2.0, 2.0) * 5.0));

                if (playerData.isOnGround() && player.getLocation().getY() % 1.0 == 0.0) {
                    playerData.setVelocityX(x);
                    playerData.setVelocityY(y);
                    playerData.setVelocityZ(z);
                    playerData.getVelocityTimer().reset();
                    playerData.setLastVelocity(System.currentTimeMillis());
                    playerData.setAttackedSinceVelocity(false);
                }
            }
        }
    }

    private void handleExplosionPacket(PacketPlayOutExplosion packet, PlayerData playerData) {
        float x=Math.abs(packet.f);
        float y=packet.g;
        float z=Math.abs(packet.h);

        if (x > 0.0f || z > 0.0f) {
            playerData.setVelocityH((int) (((x + z) / 2.0f + 2.0f) * 15.0f));
        }
        if (y > 0.0f) {
            playerData.setVelocityV((int) (Math.pow(y + 2.0f, 2.0) * 5.0));
        }
    }

    private void handleEntityPacket(PacketPlayOutEntity packet, PlayerData playerData, Player player) {
        Entity targetEntity=((CraftPlayer) player).getHandle().getWorld().getEntity(packet.a);

        if (targetEntity instanceof EntityPlayer) {
            Player target=(Player) targetEntity.getBukkitEntity();
            CustomLocation customLocation=playerData.getLastPlayerPacket(target.getUniqueId(), 1);

            if (customLocation != null) {
                double x=packet.b / 32.0;
                double y=packet.c / 32.0;
                double z=packet.d / 32.0;
                float yaw=packet.e * 360.0f / 256.0f;
                float pitch=packet.f * 360.0f / 256.0f;

                if (!packet.g) {
                    yaw=customLocation.getYaw();
                    pitch=customLocation.getPitch();
                }

                playerData.addPlayerPacket(target.getUniqueId(), new CustomLocation(customLocation.getX() + x, customLocation.getY() + y, customLocation.getZ() + z, yaw, pitch));
            }
        }
    }

    private void handleTeleportPacket(PacketPlayOutEntityTeleport packet, PlayerData playerData, Player player) {
        Entity targetEntity=((CraftPlayer) player).getHandle().getWorld().getEntity(packet.a);

        if (targetEntity instanceof EntityPlayer) {
            Player target=(Player) targetEntity.getBukkitEntity();
            double x=packet.b / 32.0;
            double y=packet.c / 32.0;
            double z=packet.d / 32.0;
            float yaw=packet.e * 360.0f / 256.0f;
            float pitch=packet.f * 360.0f / 256.0f;
            playerData.addPlayerPacket(target.getUniqueId(), new CustomLocation(x, y, z, yaw, pitch));
        }
    }

    private void handlePositionPacket(PacketPlayOutPosition packet, PlayerData playerData) {
        if (packet.e > 90.0f) {
            packet.e=90.0f;
        } else if (packet.e < -90.0f) {
            packet.e=-90.0f;
        } else if (packet.e == 0.0f) {
            packet.e=0.492832f;
        }

        playerData.setVelocityY(0.0);
        playerData.setVelocityX(0.0);
        playerData.setVelocityZ(0.0);
        playerData.setAttackedSinceVelocity(false);
        playerData.addTeleportLocation(new CustomLocation(packet.a, packet.b, packet.c, packet.d, packet.e));
    }

    private float getAngle(double posX, double posZ, CustomLocation location) {
        double x=posX - location.getX();
        double z=posZ - location.getZ();
        float newYaw=(float) Math.toDegrees(-Math.atan(x / z));

        if (z < 0.0 && x < 0.0) {
            newYaw=(float) (90.0 + Math.toDegrees(Math.atan(z / x)));
        } else if (z < 0.0 && x > 0.0) {
            newYaw=(float) (-90.0 + Math.toDegrees(Math.atan(z / x)));
        }

        return newYaw;
    }

}
