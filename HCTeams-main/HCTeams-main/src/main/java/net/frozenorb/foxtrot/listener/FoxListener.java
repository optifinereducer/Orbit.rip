package net.frozenorb.foxtrot.listener;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.bounty.Bounty;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.events.Event;
import net.frozenorb.foxtrot.events.citadel.CitadelHandler;
import net.frozenorb.foxtrot.server.RegionData;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.commands.team.TeamStuckCommand;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.foxtrot.util.PotionUtil;
import net.frozenorb.qlib.economy.FrozenEconomyHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.bukkit.ChatColor.*;
import static org.bukkit.Material.*;

@SuppressWarnings("deprecation")
public class FoxListener implements Listener {

    private static final Map<BlockVector, UUID> pressurePlates = new ConcurrentHashMap<>();
    public static final ItemStack FIRST_SPAWN_BOOK = new ItemStack(WRITTEN_BOOK);
    public static final ItemStack FIRST_SPAWN_FISHING_ROD = new ItemStack(FISHING_ROD);
    public static final Set<PotionEffectType> DEBUFFS = ImmutableSet.of(PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.WEAKNESS, PotionEffectType.HARM, PotionEffectType.WITHER);
    public static final Set<Material> NO_INTERACT_WITH = ImmutableSet.of(LAVA_BUCKET, WATER_BUCKET, BUCKET);
    public static final Set<Material> ATTACK_DISABLING_BLOCKS = ImmutableSet.of(GLASS, WOOD_DOOR, IRON_DOOR, FENCE_GATE);
    public static final Set<Material> NO_INTERACT = ImmutableSet.of(FENCE_GATE, FURNACE, BURNING_FURNACE, BREWING_STAND, CHEST, HOPPER, DISPENSER, WOODEN_DOOR, STONE_BUTTON, WOOD_BUTTON, TRAPPED_CHEST, TRAP_DOOR, LEVER, DROPPER, ENCHANTMENT_TABLE, BED_BLOCK, ANVIL, BEACON);
    private static final List<UUID> processingTeleportPlayers = new CopyOnWriteArrayList<>();

    static {
        BookMeta bookMeta = (BookMeta) FIRST_SPAWN_BOOK.getItemMeta();

        String serverName = Foxtrot.getInstance().getServerHandler().getServerName();

        bookMeta.setTitle(GOLD + "Welcome to " + serverName);
        bookMeta.setPages(

                BLUE + "Welcome to " + serverName + "!"

        );
        bookMeta.setAuthor(Foxtrot.getInstance().getServerHandler().getServerName());

        FIRST_SPAWN_BOOK.setItemMeta(bookMeta);
        FIRST_SPAWN_FISHING_ROD.addEnchantment(Enchantment.LURE, 2);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        processTerritoryInfo(event); // this only works because I'm lucky and PlayerTeleportEvent extends PlayerMoveEvent :0
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        if (ServerHandler.getTasks().containsKey(event.getPlayer().getName())) {
            Foxtrot.getInstance().getServer().getScheduler().cancelTask(ServerHandler.getTasks().get(event.getPlayer().getName()).getTaskId());
            ServerHandler.getTasks().remove(event.getPlayer().getName());
            event.getPlayer().sendMessage(YELLOW.toString() + BOLD + "LOGOUT " + RED.toString() + BOLD + "CANCELLED!");
        }

        processTerritoryInfo(event);
    }

