package rip.warzone.hub;

import lombok.Getter;
import net.frozenorb.qlib.scoreboard.FrozenScoreboardHandler;
import org.bukkit.plugin.java.JavaPlugin;
import rip.warzone.hub.armor.ArmorManager;
import rip.warzone.hub.listener.GeneralListener;
import rip.warzone.hub.scoreboard.HubScoreConfiguration;
import rip.warzone.hub.server.ServerManager;

public class Hub extends JavaPlugin {

    @Getter private static Hub instance;

    @Getter private ServerManager serverManager;
    @Getter private ArmorManager armorManager;

    @Override
    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        this.serverManager = new ServerManager();
        this.armorManager = new ArmorManager();

        FrozenScoreboardHandler.setConfiguration(HubScoreConfiguration.create());

        getServer().getPluginManager().registerEvents(new GeneralListener(), this);

    }
}
