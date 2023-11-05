package net.frozenorb.foxtrot.partner;

import net.frozenorb.foxtrot.persist.PersistMap;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class PartnerPackage extends PersistMap<Instant> implements Listener {

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    private static final long GLOBAL_COOLDOWN_TIME = 10L; // in seconds

    static final Map<UUID, Instant> GLOBAL_PACKAGE_COOLDOWN = new HashMap<>();

    protected static void setGlobalCooldown(Player player) {
        GLOBAL_PACKAGE_COOLDOWN.put(player.getUniqueId(), Instant.now().plusSeconds(GLOBAL_COOLDOWN_TIME));
    }

    public static boolean isOnGlobalPackageCooldown(Player player) {
        Instant cooldownTime = GLOBAL_PACKAGE_COOLDOWN.get(player.getUniqueId());
        return cooldownTime != null && cooldownTime.isAfter(Instant.now());
    }

    public static long getGlobalCooldownTime(Player player) {
        Instant cooldownTime = GLOBAL_PACKAGE_COOLDOWN.get(player.getUniqueId());
        return cooldownTime == null ? -1 : Duration.between(Instant.now(), cooldownTime).getSeconds();
    }

    public static String getGlobalCooldownTimeFormatted(Player player) {
        Duration duration = Duration.between(Instant.now(), GLOBAL_PACKAGE_COOLDOWN.get(player.getUniqueId()));
        return DECIMAL_FORMAT.format((double) duration.toMillis() / (double) 1000);
    }

    protected final ItemStack partnerItem = getPartnerItem();

    protected PartnerPackage(String dbName) {
        super("CD:" + dbName, dbName + "_cd", false);
    }

    public boolean isPartnerItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return false;
        if (!itemStack.hasItemMeta())
            return false;

        ItemMeta meta = itemStack.getItemMeta();
        ItemMeta partnerMeta = partnerItem.getItemMeta();
        if (!partnerMeta.getDisplayName().equals(meta.getDisplayName()))
            return false;
        return Bukkit.getItemFactory().equals(partnerMeta, meta);
    }

    public boolean isOnCooldown(Player player) {
        Instant lastUseTime = getValue(player.getUniqueId());
        return isOnGlobalPackageCooldown(player) || (lastUseTime != null && lastUseTime.isAfter(Instant.now()));
    }

    public void setCooldown(Player player) {
        Instant now = Instant.now();
        updateValueAsync(player.getUniqueId(), now.plusSeconds(getCooldownTime()));
    }

    public void resetCooldown(Player player) {
        GLOBAL_PACKAGE_COOLDOWN.remove(player.getUniqueId());
        updateValueAsync(player.getUniqueId(), Instant.MIN);
    }

    public long getCooldownTime(Player player) {
        Instant currentTime = Instant.now();
        Instant globalCooldown = GLOBAL_PACKAGE_COOLDOWN.get(player.getUniqueId());
        Instant localCooldown = getValue(player.getUniqueId());
        long time = 0;
        if (localCooldown != null && localCooldown.isAfter(currentTime)) {
            time = Duration.between(currentTime, localCooldown).getSeconds();
            long globalTime;
            if (globalCooldown != null && (globalTime = getGlobalCooldownTime(player)) > time)
                time = globalTime;
        } else if (globalCooldown != null) {
            long globalCooldownTime = getGlobalCooldownTime(player);
            if (globalCooldownTime > 0)
                time = globalCooldownTime;
        }
        return time;
    }

    public String getCooldownMessage(Player player) {
        return CC.PINK + CC.BOLD + getName() + " " + CC.RED + "is on cooldown for another " + CC.BOLD +
                TimeUtils.formatIntoDetailedString((int) getCooldownTime(player)) + CC.RED + "!";
    }

    protected void sendActivationMessages(Player attacker,
                                          String[] attackContent,
                                          Player victim,
                                          String[] victimContent) {
        attacker.sendMessage(" ");
        attacker.sendMessage(
                CC.DARK_GREEN + CC.STAR + " " + CC.GREEN +
                        attackContent[0]
        );
        if (attackContent.length > 1) {
            for (int i = 1; i < attackContent.length; i++) {
                attacker.sendMessage(
                        CC.GOLD + CC.STAR + " " + CC.YELLOW +
                                attackContent[i]
                );
            }
        }

        attacker.sendMessage(
                CC.DARK_RED + CC.STAR + " " + CC.RED +
                        "Cooldown: " + CC.WHITE + getCooldownTime() + "s"
        );
        attacker.sendMessage(" ");

        // target messages
        if (victim != null) {
            victim.sendMessage(" ");
            victim.sendMessage(
                    CC.DARK_RED + CC.STAR + " " + CC.RED + victimContent[0]
            );
            if (victimContent.length > 1) {
                for (int i = 1; i < victimContent.length; i++) {
                    victim.sendMessage(
                            CC.GOLD + CC.STAR + " " + CC.YELLOW + victimContent[i]
                    );
                }
            }
            victim.sendMessage(" ");
        }

    }

    public boolean isPurge() {
        return false;
    }

    public boolean isExclusive() {
        return true;
    }

    protected Runnable tickTask() {
        return null;
    }

    protected abstract boolean onUse(PlayerInteractEvent event);

    // cool down time in seconds
    public abstract long getCooldownTime();

    public ItemStack getPartnerItem() {
//		net.minecraft.server.v1_7_R4.ItemStack stack = CraftItemStack.asNMSCopy(partnerItem());
//
//		NBTTagCompound tag = stack.hasTag() ? stack.getTag() : new NBTTagCompound();
//
//		tag.setString("partner", "item");
//
//		return CraftItemStack.asBukkitCopy(stack);
        return partnerItem();
    }

    protected abstract ItemStack partnerItem();

    public abstract String getName();

    public abstract int getAmount();

    protected void consume(Player player, ItemStack partnerItem) {
        InventoryUtils.removeAmountFromInventory(player.getInventory(), partnerItem, 1);
    }

    @Override
    public String getRedisValue(Instant instant) {
        return instant.toString();
    }

    @Override
    public Instant getJavaObject(String str) {
        return Instant.parse(str);
    }

    @Override
    public Object getMongoValue(Instant instant) {
        return instant.toString();
    }

}
