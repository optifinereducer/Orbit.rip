package net.frozenorb.foxtrot.partner.impl.old;

import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Pair;
import net.frozenorb.qlib.util.ItemBuilder;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class DylanShell extends PartnerPackage {

    private static final int ACTIVE_TIME = 10; // how long it's active for

    private final Map<Player, Pair<Instant, Integer>> activationMap = new ConcurrentHashMap<>();

    public DylanShell() {
        super("DShell");
    }

    @Override
    protected Runnable tickTask() {
        return () -> activationMap.entrySet().removeIf(entry -> {
            Player player = entry.getKey();
            if (player == null || !player.isOnline())
                return true;

            Pair<Instant, Integer> active = entry.getValue();
            if (!active.first.isBefore(Instant.now()))
                return false;

            int hits = Math.min(active.second, 10);

            float absorption = hits == 0 ? 0.0f : hits / 2.0f;

            if (absorption > 0) {
                setAbsorption(player, absorption);
                player.sendMessage(ChatColor.RED + "You got " + absorption + " hearts of absorption");
            }

            return true;
        });
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player entity = (Player) event.getEntity();
            Pair<Instant, Integer> active = activationMap.get(entity);
            if (active == null)
                return;

            int hits = active.second;

            activationMap.put(entity, new Pair<>(active.first, ++hits));
        }
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        setGlobalCooldown(player);
        setCooldown(player);
        sendActivationMessages(player,
                new String[]{
                        "You have activated " + getName() + CC.GREEN + "!",
                        "You will receive up to 5 hearts of absorption."
                }, null, null);
        activationMap.put(player, new Pair<>(Instant.now().plusSeconds(ACTIVE_TIME), 0));
        return true;
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        activationMap.remove(event.getPlayer());
    }

    private void setAbsorption(Player player, float hearts) {
        EntityPlayer entity = ((CraftPlayer) player).getHandle();
        entity.setAbsorptionHearts((hearts * 2.0f) + entity.getAbsorptionHearts());
    }

    @Override
    public boolean isPartnerItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return false;

        if (!itemStack.hasItemMeta())
            return false;

        ItemMeta partnerItemMeta = partnerItem.getItemMeta();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!partnerItemMeta.getDisplayName().equalsIgnoreCase(itemMeta.getDisplayName()))
            return false;

        return Arrays.equals(partnerItemMeta.getLore().toArray(), itemMeta.getLore().toArray());
    }

    @Override
    public long getCooldownTime() {
        return CustomTimerCreateCommand.isPartnerPackageHour() ? 60L : TimeUnit.MINUTES.toSeconds(3);
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.EMERALD)
                .name("&a&lDylan’s Shell")
                .addToLore(
                        "&7Upon right-clicking, each hit received ",
                        "&7will equate to half a heart of absorption",
                        "&7after a 5 second period (Max 5 Hearts)."
                )
                .enchant(Enchantment.DURABILITY, 1)
                .build();
    }

    @Override
    public String getName() {
        return "§a§lDylan's Shell";
    }

    @Override
    public int getAmount() {
        return 2;
    }
}
