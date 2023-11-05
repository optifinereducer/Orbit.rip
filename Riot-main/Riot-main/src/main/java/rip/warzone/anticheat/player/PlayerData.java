package rip.warzone.anticheat.player;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_7_R4.Packet;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.AntiCheat;
import rip.warzone.anticheat.check.AbstractCheck;
import rip.warzone.anticheat.check.ICheck;
import rip.warzone.anticheat.check.impl.aimassist.cinematic.Cinematic;
import rip.warzone.anticheat.check.impl.aimassist.sensitivity.Sensitivity;
import rip.warzone.anticheat.check.impl.doubleclick.DoubleClickA;
import rip.warzone.anticheat.check.impl.invalid.InvalidA;
import rip.warzone.anticheat.check.impl.invalid.InvalidB;
import rip.warzone.anticheat.check.impl.motion.MotionA;
import rip.warzone.anticheat.check.impl.motion.MotionB;
import rip.warzone.anticheat.check.impl.range.RangeA;
import rip.warzone.anticheat.check.impl.scaffold.ScaffoldB;
import rip.warzone.anticheat.check.impl.scaffold.ScaffoldC;
import rip.warzone.anticheat.check.impl.timer.TimerA;
import rip.warzone.anticheat.check.impl.timer.TimerB;
import rip.warzone.anticheat.check.impl.velocity.VelocityA;
import rip.warzone.anticheat.check.impl.velocity.VelocityB;
import rip.warzone.anticheat.check.impl.velocity.VelocityC;
import rip.warzone.anticheat.client.ClientType;
import rip.warzone.anticheat.client.EnumClientType;
import rip.warzone.anticheat.util.BlockPos;
import rip.warzone.anticheat.util.CustomLocation;
import rip.warzone.anticheat.util.EventTimer;
import rip.warzone.anticheat.util.VelocityTracker;
import rip.warzone.anticheat.check.impl.aimassist.*;
import rip.warzone.anticheat.check.impl.autoclicker.*;
import rip.warzone.anticheat.check.impl.badpackets.*;
import rip.warzone.anticheat.check.impl.fly.*;
import rip.warzone.anticheat.check.impl.inventory.*;
import rip.warzone.anticheat.check.impl.killaura.*;
import rip.warzone.anticheat.check.impl.speed.*;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Setter
@Getter
public class PlayerData {

    private static Map<Class<? extends ICheck>, Constructor<? extends ICheck>> CONSTRUCTORS;
    public static Class<? extends ICheck>[] CHECKS;

    private UUID uuid;
    private Set<UUID> playersWatching;
    private Map<String, String> forgeMods;
    private CustomLocation lastMovePacket;
    private ClientType client= EnumClientType.VANILLA;
    private UUID lastTarget;
    private Entity lastTargetEntity;
    private String randomBanReason;
    private Map<Class<? extends Packet>, Integer> packets = new HashMap<>();

    private VelocityTracker velocityTracker=new VelocityTracker();
    public ArrayList<AbstractCheck> flaggedChecks=new ArrayList<>();
    private Set<CustomLocation> teleportLocations=Collections.newSetFromMap(new ConcurrentHashMap<>());
    private Map<Class<? extends ICheck>, ICheck> checkMap=new HashMap<>();
    private Map<Integer, Long> keepAliveTimes=new HashMap<>();
    private Map<ICheck, Double> checkVlMap=new HashMap<>();
    private Set<BlockPos> fakeBlocks=new HashSet<>();
    private StringBuilder sniffedPacketBuilder=new StringBuilder();
    private Map<UUID, List<CustomLocation>> recentPlayerPackets=new HashMap<>();
    private Map<ICheck, Set<Long>> checkViolationTimes=new HashMap<>();

    private boolean randomBan;
    private boolean allowTeleport;
    private boolean cinematic;
    private boolean inventoryOpen;
    private boolean setInventoryOpen;
    private boolean attackedSinceVelocity;
    private boolean underBlock;
    private boolean sprinting;
    private boolean inLiquid;
    private boolean instantBreakDigging;
    private boolean fakeDigging;
    private boolean onGround;
    private boolean sniffing;
    private boolean onStairs;
    private boolean onLadder;
    private boolean onCarpet;
    private boolean placing;
    private boolean banning;
    private boolean digging;
    private boolean inWeb;
    private boolean onIce;
    private boolean verifyingSensitivity;
    private double sensitivity;
    private boolean wasUnderBlock;
    private boolean wasOnGround;
    private boolean wasInLiquid;
    private boolean wasInWeb;
    public boolean devalerts=false;
    public boolean staffalerts=false;

