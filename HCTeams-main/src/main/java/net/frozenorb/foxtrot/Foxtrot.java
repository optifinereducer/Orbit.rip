package net.frozenorb.foxtrot;

import com.comphenix.protocol.ProtocolLibrary;
import com.mongodb.MongoClient;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.battlepass.BattlePassHandler;
import net.frozenorb.foxtrot.chat.ChatHandler;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.crates.CrateHandler;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.events.EventHandler;
import net.frozenorb.foxtrot.events.citadel.CitadelHandler;
import net.frozenorb.foxtrot.events.conquest.ConquestHandler;
import net.frozenorb.foxtrot.events.region.cavern.CavernHandler;
import net.frozenorb.foxtrot.events.region.glowmtn.GlowHandler;
import net.frozenorb.foxtrot.fixes.FixListener;
import net.frozenorb.foxtrot.ftop.FTopHandler;
import net.frozenorb.foxtrot.gem.GemHandler;
import net.frozenorb.foxtrot.listener.*;
import net.frozenorb.foxtrot.lunar.LunarHandler;
import net.frozenorb.foxtrot.map.MapHandler;
import net.frozenorb.foxtrot.map.game.arena.select.SelectionListeners;
import net.frozenorb.foxtrot.map.game.impl.ffa.FFAListeners;
import net.frozenorb.foxtrot.map.game.impl.shuffle.ShuffleListeners;
import net.frozenorb.foxtrot.map.game.impl.spleef.SpleefListeners;
import net.frozenorb.foxtrot.map.game.impl.sumo.SumoListeners;
import net.frozenorb.foxtrot.map.game.listener.GameListeners;
import net.frozenorb.foxtrot.map.kits.listener.KitEditorListener;
import net.frozenorb.foxtrot.map.listener.BlockDecayListeners;
import net.frozenorb.foxtrot.packetborder.PacketBorderThread;
import net.frozenorb.foxtrot.partner.PartnerCrateHandler;
import net.frozenorb.foxtrot.partner.PartnerPackageHandler;
import net.frozenorb.foxtrot.persist.RedisSaveTask;
import net.frozenorb.foxtrot.persist.maps.*;
import net.frozenorb.foxtrot.persist.maps.statistics.*;
import net.frozenorb.foxtrot.persist.maps.toggle.CobblePickupMap;
import net.frozenorb.foxtrot.persist.maps.toggle.MobDropsPickupMap;
import net.frozenorb.foxtrot.protocol.ClientCommandPacketAdaper;
import net.frozenorb.foxtrot.protocol.SignGUIPacketAdaper;
import net.frozenorb.foxtrot.purge.PurgeHandler;
import net.frozenorb.foxtrot.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.reclaim.ReclaimHandler;
import net.frozenorb.foxtrot.redeem.RedeemCreatorHandler;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.deathban.DeathbanListener;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.skinfix.SkinFix;
import net.frozenorb.foxtrot.tab.FoxtrotTabLayoutProvider;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamHandler;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.commands.team.TeamClaimCommand;
import net.frozenorb.foxtrot.team.commands.team.subclaim.TeamSubclaimCommand;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.GlowUtil;
import net.frozenorb.foxtrot.util.RegenUtils;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.tab.FrozenTabHandler;
import net.frozenorb.qlib.visibility.FrozenVisibilityHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.SpigotConfig;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class Foxtrot extends JavaPlugin {

    public static String MONGO_DB_NAME = "HCTeams";

    @Getter
    private static Foxtrot instance;

    @Getter
    private MongoClient mongoPool;

    @Getter
    private ChatHandler chatHandler;
    @Getter
    private PvPClassHandler pvpClassHandler;
    @Getter
    private TeamHandler teamHandler;
    @Getter
    private ServerHandler serverHandler;
    @Getter
    private MapHandler mapHandler;
    @Getter
    private CitadelHandler citadelHandler;
    @Getter
    private EventHandler eventHandler;
    @Getter
    private ConquestHandler conquestHandler;
    @Getter
    private CavernHandler cavernHandler;
    @Getter
    private GlowHandler glowHandler;
    @Getter
    private CrateHandler crateHandler;
    @Getter
    private ReclaimHandler reclaimHandler;
    @Getter
    private GemHandler gemHandler;
    @Getter
    private BattlePassHandler battlePassHandler;
    @Getter
    private RedeemCreatorHandler redeemCreatorHandler;

    @Getter
    private PartnerPackageHandler partnerPackageHandler;
    @Getter
    private PartnerCrateHandler partnerCrateHandler;
    @Getter
    private PurgeHandler purgeHandler;
    @Getter
    private FTopHandler topHandler;
    @Getter
    private LunarHandler lunarHandler;

    @Getter
    private PlaytimeMap playtimeMap;
    @Getter
    private OppleMap oppleMap;
    @Getter
    private DeathbanMap deathbanMap;
    @Getter
    private PvPTimerMap PvPTimerMap;
    @Getter
    private StartingPvPTimerMap startingPvPTimerMap;
    @Getter
    private DeathsMap deathsMap;
    @Getter
    private KillsMap killsMap;
    @Getter
    private KillstreakMap killstreakMap;
    @Getter
    private ChatModeMap chatModeMap;
    @Getter
    private FishingKitMap fishingKitMap;
    @Getter
    private ToggleGlobalChatMap toggleGlobalChatMap;
    @Getter
    private ChatSpyMap chatSpyMap;
    @Getter
    private DiamondMinedMap diamondMinedMap;
    @Getter
    private GoldMinedMap goldMinedMap;
    @Getter
    private IronMinedMap ironMinedMap;
    @Getter
    private CoalMinedMap coalMinedMap;
    @Getter
    private RedstoneMinedMap redstoneMinedMap;
    @Getter
    private LapisMinedMap lapisMinedMap;
    @Getter
    private EmeraldMinedMap emeraldMinedMap;
    @Getter
    private FirstJoinMap firstJoinMap;
    @Getter
    private LastJoinMap lastJoinMap;
    @Getter
    private FriendLivesMap friendLivesMap;
    @Getter
    private BaseStatisticMap enderPearlsUsedMap;
    @Getter
    private BaseStatisticMap expCollectedMap;
    @Getter
    private BaseStatisticMap itemsRepairedMap;
    @Getter
    private BaseStatisticMap splashPotionsBrewedMap;
    @Getter
    private BaseStatisticMap splashPotionsUsedMap;
    @Getter
    private WrappedBalanceMap wrappedBalanceMap;
    @Getter
    private ToggleFoundDiamondsMap toggleFoundDiamondsMap;
    @Getter
    private ToggleDeathMessageMap toggleDeathMessageMap;
    @Getter
    private ToggleClaimDisplayMap toggleClaimDisplayMap;
    @Getter
    private ToggleClaimMessageMap toggleClaimMessageMap;
    @Getter
    private ToggleFocusDisplayMap toggleFocusDisplayMap;
    @Getter
    private TabListModeMap tabListModeMap;
    @Getter
    private IPMap ipMap;
    @Getter
    private WhitelistedIPMap whitelistedIPMap;
    @Getter
    private CobblePickupMap cobblePickupMap;
    @Getter
    private MobDropsPickupMap mobDropsPickupMap;
    @Getter
    private KDRMap kdrMap;
    @Getter
    private GemMap gemMap;
    @Getter
    private BountyCooldownMap bountyCooldownMap;

    @Getter
    private CombatLoggerListener combatLoggerListener;
    @Getter
    @Setter
    // for the case of some commands in the plugin,
    // a player shouldn't be able to do them in a duel
    // thus this predicate exists to test that to avoid dep. issues
    private Predicate<Player> inDuelPredicate = (player) -> mapHandler.isKitMap() && mapHandler.getDuelHandler().isInDuel(player);

    @Getter
    @Setter
    private Predicate<Player> inEventPredicate = (player) ->
            mapHandler.isKitMap() &&
                    mapHandler.getGameHandler().isOngoingGame() && mapHandler.getGameHandler().getOngoingGame().isPlaying(player.getUniqueId());

    @Getter
    private boolean isCrazyEnchants = false;

    @Override
    public void onEnable() {
        if (Bukkit.getServerName().contains(" ")) {
            System.out.println("*********************************************");
            System.out.println("               ATTENTION");
            System.out.println("SET server-name VALUE IN server.properties TO");
            System.out.println("A PROPER SERVER NAME. THIS WILL BE USED AS THE");
            System.out.println("MONGO DATABASE NAME.");
            System.out.println("*********************************************");
            this.getServer().shutdown();
            return;
        }

        SpigotConfig.onlyCustomTab = true; // because I know we'll forget
        instance = this;
        saveDefaultConfig();

        try {
            mongoPool = new MongoClient(getConfig().getString("Mongo.Host", "127.0.0.1"));
            MONGO_DB_NAME = Bukkit.getServerName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        (new DTRHandler()).runTaskTimer(this, 20L, 450L);
        (new RedisSaveTask()).runTaskTimerAsynchronously(this, 1200L, 1200L);
        (new PacketBorderThread()).start();

        setupHandlers();
        setupPersistence();
        setupListeners();
        setupTasks();

        FrozenVisibilityHandler.registerHandler("foxtrot", new FoxtrotVisibilityHandler());
        FrozenTabHandler.setLayoutProvider(new FoxtrotTabLayoutProvider());

        ProtocolLibrary.getProtocolManager().addPacketListener(new SignGUIPacketAdaper());
        ProtocolLibrary.getProtocolManager().addPacketListener(new ClientCommandPacketAdaper());

        for (World world : Bukkit.getWorlds()) {
            world.setThundering(false);
            world.setStorm(false);
            world.setWeatherDuration(Integer.MAX_VALUE);
            world.setGameRuleValue("doFireTick", "false");
            world.setGameRuleValue("mobGriefing", "false");
        }

        if (!Bukkit.getOnlineMode()) {
            SkinFix skinFix = new SkinFix();
            getServer().getPluginManager().registerEvents(skinFix, this);
            ProtocolLibrary.getProtocolManager().addPacketListener(skinFix);
        }

        // we just define this here while we're testing, if we actually
        // accept this feature it'll be moved to somewhere better
//		new ServerFakeFreezeTask().runTaskTimerAsynchronously(this, 20L, 20L);

        isCrazyEnchants = Bukkit.getPluginManager().getPlugin("CrazyEnchantments") != null;
        GlowUtil.init();
    }

    @Override
    public void onDisable() {
        getEventHandler().saveEvents();

        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            getPlaytimeMap().playerQuit(player.getUniqueId(), false);
            player.setMetadata("loggedout", new FixedMetadataValue(this, true));
        }

        for (String playerName : PvPClassHandler.getEquippedKits().keySet()) {
            PvPClassHandler.getEquippedKits().get(playerName).remove(getServer().getPlayerExact(playerName));
        }

        for (Entity e : this.combatLoggerListener.getCombatLoggers()) {
            if (e != null) {
                e.remove();
            }
        }

        RedisSaveTask.save(null, false);
        Foxtrot.getInstance().getServerHandler().save();

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            Foxtrot.getInstance().getMapHandler().getStatsHandler().save();
            Foxtrot.getInstance().getMapHandler().getBountyManager().save();
        }

        RegenUtils.resetAll();

        qLib.getInstance().runRedisCommand((jedis) -> {
            jedis.save();
            return null;
        });

        if (Foxtrot.getInstance().getMapHandler().getGameHandler() != null) {
            if (Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
                Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame().endGame();
            }
        }
    }

    private void setupHandlers() {
        serverHandler = new ServerHandler();
        mapHandler = new MapHandler();
        mapHandler.load();

        teamHandler = new TeamHandler();
        LandBoard.getInstance().loadFromTeams();

        chatHandler = new ChatHandler();
        citadelHandler = new CitadelHandler();
        pvpClassHandler = new PvPClassHandler();
        eventHandler = new EventHandler();
        conquestHandler = new ConquestHandler();

        partnerPackageHandler = new PartnerPackageHandler();
        partnerCrateHandler = new PartnerCrateHandler();
        purgeHandler = new PurgeHandler();
        topHandler = new FTopHandler();
        lunarHandler = new LunarHandler(this);

        if (getConfig().getBoolean("glowstoneMountain", false)) {
            glowHandler = new GlowHandler();
        }

        if (getConfig().getBoolean("cavern", false)) {
            cavernHandler = new CavernHandler();
        }

        crateHandler = new CrateHandler();
        reclaimHandler = new ReclaimHandler();

        gemHandler = new GemHandler();
        gemHandler.loadChances();

        if (!mapHandler.isKitMap()) {
            battlePassHandler = new BattlePassHandler();
        }

        redeemCreatorHandler = new RedeemCreatorHandler();

        FrozenCommandHandler.registerAll(this);

        DeathMessageHandler.init();
        DTRHandler.loadDTR();
    }

    private void setupListeners() {
        getServer().getPluginManager().registerEvents(new MapListener(), this);
        getServer().getPluginManager().registerEvents(new DimensionListener(), this);
        getServer().getPluginManager().registerEvents(new AntiGlitchListener(), this);
        getServer().getPluginManager().registerEvents(new BasicPreventionListener(), this);
        getServer().getPluginManager().registerEvents(new BorderListener(), this);
        getServer().getPluginManager().registerEvents((combatLoggerListener = new CombatLoggerListener()), this);
        getServer().getPluginManager().registerEvents(new CrowbarListener(), this);
        getServer().getPluginManager().registerEvents(new DeathbanListener(), this);
        getServer().getPluginManager().registerEvents(new ElevatorsListeners(), this);
        getServer().getPluginManager().registerEvents(new EnchantmentLimiterListener(), this);
        getServer().getPluginManager().registerEvents(new EnderpearlCooldownHandler(), this);
        getServer().getPluginManager().registerEvents(new EndListener(), this);
        getServer().getPluginManager().registerEvents(new FastBowListener(), this);
        getServer().getPluginManager().registerEvents(new FoundDiamondsListener(), this);
        getServer().getPluginManager().registerEvents(new FoxListener(), this);
        getServer().getPluginManager().registerEvents(new GoldenAppleListener(), this);
        getServer().getPluginManager().registerEvents(new KOTHRewardKeyListener(), this);
        getServer().getPluginManager().registerEvents(new PvPTimerListener(), this);
        getServer().getPluginManager().registerEvents(new PotionLimiterListener(), this);
        getServer().getPluginManager().registerEvents(new NetherPortalListener(), this);
        getServer().getPluginManager().registerEvents(new PortalTrapListener(), this);
        getServer().getPluginManager().registerEvents(new SignSubclaimListener(), this);
        getServer().getPluginManager().registerEvents(new SpawnListener(), this);
        getServer().getPluginManager().registerEvents(new SpawnTagListener(), this);
        getServer().getPluginManager().registerEvents(new StaffUtilsListener(), this);
        getServer().getPluginManager().registerEvents(new TeamListener(), this);
        getServer().getPluginManager().registerEvents(new WebsiteListener(), this);
        getServer().getPluginManager().registerEvents(new TeamSubclaimCommand(), this);
        getServer().getPluginManager().registerEvents(new TeamClaimCommand(), this);
        getServer().getPluginManager().registerEvents(new StatTrakListener(), this);
        getServer().getPluginManager().registerEvents(new FixListener(), this);

        if (getServerHandler().isReduceArmorDamage()) {
            getServer().getPluginManager().registerEvents(new ArmorDamageListener(), this);
        }

        if (getServerHandler().isBlockEntitiesThroughPortals()) {
            getServer().getPluginManager().registerEvents(new EntityPortalListener(), this);
        }

        if (getServerHandler().isBlockRemovalEnabled()) {
            getServer().getPluginManager().registerEvents(new BlockRegenListener(), this);
        }

        // Register KitMap specific listeners
        if (getMapHandler().isKitMap()) {
            getServer().getPluginManager().registerEvents(new KitMapListener(), this);
            getServer().getPluginManager().registerEvents(new KitEditorListener(), this);
            getServer().getPluginManager().registerEvents(new BlockDecayListeners(), this);
            getServer().getPluginManager().registerEvents(new GameListeners(), this);
            getServer().getPluginManager().registerEvents(new SumoListeners(), this);
            getServer().getPluginManager().registerEvents(new FFAListeners(), this);
            getServer().getPluginManager().registerEvents(new SpleefListeners(), this);
            getServer().getPluginManager().registerEvents(new ShuffleListeners(), this);
            getServer().getPluginManager().registerEvents(new SelectionListeners(), this);
        }

        getServer().getPluginManager().registerEvents(new BlockConvenienceListener(), this);

        //getServer().getPluginManager().registerEvents(new IPListener(), this );
        //getServer().getPluginManager().registerEvents(new Prot3Sharp3Listener(), this);
    }

    private void setupPersistence() {
        (playtimeMap = new PlaytimeMap()).loadFromRedis();
        (oppleMap = new OppleMap()).loadFromRedis();
        (deathbanMap = new DeathbanMap()).loadFromRedis();
        (PvPTimerMap = new PvPTimerMap()).loadFromRedis();
        (startingPvPTimerMap = new StartingPvPTimerMap()).loadFromRedis();
        (deathsMap = new DeathsMap()).loadFromRedis();
        (killsMap = new KillsMap()).loadFromRedis();
        (killstreakMap = new KillstreakMap()).loadFromRedis();
        (chatModeMap = new ChatModeMap()).loadFromRedis();
        (toggleGlobalChatMap = new ToggleGlobalChatMap()).loadFromRedis();
        (fishingKitMap = new FishingKitMap()).loadFromRedis();
        (friendLivesMap = new FriendLivesMap()).loadFromRedis();
        (chatSpyMap = new ChatSpyMap()).loadFromRedis();
        (diamondMinedMap = new DiamondMinedMap()).loadFromRedis();
        (goldMinedMap = new GoldMinedMap()).loadFromRedis();
        (ironMinedMap = new IronMinedMap()).loadFromRedis();
        (coalMinedMap = new CoalMinedMap()).loadFromRedis();
        (redstoneMinedMap = new RedstoneMinedMap()).loadFromRedis();
        (lapisMinedMap = new LapisMinedMap()).loadFromRedis();
        (emeraldMinedMap = new EmeraldMinedMap()).loadFromRedis();
        (firstJoinMap = new FirstJoinMap()).loadFromRedis();
        (lastJoinMap = new LastJoinMap()).loadFromRedis();
        (enderPearlsUsedMap = new EnderPearlsUsedMap()).loadFromRedis();
        (expCollectedMap = new ExpCollectedMap()).loadFromRedis();
        (itemsRepairedMap = new ItemsRepairedMap()).loadFromRedis();
        (splashPotionsBrewedMap = new SplashPotionsBrewedMap()).loadFromRedis();
        (splashPotionsUsedMap = new SplashPotionsUsedMap()).loadFromRedis();
        (wrappedBalanceMap = new WrappedBalanceMap()).loadFromRedis();
        (toggleFoundDiamondsMap = new ToggleFoundDiamondsMap()).loadFromRedis();
        (toggleDeathMessageMap = new ToggleDeathMessageMap()).loadFromRedis();
        (toggleClaimDisplayMap = new ToggleClaimDisplayMap()).loadFromRedis();
        (toggleClaimMessageMap = new ToggleClaimMessageMap()).loadFromRedis();
        (toggleFocusDisplayMap = new ToggleFocusDisplayMap()).loadFromRedis();
        (tabListModeMap = new TabListModeMap()).loadFromRedis();
        (ipMap = new IPMap()).loadFromRedis();
        (whitelistedIPMap = new WhitelistedIPMap()).loadFromRedis();
        (cobblePickupMap = new CobblePickupMap()).loadFromRedis();
        (mobDropsPickupMap = new MobDropsPickupMap()).loadFromRedis();
        (kdrMap = new KDRMap()).loadFromRedis();
        (gemMap = new GemMap()).loadFromRedis();
        (bountyCooldownMap = new BountyCooldownMap()).loadFromRedis();
    }

    private void setupTasks() {
        // unlocks claims at 10 minutes left of SOTW timer
        new BukkitRunnable() {
            @Override
            public void run() {
                if (CustomTimerCreateCommand.isSOTWTimer()) {
                    long endsAt = CustomTimerCreateCommand.getCustomTimers().get("&a&lSOTW");
                    if (endsAt - System.currentTimeMillis() <= TimeUnit.MINUTES.toMillis(10L)) {
                        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
                            if (team.isClaimLocked()) {
                                team.setClaimLocked(false);
                                team.sendMessage(CC.YELLOW + "Your faction's claims have been unlocked due to SOTW ending in 10 minutes!");
                            }
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(Foxtrot.getInstance(), 20L, 20L);
    }

}
