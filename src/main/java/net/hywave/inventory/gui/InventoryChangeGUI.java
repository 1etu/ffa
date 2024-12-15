package net.hywave.inventory.gui;

import net.hywave.events.BaseEvent;
import net.hywave.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryChangeGUI extends BaseEvent {
    private static final String GUI_TITLE = ColorUtil.colorize("&8Inventory Changes");
    private static final Map<Player, Map<Integer, Integer>> slotChanges = new HashMap<>();
    private static InventoryChangeGUI instance;

    public InventoryChangeGUI(JavaPlugin plugin) {
        super(plugin);
        instance = this;
    }

    public static InventoryChangeGUI getInstance() {
        return instance;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(GUI_TITLE)) {
            event.setCancelled(true);
        }
    }

    public static void showChanges(Player player, Map<Integer, ItemStack> before, Map<Integer, ItemStack> after) {
        Inventory gui = Bukkit.createInventory(null, 18, GUI_TITLE);
        Map<Integer, Integer> changes = new HashMap<>();

        ItemStack filler = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < 18; i++) {
            gui.setItem(i, filler);
        }

        for (Map.Entry<Integer, ItemStack> entry : before.entrySet()) {
            int slot = entry.getKey();
            if (slot >= 0 && slot <= 8) {
                ItemStack item = entry.getValue();
                if (item == null) continue;
                ItemStack clone = item.clone();
                gui.setItem(slot, clone);
            }
        }

        for (Map.Entry<Integer, ItemStack> entry : after.entrySet()) {
            int newSlot = entry.getKey();
            ItemStack item = entry.getValue();
            
            int oldSlot = -1;
            for (Map.Entry<Integer, ItemStack> beforeEntry : before.entrySet()) {
                if (isSameItem(beforeEntry.getValue(), item) && beforeEntry.getKey() != newSlot) {
                    oldSlot = beforeEntry.getKey();
                    break;
                }
            }

            if (oldSlot != -1) {
                changes.put(oldSlot, newSlot);
                
                if (oldSlot >= 0 && oldSlot <= 8) {
                    ItemStack barrier = new ItemStack(Material.BARRIER);
                    ItemMeta meta = barrier.getItemMeta();
                    String itemName = item.hasItemMeta() && item.getItemMeta().hasDisplayName() 
                        ? item.getItemMeta().getDisplayName() 
                        : formatMaterialName(item.getType().name());
                    meta.setDisplayName(ColorUtil.colorize("&cPosition changed of " + itemName));
                    List<String> lore = new ArrayList<>();
                    lore.add(ColorUtil.colorize("&7Previous Slot: " + (oldSlot + 1)));
                    lore.add(ColorUtil.colorize("&7New Slot: " + (newSlot + 1)));
                    meta.setLore(lore);
                    barrier.setItemMeta(meta);
                    gui.setItem(oldSlot, barrier);
                }
            }
        }

        for (Map.Entry<Integer, ItemStack> entry : after.entrySet()) {
            int slot = entry.getKey();
            if (slot >= 0 && slot <= 8) {
                ItemStack value = entry.getValue();
                if (value == null) continue;
                ItemStack item = value.clone();
                ItemMeta meta = item.getItemMeta();
                if (changes.containsValue(slot)) {
                    List<String> lore = new ArrayList<>();
                    for (Map.Entry<Integer, Integer> change : changes.entrySet()) {
                        if (change.getValue() == slot) {
                            lore.add(ColorUtil.colorize("&7Moved from slot " + (change.getKey() + 1)));
                            break;
                        }
                    }
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
                gui.setItem(slot + 9, item);
            }
        }

        slotChanges.put(player, changes);
        player.openInventory(gui);
    }

    private static String formatMaterialName(String name) {
        String[] words = name.toLowerCase().split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (result.length() > 0) result.append(" ");
            result.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
        }
        return result.toString();
    }

    private static boolean isSameItem(ItemStack item1, ItemStack item2) {
        if (item1 == null || item2 == null) return false;
        return item1.getType() == item2.getType() && 
               item1.getDurability() == item2.getDurability() &&
               item1.getAmount() == item2.getAmount();
    }
}
