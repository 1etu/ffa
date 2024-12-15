package net.hywave.profile;

import com.google.gson.annotations.SerializedName;
import net.hywave.inventory.InventoryPreset;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class PlayerProfile {
    @SerializedName("uuid")
    private final UUID uuid;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("role")
    private PlayerRole role;
    
    @SerializedName("stats")
    private final PlayerStats stats;
    
    @SerializedName("inventory_preset")
    private Map<Integer, ItemStack> inventoryPreset;
    
    @SerializedName("level")
    private final PlayerLevel level;
    
    @SerializedName("xp")
    private long xp;
    
    @SerializedName("xp_needed")
    private int xpNeeded;
    
    @SerializedName("current_level")
    private int currentLevel;

    public PlayerProfile(UUID uuid) {
        this.uuid = uuid;
        this.stats = new PlayerStats();
        this.level = new PlayerLevel(uuid.toString());
        this.role = PlayerRole.USER;
        this.currentLevel = 1;
    }

    public PlayerProfile(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.role = PlayerRole.USER;
        this.stats = new PlayerStats();
        this.inventoryPreset = new InventoryPreset().getSlotItems();
        this.level = new PlayerLevel(uuid.toString());
        this.xp = 0;
        this.xpNeeded = 1000;
        this.currentLevel = 1;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlayerRole getRole() {
        return role;
    }

    public void setRole(PlayerRole role) {
        this.role = role;
    }

    public PlayerStats getStats() {
        return stats;
    }

    public Map<Integer, ItemStack> getInventoryPreset() {
        return inventoryPreset;
    }

    public void setInventoryPreset(Map<Integer, ItemStack> inventoryPreset) {
        this.inventoryPreset = inventoryPreset;
    }

    public PlayerLevel getLevel() {
        return level;
    }

    public void addExperience(long amount) {
        int oldLevel = this.currentLevel;
        level.addExperience(amount);
        
        int newLevel = level.getLevel();
        
        if (newLevel > oldLevel) {
            this.currentLevel = newLevel;
            level.levelUp(oldLevel);
        }
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
}
