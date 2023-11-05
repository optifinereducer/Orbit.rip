package rip.warzone.hub.armor;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import rip.warzone.hub.Hub;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ArmorManager {

    @Getter private List<Armor> armors = new ArrayList<>();

    public ArmorManager(){
        File dir = new File(Hub.getInstance().getDataFolder(), "/data/");
        if(!dir.exists()) dir.mkdir();
        ConfigurationSection armorSection = Hub.getInstance().getConfig().getConfigurationSection("cosmetics.armor");
        armorSection.getKeys(false).forEach(name -> {
            armors.add(
                    new Armor(
                            name,
                            armorSection.getString(name + ".display-name"),
                            armorSection.getString(name + ".text-color"),
                            DyeColor.valueOf(armorSection.getString(name + ".gui-color")),
                            Color.fromRGB(armorSection.getInt(name + ".armor-color.red"), armorSection.getInt(name + ".armor-color.blue"), armorSection.getInt(name + ".armor-color.green")),
                            armorSection.getString(name + ".permission")));
            Hub.getInstance().getLogger().info("Loaded the \"" + name + "\" armor cosmetic!");
        });
    }

    public void apply(Player player, Armor armor, boolean glint){
        if(armor == null){
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);
            player.updateInventory();
            return;
        }
        armor.apply(player, glint);
    }

    @SneakyThrows
    public void setActiveArmor(Player player, Armor armor, boolean glint){

        File file = new File(Hub.getInstance().getDataFolder(), "/data/" + player.getUniqueId().toString() + ".yml");
        if (!file.exists()) file.createNewFile();
        YamlConfiguration config = new YamlConfiguration();
        config.load(file);
        if(armor == null) {
            config.set("activeArmor", null);
        }else{
            config.set("activeArmor", armor.getName());
        }
        config.set("glint", glint);
        config.save(file);
        apply(player, armor, isGlint(player));

    }

    @SneakyThrows
    public void setGlint(Player player, boolean glint){
        File file = new File(Hub.getInstance().getDataFolder(), "/data/" + player.getUniqueId().toString() + ".yml");
        if (!file.exists()) file.createNewFile();
        YamlConfiguration config = new YamlConfiguration();
        config.load(file);
        config.set("glint", glint);
        config.save(file);
        apply(player, getActiveArmor(player), glint);
    }

    @SneakyThrows
    public boolean isGlint(Player player){
        File file = new File(Hub.getInstance().getDataFolder(), "/data/" + player.getUniqueId().toString() + ".yml");
        if (!file.exists()) file.createNewFile();
        YamlConfiguration config = new YamlConfiguration();
        config.load(file);
        return config.getBoolean("glint");
    }

    @SneakyThrows
    public Armor getActiveArmor(Player player){

        File file = new File(Hub.getInstance().getDataFolder(), "/data/" + player.getUniqueId().toString() + ".yml");
        if(!file.exists()) file.createNewFile();
        YamlConfiguration config = new YamlConfiguration();
        config.load(file);

        if(config.get("activeArmor") == null) return null;

        return armors.stream().filter(armor -> armor.getName().equalsIgnoreCase(config.getString("activeArmor"))).findFirst().orElse(null);
    }

}