    private double movementSpeed;
    private double lastGroundY;
    private double LastDistanceY;
    private double randomBanRate;
    private double velocityX;
    private double velocityY;
    private double velocityZ;

    private long lastAttack;
    private long lastDelayedMovePacket;
    private long lastAnimationPacket;
    private long lastAttackPacket;
    private long lastVelocity;
    public long lastFlag, addedToBanwave;
    private long ping;

    public int violations;
    private int LastAttackTime;
    private int velocityH;
    private int AirTicks;
    public int currentTick;
    private int deathTicks;
    private int GroundTicks;
    private int RespawnTicks;
    private int teleportTicks;
    private int standTicks;
    private int velocityV;
    private int lastCps;
    private int LastTeleportTime;
    private int movementsSinceIce;
    private int movementsSinceUnderBlock;
    public int InvalidKeepAlivesVerbose;

    public boolean hasLooked;

    private EventTimer velocityTimer;
    private EventTimer iceTimer;
    private EventTimer blockAboveTimer;

    public PlayerData(UUID uuid) {
        this.uuid=uuid;
        this.playersWatching=new HashSet<>();

        this.setupEventTimers();

        AntiCheat.instance.getServer().getScheduler().runTaskAsynchronously(AntiCheat.instance, () -> {
            CONSTRUCTORS.keySet().stream().map(o -> (Class<? extends ICheck>) o).forEach(check -> {
                Constructor<? extends ICheck> constructor=CONSTRUCTORS.get(check);

                try {
                    this.checkMap.put(check, constructor.newInstance(this));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });
    }

    private void setupEventTimers() {
        this.velocityTimer=new EventTimer(20, this);
        this.iceTimer=new EventTimer(20, this);
        this.blockAboveTimer=new EventTimer(20, this);
    }


    public <T extends ICheck> T getCheck(Class<T> clazz) {
        return (T) this.checkMap.get(clazz);
    }

    public AbstractCheck getCheckByName(String name) {
        for ( AbstractCheck check : getFlaggedChecks() ) {
            if (check.getName().equalsIgnoreCase(name)) {
                return check;
            }
        }
        return null;
    }

    public CustomLocation getLastPlayerPacket(UUID playerUUID, int index) {
        List<CustomLocation> customLocations=this.recentPlayerPackets.get(playerUUID);

        if (customLocations != null && customLocations.size() > index) {
            return customLocations.get(customLocations.size() - index);
        }

        return null;
    }

    public void updateTimers() {
        if (this.onIce) {
            this.iceTimer.reset();
        }

        if (this.isUnderBlock()) {
            this.blockAboveTimer.reset();
        }
    }

    public boolean isLagging() {
        long now=System.currentTimeMillis();
        return now - lastDelayedMovePacket < 220L || teleportTicks > 0;
    }

    public void addPlayerPacket(UUID playerUUID, CustomLocation customLocation) {
        List<CustomLocation> customLocations=this.recentPlayerPackets.get(playerUUID);

        if (customLocations == null) {
            customLocations=new ArrayList<>();
        }

        if (customLocations.size() == 20) {
            customLocations.remove(0);
        }

        customLocations.add(customLocation);

        this.recentPlayerPackets.put(playerUUID, customLocations);
    }

    public void addTeleportLocation(CustomLocation teleportLocation) {
        this.teleportLocations.add(teleportLocation);
    }

    public boolean allowTeleport(CustomLocation teleportLocation) {
        for ( CustomLocation customLocation : this.teleportLocations ) {
            double delta=Math.pow(teleportLocation.getX() - customLocation.getX(), 2.0) +
                    Math.pow(teleportLocation.getZ() - customLocation.getZ(), 2.0);
            if (delta <= 0.005) {
                this.teleportLocations.remove(customLocation);
                return true;
            }
        }
        return false;
    }

    public void setDeathTicks(int deathTicks) {
        this.deathTicks=deathTicks;
    }


    public double getCheckVl(ICheck check) {
        if (!this.checkVlMap.containsKey(check)) {
            this.checkVlMap.put(check, 0.0);
        }

        return this.checkVlMap.get(check);
    }

    public void setCheckVl(double vl, ICheck check) {
        if (vl < 0.0) {
            vl=0.0;
        }

        this.checkVlMap.put(check, vl);
    }


    public boolean keepAliveExists(int id) {
        return this.keepAliveTimes.containsKey(id);
    }

    public long getKeepAliveTime(int id) {
        return this.keepAliveTimes.get(id);
    }

    public void removeKeepAliveTime(int id) {
        this.keepAliveTimes.remove(id);
    }


    public boolean isPlayerWatching(Player player) {
        return this.playersWatching.contains(player.getUniqueId());
    }

    public void togglePlayerWatching(Player player) {
        if (!this.playersWatching.remove(player.getUniqueId())) {
            this.playersWatching.add(player.getUniqueId());
        }
    }

    public void addKeepAliveTime(int id) {
        this.keepAliveTimes.put(id, System.currentTimeMillis());
    }

    public int getViolations(ICheck check, Long time) {
        Set<Long> timestamps=this.checkViolationTimes.get(check);

        if (timestamps != null) {
            int violations=0;

            for ( long timestamp : timestamps ) {
                if (System.currentTimeMillis() - timestamp <= time) {
                    ++violations;
                }
            }

            return violations;
        }

        return 0;
    }

    public int getViolations(ICheck check) {
        Set<Long> logs=this.checkViolationTimes.get(check);
        return logs.size();
    }

    public void addViolation(ICheck check) {
        Set<Long> timestamps=this.checkViolationTimes.get(check);

        if (timestamps == null) {
            timestamps=new HashSet<>();
        }

        timestamps.add(System.currentTimeMillis());

        this.checkViolationTimes.put(check, timestamps);
    }

    static {
        List<Class<? extends ICheck>> checks=Arrays.asList(
                Cinematic.class, Sensitivity.class,

                AimAssistA.class, AimAssistB.class, AimAssistC.class, AimAssistD.class,
                AimAssistE.class, AimAssistF.class, AimAssistG.class, AimAssistH.class,
                //AimAssistI.class, AimAssistJ.class,

                DoubleClickA.class,

                AutoClickerA.class, AutoClickerB.class, AutoClickerC.class, AutoClickerD.class,
                AutoClickerE.class, AutoClickerF.class, AutoClickerG.class, AutoClickerH.class,
                AutoClickerI.class, AutoClickerJ.class, AutoClickerK.class, AutoClickerK.class,
                AutoClickerL.class, AutoClickerN.class, AutoClickerO.class,
                AutoClickerP.class, AutoClickerQ.class, AutoClickerR.class,

                BadPacketsA.class, BadPacketsB.class, BadPacketsC.class, BadPacketsD.class,
                BadPacketsE.class, BadPacketsF.class, BadPacketsG.class, BadPacketsH.class,
                BadPacketsI.class, BadPacketsJ.class, BadPacketsL.class, BadPacketsM.class,
                BadPacketsN.class,

                FlyA.class, FlyB.class, FlyC.class, FlyE.class, FlyC.class, FlyD.class,
                FlyE.class,

                SpeedA.class, SpeedB.class, SpeedC.class, SpeedD.class, SpeedE.class,
                SpeedF.class, SpeedG.class,

                InventoryA.class, InventoryB.class, InventoryC.class, InventoryD.class,
                InventoryE.class, InventoryF.class, InventoryG.class,

                KillAuraA.class, KillAuraB.class, KillAuraC.class, KillAuraD.class,
                KillAuraE.class, KillAuraF.class, KillAuraG.class, KillAuraH.class,
                KillAuraI.class, KillAuraJ.class, KillAuraK.class, KillAuraL.class,
                KillAuraM.class, KillAuraN.class, KillAuraO.class, KillAuraP.class,
                KillAuraQ.class, KillAuraR.class, KillAuraS.class, KillAuraT.class,

                InvalidA.class, InvalidB.class,

                MotionA.class, MotionB.class,

                RangeA.class,

                TimerA.class, TimerB.class,

                VelocityA.class, VelocityB.class, VelocityC.class,

                ScaffoldB.class, ScaffoldC.class
        );

        CHECKS=checks.toArray(new Class[checks.size()]);

        CONSTRUCTORS=new ConcurrentHashMap<>();

        for ( Class<? extends ICheck> check : PlayerData.CHECKS ) {
            try {
                PlayerData.CONSTRUCTORS.put(check, check.getConstructor(PlayerData.class));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
}
