package rip.warzone.hub.listener;

import lombok.SneakyThrows;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.spigotmc.SpigotConfig;
import rip.warzone.hub.Hub;
import rip.warzone.hub.HubConstants;
import rip.warzone.hub.armor.Armor;
import rip.warzone.hub.utils.menu.armor.ArmorSelectionMenu;
import rip.warzone.hub.utils.menu.server.ServerSelectorMenu;

import java.io.File;

public class GeneralListener implements Listener {

    @EventHandler
    @SneakyThrows
    public void join(PlayerJoinEvent event){

        File file = new File(Hub.getInstance().getDataFolder(), "/data/" + event.getPlayer().getUniqueId().toString() + ".yml");
        System.out.println(file.getAbsolutePath());
        System.out.println(file.getAbsolutePath());
        System.out.println(file.getAbsolutePath());
        System.out.println(file.getAbsolutePath());
        if(!file.exists()) file.createNewFile();

        YamlConfiguration config = new YamlConfiguration();
        config.load(file);
        config.save(file);

        event.getPlayer().getInventory().clear();
        event.getPlayer().getInventory().setHelmet(null);
        event.getPlayer().getInventory().setChestplate(null);
        event.getPlayer().getInventory().setLeggings(null);
        event.getPlayer().getInventory().setBoots(null);

        event.getPlayer().getInventory().setItem(0, HubConstants.ENDERPEARL);
        event.getPlayer().getInventory().setItem(4, HubConstants.COMPASS_ITEM);
        event.getPlayer().getInventory().setItem(8, HubConstants.ARMOR_SELECTOR);

        Armor armor = Hub.getInstance().getArmorManager().getActiveArmor(event.getPlayer());
        if(armor != null) armor.apply(event.getPlayer(), Hub.getInstance().getArmorManager().isGlint(event.getPlayer()));

        World world = Bukkit.getServer().getWorlds().get(0);
        Location location = new Location(world, Math.floor(world.getSpawnLocation().getX()) + 0.5, world.getSpawnLocation().getY(), Math.floor(world.getSpawnLocation().getZ()) + 0.5, world.getSpawnLocation().getYaw(), world.getSpawnLocation().getPitch());
        event.getPlayer().teleport(location);
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event){
        if(event.getInventory().getHolder() == event.getWhoClicked()) event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        if(event.getPlayer().getGameMode() != GameMode.CREATIVE) event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerPickupItemEvent event){
        if(event.getPlayer().getGameMode() != GameMode.CREATIVE) event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        if(event.getPlayer().getGameMode() != GameMode.CREATIVE) event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(BlockPlaceEvent event){
        if(event.getPlayer().getGameMode() != GameMode.CREATIVE) event.setCancelled(true);
    }

    @EventHandler
    public void move(PlayerMoveEvent event){
        if(event.getPlayer().isFlying()){
            event.getPlayer().setFlying(false);
            event.getPlayer().setAllowFlight(false);
            event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().multiply(0.98).setY(1.21));
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.FIREWORK_BLAST, 20.0F, 0.0952381F);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.EXPLODE, 1.0F, 2.0F);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.WITHER_SHOOT, 1.0F, 2.0F);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);
        }
        if(event.getTo().subtract(0, 1, 0).getBlock().getType().isSolid()) event.getPlayer().setAllowFlight(true);
    }

    @EventHandler
    public void damage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player) event.setCancelled(true);
    }

    @EventHandler
    public void invMove(InventoryClickEvent event){
        if(event.getWhoClicked().getGameMode() != GameMode.CREATIVE && event.getInventory().getHolder() != event.getWhoClicked()) event.setCancelled(true);
    }

    @EventHandler
    public void hunger(FoodLevelChangeEvent event){
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onDrop(PlayerInteractEvent event){
        if(event.getAction() == Action.PHYSICAL && event.getPlayer().getGameMode() != GameMode.CREATIVE) event.setCancelled(true);
        if(event.getAction().name().contains("RIGHT")) {
            ItemStack item = event.getPlayer().getItemInHand();
            if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
            if (item.isSimilar(HubConstants.COMPASS_ITEM)) {
                Bukkit.getServer().getScheduler().runTask(Hub.getInstance(), () -> new ServerSelectorMenu().openMenu(event.getPlayer()));
            } else if (item.isSimilar(HubConstants.ARMOR_SELECTOR)) {
                Bukkit.getServer().getScheduler().runTask(Hub.getInstance(), () -> new ArmorSelectionMenu().openMenu(event.getPlayer()));
            }else if(item.isSimilar(HubConstants.ENDERPEARL)){
                event.setCancelled(true);
                event.getPlayer().updateInventory();
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().multiply(1.2).add(new Vector(0, 0.83, 0)));
            }
        }
    }

    @EventHandler
    public void command(PlayerCommandPreprocessEvent event){
        if(event.getMessage().startsWith("/joinqueue") || event.getMessage().startsWith("/jq")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(SpigotConfig.unknownCommandMessage);
        }
    }

}
