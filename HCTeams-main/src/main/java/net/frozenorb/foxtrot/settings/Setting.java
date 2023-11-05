package net.frozenorb.foxtrot.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.settings.menu.button.SettingButton;
import net.frozenorb.foxtrot.tab.TabListMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@AllArgsConstructor
public enum Setting {

    PUBLIC_CHAT(
            ChatColor.LIGHT_PURPLE + "Public Chat",
            ChatColor.GRAY + "Do you want to see public chat messages?",
            Material.SIGN,
            ChatColor.GREEN + "Show public chat",
            ChatColor.RED + "Hide public chat",
            true
    ) {

        @Override
        public void toggle(Player player) {
            boolean value = !Foxtrot.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getUniqueId());

            Foxtrot.getInstance().getToggleGlobalChatMap().setGlobalChatToggled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.YELLOW + "You are now " + (value ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.YELLOW + " to see global chat messages.");
        }

        @Override
        public boolean isEnabled(Player player) {
            return Foxtrot.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getUniqueId());
        }

    },
    DISPLAY_CLAIM_ON_SCOREBOARD(
            ChatColor.LIGHT_PURPLE + "Display Claim On Scoreboard",
            ChatColor.GRAY + "Do you want to see the claim you're in on your scoreboard?",
            Material.COMPASS,
            ChatColor.GREEN + "Show claim",
            ChatColor.RED + "Hide claim",
            true
    ) {

        @Override
        public void toggle(Player player) {
            boolean value = !Foxtrot.getInstance().getToggleClaimDisplayMap().isClaimDisplayEnabled(player.getUniqueId());

            Foxtrot.getInstance().getToggleClaimDisplayMap().setClaimDisplayEnabled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.YELLOW + "You are now " + (value ? ChatColor.GREEN + "seeing" : ChatColor.RED + "hiding") + ChatColor.YELLOW + " the claim scoreboard display.");
        }

        @Override
        public boolean isEnabled(Player player) {
            return Foxtrot.getInstance().getToggleClaimDisplayMap().isClaimDisplayEnabled(player.getUniqueId());
        }

    },
    DISPLAY_FOCUS_ON_SCOREBOARD(
            ChatColor.LIGHT_PURPLE + "Display Focus On Scoreboard",
            ChatColor.GRAY + "Do you want to see information about the focused faction?",
            Material.WATCH,
            ChatColor.GREEN + "Show information",
            ChatColor.RED + "Hide information",
            true
    ) {

        @Override
        public void toggle(Player player) {
            boolean value = !Foxtrot.getInstance().getToggleFocusDisplayMap().isFocusDisplayEnabled(player.getUniqueId());

            Foxtrot.getInstance().getToggleFocusDisplayMap().setFocusDisplayEnabled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.YELLOW + "You are now " + (value ? ChatColor.GREEN + "seeing" : ChatColor.RED + "hiding") + ChatColor.YELLOW + " focus information.");
        }

        @Override
        public boolean isEnabled(Player player) {
            return Foxtrot.getInstance().getToggleFocusDisplayMap().isFocusDisplayEnabled(player.getUniqueId());
        }

    },
    FOUND_DIAMONDS(
            ChatColor.LIGHT_PURPLE + "Found Diamonds",
            ChatColor.GRAY + "Do you want to see found-diamonds messages?",
            Material.DIAMOND_ORE,
            ChatColor.GREEN + "Show messages",
            ChatColor.RED + "Hide messages",
            true
    ) {

        @Override
        public void toggle(Player player) {
            boolean value = !Foxtrot.getInstance().getToggleFoundDiamondsMap().isFoundDiamondToggled(player.getUniqueId());

            Foxtrot.getInstance().getToggleFoundDiamondsMap().setFoundDiamondToggled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.YELLOW + "You are now " + (value ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.YELLOW + " to see found diamond messages.");
        }

        @Override
        public boolean isEnabled(Player player) {
            return Foxtrot.getInstance().getToggleFoundDiamondsMap().isFoundDiamondToggled(player.getUniqueId());
        }

    },
    TAB_LIST(
            ChatColor.LIGHT_PURPLE + "Tab List Info",
            ChatColor.GRAY + "Do you want to see extra info on your tab list?",
            Material.PAINTING,
            ChatColor.GREEN + "Show info on tab",
            ChatColor.RED + "Show default tab",
            true
    ) {

        @Override
        public void toggle(Player player) {
            TabListMode mode = SettingButton.next(Foxtrot.getInstance().getTabListModeMap().getTabListMode(player.getUniqueId()));

            Foxtrot.getInstance().getTabListModeMap().setTabListMode(player.getUniqueId(), mode);
            player.sendMessage(ChatColor.YELLOW + "You've set your tab list mode to " + ChatColor.LIGHT_PURPLE + mode.getName() + ChatColor.YELLOW + ".");
        }

        @Override
        public boolean isEnabled(Player player) {
            return true;
        }

    },
    DEATH_MESSAGES(
            ChatColor.LIGHT_PURPLE + "Death Messages",
            ChatColor.GRAY + "Do you want to see death messages?",
            Material.SKULL_ITEM,
            ChatColor.GREEN + "Show messages",
            ChatColor.RED + "Hide messages",
            true
    ) {
        @Override
        public void toggle(Player player) {
            boolean value = !Foxtrot.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId());

            Foxtrot.getInstance().getToggleDeathMessageMap().setDeathMessagesEnabled(player.getUniqueId(), value);
            player.sendMessage(ChatColor.YELLOW + "You are now " + (value ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.YELLOW + " to see death messages.");
        }

        @Override
        public boolean isEnabled(Player player) {
            return Foxtrot.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId());
        }
    };

    @Getter private String name;
    @Getter private String description;
    @Getter private Material icon;
    @Getter private String enabledText;
    @Getter private String disabledText;
    private boolean defaultValue;

    // Using @Getter means the method would be 'isDefaultValue',
    // which doesn't correctly represent this variable.
    public boolean getDefaultValue() {
        return (defaultValue);
    }

    public SettingButton toButton() {
        return new SettingButton(this);
    }

    public abstract void toggle(Player player);

    public abstract boolean isEnabled(Player player);

}
