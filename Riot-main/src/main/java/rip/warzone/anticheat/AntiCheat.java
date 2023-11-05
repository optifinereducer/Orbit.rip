package rip.warzone.anticheat;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.hylist.HylistSpigot;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import org.bukkit.plugin.java.JavaPlugin;
import rip.warzone.anticheat.alert.AlertsManager;
import rip.warzone.anticheat.banwave.BanWave;
import rip.warzone.anticheat.banwave.BanWaveManager;
import rip.warzone.anticheat.command.*;
import rip.warzone.anticheat.handler.CustomMovementHandler;
import rip.warzone.anticheat.handler.CustomPacketHandler;
import rip.warzone.anticheat.listener.PlayerListener;
import rip.warzone.anticheat.listener.XRayListener;
import rip.warzone.anticheat.log.LogExportRunnable;
import rip.warzone.anticheat.log.LogManager;
import rip.warzone.anticheat.player.PlayerDataManager;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AntiCheat extends JavaPlugin {

    public static AntiCheat instance;

    private PlayerDataManager playerDataManager;
    private AlertsManager alertsManager;
    private LogManager logManager;

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    private final double rangeVl=10;
    private final List<String> disabledChecks=new ArrayList<>();

    public static AntiCheat instances;
    public BanWaveManager banWaveManager;

    @Override
    public void onEnable() {
        instance=this;
        banWaveManager=new BanWaveManager();
        new BanWave();
        saveDefaultConfig();
        registerHandlers();
        registerManagers();
        registerListeners();
        registerCommands();
        registerExportLogsTimer();
        registerDatabase();

        FrozenCommandHandler.registerAll(this);

    }

    @Override
    public void onDisable() {
        getLogManager().exportAllLogs();
        mongoClient.close();
    }

    public boolean isAntiCheatEnabled() {
        return MinecraftServer.getServer().recentTps[0] > 19.0;
    }

    private void registerHandlers() {
        HylistSpigot.INSTANCE.addPacketHandler(new CustomPacketHandler(this));
        HylistSpigot.INSTANCE.addMovementHandler(new CustomMovementHandler(this));
    }

    private void registerManagers() {
        alertsManager=new AlertsManager();
        playerDataManager=new PlayerDataManager();
        logManager=new LogManager();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new BanWaveCommand(), this);
        getServer().getPluginManager().registerEvents(new XRayListener(), this);
    }

    private void registerCommands() {
        FrozenCommandHandler.registerClass(BanCommand.class);
        FrozenCommandHandler.registerClass(InfoCommand.class);
        FrozenCommandHandler.registerClass(ClientCommand.class);
        FrozenCommandHandler.registerClass(LogsCommand.class);
        FrozenCommandHandler.registerClass(BanWaveCommand.class);
        FrozenCommandHandler.registerClass(StaffAlertsCommand.class);
        FrozenCommandHandler.registerClass(XRayAlertsCommand.class);
    }

    private void registerExportLogsTimer() {
        getServer().getScheduler().runTaskTimerAsynchronously(this, new LogExportRunnable(null), 600L, 600L);
    }

    private void registerDatabase() {
        if (getConfig().getBoolean("Mongo.Authentication.Enabled")) {
            ServerAddress serverAddress=new ServerAddress(
                    getConfig().getString("Mongo.Host"),
                    getConfig().getInt("Mongo.Port")
            );

            MongoCredential credential=MongoCredential.createCredential(
                    getConfig().getString("Mongo.Authentication.Username"),
                    "admin",
                    getConfig().getString("Mongo.Authentication.Password").toCharArray()
            );

            mongoClient=new MongoClient(serverAddress, credential, MongoClientOptions.builder().build());
        } else {
            mongoClient=new MongoClient(
                    getConfig().getString("Mongo.Host"),
                    getConfig().getInt("Mongo.Port")
            );
        }

        mongoDatabase=mongoClient.getDatabase(getConfig().getString("Mongo.DbName"));
    }

    public PlayerDataManager getPlayerDataManager() {
        return this.playerDataManager;
    }

}