    /*@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerPressurePlate(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.STONE_PLATE) {
            BlockVector vector = event.getClickedBlock().getLocation().toVector().toBlockVector();
    
            if (!pressurePlates.containsKey(vector)) {
                pressurePlates.put(vector, event.getPlayer().getUniqueId()); // when this person steps off the plate, it will be depressed
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMoveOffPressurePlate(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
    
        if (event.getFrom().getBlock().getType() == Material.STONE_PLATE) {
            BlockVector vector = event.getFrom().toVector().toBlockVector();
    
            if (pressurePlates.containsKey(vector) && event.getPlayer().getUniqueId().equals(pressurePlates.get(vector))) {
                final Block block = event.getFrom().getBlock();
                pressurePlates.remove(vector);
    
                new BukkitRunnable() {
    
                    @Override
                    public void run() {
                        // pop pressure plate up
                        block.setType(Material.STONE_PLATE);
                        block.setData((byte) 0);
                        block.getState().update(true);
                    }
                }.runTaskLater(Foxtrot.getInstance(), 1L);
            }
        }
    }*/

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Foxtrot.getInstance().getPlaytimeMap().playerQuit(event.getPlayer().getUniqueId(), true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();

        Foxtrot.getInstance().getPlaytimeMap().playerJoined(player.getUniqueId());
        Foxtrot.getInstance().getLastJoinMap().setLastJoin(player.getUniqueId());

        if (!player.hasPlayedBefore()) {
            Foxtrot.getInstance().getFirstJoinMap().setFirstJoin(player.getUniqueId());
            FrozenEconomyHandler.setBalance(player.getUniqueId(), 250D);

            if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
                player.getInventory().addItem(FIRST_SPAWN_BOOK);
//                player.getInventory().addItem(FIRST_SPAWN_FISHING_ROD);
            }

            if (CustomTimerCreateCommand.getCustomTimers().get("&a&lSOTW") == null) {
                if (Foxtrot.getInstance().getServerHandler().isStartingTimerEnabled()) {
                    Foxtrot.getInstance().getPvPTimerMap().createStartingTimer(player.getUniqueId(), (int) TimeUnit.HOURS.toSeconds(1));
                } else {
                    Foxtrot.getInstance().getPvPTimerMap().createTimer(player.getUniqueId(), (int) TimeUnit.MINUTES.toSeconds(30));
                }
            }

            player.teleport(Foxtrot.getInstance().getServerHandler().getSpawnLocation());

            /* Populate these fields in mongo for Ariel, doesnt want them to be empty if player has no kills */
            if (Foxtrot.getInstance().getDeathsMap().getDeaths(player.getUniqueId()) == 0) {
                Foxtrot.getInstance().getDeathsMap().setDeaths(player.getUniqueId(), 0);
            }

            if (Foxtrot.getInstance().getKillsMap().getKills(player.getUniqueId()) == 0) {
                Foxtrot.getInstance().getKillsMap().setKills(player.getUniqueId(), 0);
            }
        }

//        Team team = LandBoard.getInstance().getTeam(player.getLocation());

//        if (team != null && team.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
//            player.setMetadata("join_invis", new FixedMetadataValue(Foxtrot.getInstance(), true));
//            for (Claim claim : team.getClaims()) {
//                for (Player target : claim.getPlayers()) {
//                    if (target == player) continue;
//                    if (target.hasMetadata("join_invis")) {
//                        target.hidePlayer(player);
//                    }
//                    player.hidePlayer(target);
//                }
//            }
//        }

    }

    @EventHandler
    public void onBookDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().equals(FIRST_SPAWN_BOOK)) {
            event.getItemDrop().remove(); // kill the book
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onStealthPickaxe(BlockBreakEvent event) {
        Block block = event.getBlock();
        ItemStack inHand = event.getPlayer().getItemInHand();
        if (inHand.getType() == GOLD_PICKAXE && inHand.hasItemMeta()) {
            if (inHand.getItemMeta().getDisplayName().startsWith(ChatColor.AQUA.toString())) {
                event.setCancelled(true);

                block.breakNaturally(inHand);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onStealthItemPickup(PlayerPickupItemEvent event) {
        ItemStack inHand = event.getPlayer().getItemInHand();
        if (inHand.getType() == GOLD_PICKAXE && inHand.hasItemMeta()) {
            if (inHand.getItemMeta().getDisplayName().startsWith(ChatColor.AQUA.toString())) {
                event.setCancelled(true);
                event.getPlayer().getInventory().addItem(event.getItem().getItemStack());
                event.getItem().remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (ServerHandler.getTasks().containsKey(player.getName())) {
                Foxtrot.getInstance().getServer().getScheduler().cancelTask(ServerHandler.getTasks().get(player.getName()).getTaskId());
                ServerHandler.getTasks().remove(player.getName());
                player.sendMessage(YELLOW.toString() + BOLD + "LOGOUT " + RED.toString() + BOLD + "CANCELLED!");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onProjectileInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem().getType() == POTION) {
                try { // Ensure that any errors with Potion.fromItemStack don't mess with the rest of the code.
                    ItemStack i = event.getItem();

                    // We can't run Potion.fromItemStack on a water bottle.
                    if (i.getDurability() != (short) 0) {
                        Potion pot = Potion.fromItemStack(i);

                        if (pot != null && pot.isSplash() && pot.getType() != null && DEBUFFS.contains(pot.getType().getEffectType())) {
                            if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                                player.sendMessage(RED + "You cannot do this while your PVP Timer is active!");
                                player.sendMessage(RED + "Type '" + GRAY + "/pvp enable" + RED + "' to remove your timer.");
                                event.setCancelled(true);
                                return;
                            }

                            if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                                event.setCancelled(true);
                                event.getPlayer().sendMessage(RED + "You cannot launch debuffs from inside spawn!");
                                event.getPlayer().updateInventory();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        if (event.getClickedBlock().getType() == ENCHANTMENT_TABLE && event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (event.getItem() != null) {
                if (event.getItem().getType() == ENCHANTED_BOOK) {
                    event.getItem().setType(BOOK);

                    event.getPlayer().sendMessage(GREEN + "You reverted this book to its original form!");
                    event.setCancelled(true);
                }
            }

            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getClickedBlock().getLocation()) || Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isEOTW()) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getClickedBlock().getLocation());

        if (team != null && !team.isMember(event.getPlayer().getUniqueId())) {
            if (NO_INTERACT.contains(event.getClickedBlock().getType()) || NO_INTERACT_WITH.contains(event.getMaterial())) {
                if (event.getClickedBlock().getType().name().contains("BUTTON") || event.getClickedBlock().getType().name().contains("CHEST") || event.getClickedBlock().getType().name().contains("DOOR")) {
                    CitadelHandler citadelHandler = Foxtrot.getInstance().getCitadelHandler();

                    if (DTRBitmask.CITADEL.appliesAt(event.getClickedBlock().getLocation()) && citadelHandler.canLootCitadel(event.getPlayer())) {
                        return;
                    }
                }

                if (event.getItem() != null && event.getItem().getType() == POTION) {
                    try {
                        Potion potion = Potion.fromItemStack(event.getItem());
                        if (potion.isSplash()) {
                            PotionUtil.splashPotion(player, event.getItem());
                            if (player.getItemInHand() != null && player.getItemInHand().isSimilar(event.getItem())) {
                                player.setItemInHand(null);
                                player.updateInventory();
                            } else {
                                InventoryUtils.removeAmountFromInventory(player.getInventory(), event.getItem(), 1);
                            }
                        }
                    } catch (Exception e) {
                        Bukkit.getLogger().warning("Swallowed exception " + e.getMessage());
                    }
                }

                if (!CustomTimerCreateCommand.isPurgeTimer()) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(YELLOW + "You cannot do this in " + team.getName(event.getPlayer()) + YELLOW + "'s territory.");
                }

                if (event.getMaterial() == TRAP_DOOR || event.getMaterial() == FENCE_GATE || event.getMaterial().name().contains("DOOR")) {
                    Foxtrot.getInstance().getServerHandler().disablePlayerAttacking(event.getPlayer(), 1);
                }

                return;
            }

            if (event.getAction() == Action.PHYSICAL) {
                event.setCancelled(true);
            }
        } else if (event.getMaterial() == LAVA_BUCKET) {
            if (team == null || !team.isMember(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(RED + "You can only do this in your own claims!");
            }
        } else {
            UUID uuid = player.getUniqueId();

            if (team != null && !team.isCaptain(uuid) && !team.isCoLeader(uuid) && !team.isOwner(uuid)) {
                Subclaim subclaim = team.getSubclaim(event.getClickedBlock().getLocation());

                if (subclaim != null && !subclaim.isMember(event.getPlayer().getUniqueId())) {
                    if (NO_INTERACT.contains(event.getClickedBlock().getType()) || NO_INTERACT_WITH.contains(event.getMaterial())) {
                        if (!CustomTimerCreateCommand.isPurgeTimer()) {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(YELLOW + "You do not have access to the subclaim " + GREEN + subclaim.getName() + YELLOW + "!");
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSignInteract(final PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getState() instanceof Sign) {
                Sign s = (Sign) event.getClickedBlock().getState();

                if (DTRBitmask.SAFE_ZONE.appliesAt(event.getClickedBlock().getLocation())) {
                    if (s.getLine(0).contains("Kit")) {
                        Foxtrot.getInstance().getServerHandler().handleKitSign(s, event.getPlayer());
                    } else if (s.getLine(0).contains("Buy") || s.getLine(0).contains("Sell")) {
                        Foxtrot.getInstance().getServerHandler().handleShopSign(s, event.getPlayer());
                    }

                    event.setCancelled(true);
                }
            }
        }

        if (event.getItem() != null && event.getMaterial() == SIGN) {
            if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().getLore() != null) {
                ArrayList<String> lore = (ArrayList<String>) event.getItem().getItemMeta().getLore();

                if (lore.size() > 1 && lore.get(1).contains("§e")) {
                    if (event.getClickedBlock() != null) {
                        event.getClickedBlock().getRelative(event.getBlockFace()).getState().setMetadata("noSignPacket", new FixedMetadataValue(Foxtrot.getInstance(), true));

                        new BukkitRunnable() {

                            public void run() {
                                event.getClickedBlock().getRelative(event.getBlockFace()).getState().removeMetadata("noSignPacket", Foxtrot.getInstance());
                            }

                        }.runTaskLater(Foxtrot.getInstance(), 20L);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignPlace(BlockPlaceEvent event) {
        ItemStack hand = event.getItemInHand();
        if (hand.getType() == SIGN) {
            if (hand.hasItemMeta() && hand.getItemMeta().getLore() != null) {
                ArrayList<String> lore = (ArrayList<String>) hand.getItemMeta().getLore();

                if (event.getBlock().getType() == WALL_SIGN || event.getBlock().getType() == SIGN_POST) {
                    Sign s = (Sign) event.getBlock().getState();

                    for (int i = 0; i < 4; i++) {
                        s.setLine(i, lore.get(i));
                    }

                    s.setMetadata("deathSign", new FixedMetadataValue(Foxtrot.getInstance(), true));
                    s.update();
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawnerPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        ItemStack hand = event.getItemInHand();
        if (hand.getType() == MOB_SPAWNER) {
            if (!(event.isCancelled())) {
                if (hand.hasItemMeta() && hand.getItemMeta().hasDisplayName()) {
                    String name = stripColor(hand.getItemMeta().getDisplayName());
                    String entName = name.replace(" Spawner", "");
                    EntityType type = EntityType.valueOf(entName.toUpperCase().replaceAll(" ", "_"));

                    CreatureSpawner spawner = (CreatureSpawner) block.getState();
                    spawner.setSpawnedType(type);
                    spawner.update();

                    event.getPlayer().sendMessage(AQUA + "You placed a " + entName + " spawner!");
                }
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        if (e.getBlock().getState().hasMetadata("deathSign") || ((Sign) e.getBlock().getState()).getLine(1).contains("§e")) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == WALL_SIGN || e.getBlock().getType() == SIGN_POST) {
            if (e.getBlock().getState().hasMetadata("deathSign") || ((e.getBlock().getState() instanceof Sign && ((Sign) e.getBlock().getState()).getLine(1).contains("§e")))) {
                e.setCancelled(true);

                Sign sign = (Sign) e.getBlock().getState();

                ItemStack deathsign = new ItemStack(SIGN);
                ItemMeta meta = deathsign.getItemMeta();

                if (sign.getLine(1).contains("Captured")) {
                    meta.setDisplayName("§dKOTH Capture Sign");
                } else {
                    meta.setDisplayName("§dDeath Sign");
                }

                meta.setLore(Arrays.asList(sign.getLines()));
                deathsign.setItemMeta(meta);
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), deathsign);

                e.getBlock().setType(AIR);
                e.getBlock().getState().removeMetadata("deathSign", Foxtrot.getInstance());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        SpawnTagHandler.removeTag(event.getEntity());
        Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity());
        Player killer = event.getEntity().getKiller();

        if (Foxtrot.getInstance().getInDuelPredicate().test(event.getEntity()) || Foxtrot.getInstance().getInEventPredicate().test(event.getEntity())) {
            return;
        }

        if (killer != null) {
            Team killerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(killer);
            Location deathLoc = event.getEntity().getLocation();
            int deathX = deathLoc.getBlockX();
            int deathY = deathLoc.getBlockY();
            int deathZ = deathLoc.getBlockZ();

            if (killerTeam != null) {
                TeamActionTracker.logActionAsync(killerTeam, TeamActionType.MEMBER_KILLED_ENEMY_IN_PVP, ImmutableMap.of("playerId", killer.getUniqueId(), "playerName", killer.getName(), "killedId", event.getEntity().getUniqueId(), "killedName", event.getEntity().getName(), "coordinates", deathX + ", " + deathY + ", " + deathZ));
            }

            if (playerTeam != null) {
                TeamActionTracker.logActionAsync(playerTeam, TeamActionType.MEMBER_KILLED_BY_ENEMY_IN_PVP, ImmutableMap.of("playerId", event.getEntity().getUniqueId(), "playerName", event.getEntity().getName(), "killerId", killer.getUniqueId(), "killerName", killer.getName(), "coordinates", deathX + ", " + deathY + ", " + deathZ));
            }

            // Add kills to sword lore if the victim does not equal the killer
            if (!event.getEntity().equals(killer)) {
                ItemStack hand = killer.getItemInHand();

                if (hand != null && (hand.getType().name().contains("SWORD") || hand.getType() == BOW)) {
                    InventoryUtils.addKill(hand, killer.getDisplayName() + YELLOW + " " + (hand.getType() == BOW ? "shot" : "killed") + " " + event.getEntity().getDisplayName());
                }
            }

            if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
                Bounty bounty = Foxtrot.getInstance().getMapHandler().getBountyManager().removeBounty(event.getEntity());

                if (bounty != null) {
                    Foxtrot.getInstance().getGemMap().addGems(killer.getUniqueId(), bounty.getGems(), true);
                    Bukkit.broadcastMessage(CC.GRAY + "[" + CC.GOLD + "Bounty" + CC.GRAY + "] " + killer.getDisplayName() + CC.YELLOW + " killed "
                            + event.getEntity().getDisplayName() + CC.YELLOW + " and received the bounty of "
                            + CC.GREEN + bounty.getGems() + " gems" + CC.YELLOW + "!");
                }
            }
        }

        if (playerTeam != null) {
            playerTeam.playerDeath(event.getEntity().getName(), Foxtrot.getInstance().getServerHandler().getDTRLoss(event.getEntity()), killer);
        }

        if (killer == null || (!event.getEntity().equals(killer))) {
            // Add deaths to armor
            String deathMsg = YELLOW + event.getEntity().getName() + RESET + " " + (event.getEntity().getKiller() != null ? "killed by " + YELLOW + event.getEntity().getKiller().getName() : "died") + " " + GOLD +
                    InventoryUtils.DEATH_TIME_FORMAT.format(new Date());

            for (ItemStack armor : event.getEntity().getInventory().getArmorContents()) {
                if (armor != null && armor.getType() != AIR) {
                    InventoryUtils.addDeath(armor, deathMsg);
                }
            }
        }

        event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());

        // Transfer money
        double bal = FrozenEconomyHandler.getBalance(event.getEntity().getUniqueId());
        FrozenEconomyHandler.withdraw(event.getEntity().getUniqueId(), bal);

        // Only tell player they earned money if they actually earned something
        if ((killer = event.getEntity().getKiller()) != null && !Double.isNaN(bal) && bal > 0) {
            FrozenEconomyHandler.deposit(killer.getUniqueId(), bal);
            killer.sendMessage(GOLD + "You earned " + BOLD + "$" + bal + GOLD + " for killing " + event.getEntity().getDisplayName() + GOLD + "!");
        }
    }

    private void processTerritoryInfo(PlayerMoveEvent event) {
        Team ownerTo = LandBoard.getInstance().getTeam(event.getTo());

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {

            /*
            //prevent stack overflow
            if (ownerTo != null && ownerTo.getKitName().equalsIgnoreCase("spawn")) {
                return;
            }
            
            //prevent staff from being teleported during the claiming process
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                return;
            }
            */

            if (!DTRBitmask.SAFE_ZONE.appliesAt(event.getTo())) {

                if (DTRBitmask.KOTH.appliesAt(event.getTo()) || DTRBitmask.CITADEL.appliesAt(event.getTo())) {
                    Foxtrot.getInstance().getPvPTimerMap().removeTimer(event.getPlayer().getUniqueId());

                    event.getPlayer().sendMessage(ChatColor.RED + "Your PvP Protection has been removed for entering claimed land.");
                } else if (ownerTo != null && ownerTo.getOwner() != null) {
                    if (!ownerTo.getMembers().contains(event.getPlayer().getUniqueId())) {
                        event.setCancelled(true);

                        for (Claim claim : ownerTo.getClaims()) {
                            if (claim.contains(event.getFrom()) && !ownerTo.isMember(event.getPlayer().getUniqueId())) {
                                Location nearest = TeamStuckCommand.nearestSafeLocation(event.getPlayer().getLocation());
                                boolean spawn = false;

                                if (nearest == null) {
                                    nearest = Foxtrot.getInstance().getServerHandler().getSpawnLocation();
                                    spawn = true;
                                }

                                event.getPlayer().teleport(nearest);
                                event.getPlayer().sendMessage(ChatColor.RED + "Moved you to " + (spawn ? "spawn" : "nearest unclaimed territory") + " because you were in land that was claimed.");
                                return;
                            }
                        }

                        event.getPlayer().sendMessage(ChatColor.RED + "You cannot enter another team's territory with PvP Protection.");
                        event.getPlayer().sendMessage(ChatColor.RED + "Use " + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + " to remove your protection.");
                        return;
                    }
                }
            }
        }

        Team ownerFrom = LandBoard.getInstance().getTeam(event.getFrom());

        if (ownerFrom != ownerTo) {
            ServerHandler sm = Foxtrot.getInstance().getServerHandler();
            RegionData from = sm.getRegion(ownerFrom, event.getFrom());
            RegionData to = sm.getRegion(ownerTo, event.getTo());

            if (from.equals(to)) return;

            if (!to.getRegionType().getMoveHandler().handleMove(event)) {
                return;
            }

            boolean fromReduceDeathban = from.getData() != null && (from.getData().hasDTRBitmask(DTRBitmask.FIVE_MINUTE_DEATHBAN) || from.getData().hasDTRBitmask(DTRBitmask.FIFTEEN_MINUTE_DEATHBAN) || from.getData().hasDTRBitmask(DTRBitmask.SAFE_ZONE));
            boolean toReduceDeathban = to.getData() != null && (to.getData().hasDTRBitmask(DTRBitmask.FIVE_MINUTE_DEATHBAN) || to.getData().hasDTRBitmask(DTRBitmask.FIFTEEN_MINUTE_DEATHBAN) || to.getData().hasDTRBitmask(DTRBitmask.SAFE_ZONE));

            if (fromReduceDeathban && from.getData() != null) {
                Event fromLinkedKOTH = Foxtrot.getInstance().getEventHandler().getEvent(from.getData().getName());

                if (fromLinkedKOTH != null && !fromLinkedKOTH.isActive()) {
                    fromReduceDeathban = false;
                }
            }

            if (toReduceDeathban && to.getData() != null) {
                Event toLinkedKOTH = Foxtrot.getInstance().getEventHandler().getEvent(to.getData().getName());

                if (toLinkedKOTH != null && !toLinkedKOTH.isActive()) {
                    toReduceDeathban = false;
                }
            }

            if (Foxtrot.getInstance().getToggleClaimMessageMap().areClaimMessagesEnabled(event.getPlayer().getUniqueId())) {
                // create leaving message
                FancyMessage nowLeaving = new FancyMessage("Now leaving: ").color(YELLOW).then(from.getName(event.getPlayer())).color(YELLOW);

                if (ownerFrom != null) {
                    nowLeaving.command("/t i " + ownerFrom.getName()).tooltip(GREEN + "View team info");
                }

                nowLeaving.then(" (").color(YELLOW).then(fromReduceDeathban ? "Non-Deathban" : "Deathban").color(fromReduceDeathban ? GREEN : RED).then(")").color(YELLOW);

                // create entering message
                FancyMessage nowEntering = new FancyMessage("Now entering: ").color(YELLOW).then(to.getName(event.getPlayer())).color(WHITE);

                if (ownerTo != null) {
                    nowEntering.command("/t i " + ownerTo.getName()).tooltip(GREEN + "View team info");
                }

                nowEntering.then(" (").color(YELLOW).then(toReduceDeathban ? "Non-Deathban" : "Deathban").color(toReduceDeathban ? GREEN : RED).then(")").color(YELLOW);

                // send both
                nowLeaving.send(event.getPlayer());
                nowEntering.send(event.getPlayer());
            }

//            if (event.getPlayer().hasMetadata("join_invis")) {
//                event.getPlayer().removeMetadata("join_invis", Foxtrot.getInstance());
//                for (Claim claim : from.getData().getClaims()) {
//                    for (Player player : claim.getPlayers()) {
//                        if (player == event.getPlayer()) continue;
//                        if (!player.canSee(event.getPlayer()) && !player.hasMetadata("invisible")) {
//                            player.showPlayer(event.getPlayer());
//                            event.getPlayer().showPlayer(player);
//                        }
//
//                        if (!event.getPlayer().canSee(player) && !player.hasMetadata("invisible")) {
//                            event.getPlayer().showPlayer(player);
//                        }
//                    }
//                }
//            }
        }
    }

}
