package net.frozenorb.foxtrot.partner.impl.purge;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class MaskPurgePackage extends PartnerPackage {

    public static int protectionLevel = 6;

    private static final List<String> SKINS = Arrays.asList("anonim", "0keys", "CNov");

    private final List<UUID> equipped = new ArrayList<>();

    public MaskPurgePackage() {
        super("MaskPackage");

        new BukkitRunnable() {
            public void run() {
                for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                    boolean wearing = isWearing(player);
                    if (wearing && !CustomTimerCreateCommand.isPurgeTimer()) {
                        ItemStack helmet = player.getInventory().getHelmet();
                        player.getInventory().setHelmet(null);
                        player.getWorld().dropItemNaturally(player.getLocation(), helmet.clone());
                        player.sendMessage(CC.RED + "You can only be wearing " + helmet.getItemMeta().getDisplayName() + CC.RED + " during the purge!");
                        player.removePotionEffect(PotionEffectType.INVISIBILITY);
                    } else if (wearing) {
                        if (!equipped.contains(player.getUniqueId())) {
                            equipped.add(player.getUniqueId());
                        }
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0), true);
                    } else if (equipped.remove(player.getUniqueId())) {
                        player.removePotionEffect(PotionEffectType.INVISIBILITY);
                    }
                }
            }
        }.runTaskTimer(Foxtrot.getInstance(), 15L, 20L);
    }


    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }

    private boolean isWearing(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet == null || helmet.getType() != Material.SKULL_ITEM) {
            return false;
        }

        if (!helmet.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = helmet.getItemMeta();

        if (meta.getEnchantLevel(Enchantment.PROTECTION_ENVIRONMENTAL) != protectionLevel) {
            return false;
        }

        return getName().equals(meta.getDisplayName());
    }

    @Override
    public long getCooldownTime() {
        return 0;
    }

    @Override
    protected ItemStack partnerItem() {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(SKINS.get(ThreadLocalRandom.current().nextInt(SKINS.size())));
        meta.setDisplayName(getName());
        meta.setLore(Arrays.asList(
                CC.GRAY + "Apply this helmet during the purge for",
                CC.GRAY + "invisibility and protection 2"
        ));

        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protectionLevel);

        return item;
    }

    @Override
    public String getName() {
        return "§4§lPurge Mask";
    }

    @Override
    public int getAmount() {
        return 2;
    }

    @Override
    public boolean isPurge() {
        return true;
    }
}
