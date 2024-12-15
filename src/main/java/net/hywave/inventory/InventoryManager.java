package net.hywave.inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.hywave.inventory.gui.InventoryChangeGUI;
import net.hywave.utils.ColorUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.hywave.profile.PlayerProfile;
import net.hywave.profile.ProfileManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {
    private static InventoryManager instance;
    private final JavaPlugin plugin;
    private final Gson gson;

    private InventoryManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
                .setPrettyPrinting()
                .create();
    }

    public static void initialize(JavaPlugin plugin) {
        if (instance == null) {
            instance = new InventoryManager(plugin);
        }
    }

    public static InventoryManager getInstance() {
        return instance;
    }

    public void saveCurrentInventory(Player player) {
        PlayerProfile profile = ProfileManager.getInstance().getProfile(player.getUniqueId());
        if (profile == null) return;

        Map<Integer, ItemStack> beforeItems = profile.getInventoryPreset();
        PlayerInventory inventory = player.getInventory();

        Map<Integer, ItemStack> newPreset = new HashMap<>();

        newPreset.put(-4, inventory.getHelmet());
        newPreset.put(-3, inventory.getChestplate());
        newPreset.put(-2, inventory.getLeggings());
        newPreset.put(-1, inventory.getBoots());

        for (int i = 0; i <= 35; i++) {
            newPreset.put(i, inventory.getItem(i));
        }

        profile.setInventoryPreset(newPreset);
        ProfileManager.getInstance().updateProfile(profile);

        TextComponent message = new TextComponent(ColorUtil.colorize("&aYour inventory preset has been saved.\n"));
        
        TextComponent viewChanges = new TextComponent(ColorUtil.colorize("&e[Click to see changes]"));
        viewChanges.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new ComponentBuilder(ColorUtil.colorize("&7Click to view inventory changes")).create()));
        viewChanges.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, 
            "/viewchanges " + player.getName()));
        
        TextComponent separator = new TextComponent(ColorUtil.colorize(" &8| "));
        
        TextComponent revertChanges = new TextComponent(ColorUtil.colorize("&c[Revert Changes]"));
        revertChanges.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new ComponentBuilder(ColorUtil.colorize("&7Click to revert to previous layout")).create()));
        revertChanges.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, 
            "/revertchanges " + player.getName()));
        
        message.addExtra(viewChanges);
        message.addExtra(separator);
        message.addExtra(revertChanges);
        
        player.spigot().sendMessage(message);

        player.setMetadata("inventory_changes", 
            new FixedMetadataValue(plugin, new Object[]{beforeItems, newPreset}));
    }

    public void applyPreset(Player player) {
        PlayerProfile profile = ProfileManager.getInstance().getProfile(player.getUniqueId());
        if (profile == null) return;

        PlayerInventory inventory = player.getInventory();
        inventory.clear();

        Map<Integer, ItemStack> preset = profile.getInventoryPreset();
        for (Map.Entry<Integer, ItemStack> entry : preset.entrySet()) {
            int slot = entry.getKey();
            ItemStack item = entry.getValue();
            
            if (item == null) continue;

            if (slot < 0) {
                switch (slot) {
                    case -4: inventory.setHelmet(item.clone()); break;
                    case -3: inventory.setChestplate(item.clone()); break;
                    case -2: inventory.setLeggings(item.clone()); break;
                    case -1: inventory.setBoots(item.clone()); break;
                }
            } else {
                inventory.setItem(slot, item.clone());
            }
        }
    }
}
