package net.hywave.inventory;

import com.google.gson.annotations.SerializedName;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;

public class InventoryPreset {
    @SerializedName("slot_items")
    private final Map<Integer, ItemStack> slotItems;

    public InventoryPreset() {
        this.slotItems = new HashMap<>();
        setDefaultItems();
    }

    private void setDefaultItems() {
        slotItems.put(-4, new ItemStack(Material.CHAINMAIL_HELMET));    // Helmet slot
        slotItems.put(-3, new ItemStack(Material.IRON_CHESTPLATE));     // Chestplate slot
        slotItems.put(-2, new ItemStack(Material.GOLD_LEGGINGS));       // Leggings slot
        slotItems.put(-1, new ItemStack(Material.DIAMOND_BOOTS));       // Boots slot

        slotItems.put(0, new ItemStack(Material.IRON_SWORD));          // Slot 1
        slotItems.put(1, new ItemStack(Material.FLINT_AND_STEEL));     // Slot 2
        slotItems.put(2, new ItemStack(Material.FISHING_ROD));         // Slot 3
        slotItems.put(3, new ItemStack(Material.BOW));                 // Slot 4
        
        ItemStack arrows = new ItemStack(Material.ARROW, 16);
        slotItems.put(8, arrows);                                      // Slot 9 (last slot)
    }

    public Map<Integer, ItemStack> getSlotItems() {
        return new HashMap<>(slotItems);
    }

    public void setItem(int slot, ItemStack item) {
        if (item != null) {
            slotItems.put(slot, item.clone());
        } else {
            slotItems.remove(slot);
        }
    }

    public ItemStack getItem(int slot) {
        return slotItems.get(slot);
    }
}
