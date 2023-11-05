package rip.warzone.hub.server;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import rip.warzone.hub.Hub;

import java.util.ArrayList;
import java.util.List;

public class ServerManager {

    @Getter private final List<Server> servers = new ArrayList<>();

    public ServerManager(){
        ConfigurationSection section = Hub.getInstance().getConfig().getConfigurationSection("servers");
        section.getKeys(false).forEach(name -> {
            servers.add(new Server(name, section.getInt(name + ".guiSlot"), Material.valueOf(section.getString(name + ".icon")), section.getString(name + ".display-name"), section.getStringList(name + ".description")));
            Hub.getInstance().getLogger().info("Loaded the \"" + name + "\" server!");
        });
    }

}
