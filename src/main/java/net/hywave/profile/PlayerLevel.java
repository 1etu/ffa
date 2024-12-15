package net.hywave.profile;

import com.google.gson.annotations.SerializedName;
import net.hywave.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerLevel {
    @SerializedName("level")
    private int level;
    
    @SerializedName("experience")
    private long experience;

    @SerializedName("uuid")
    private final String playerUUID;
    
    public PlayerLevel(String playerUUID) {
        this.level = 1;
        this.experience = 0;
        this.playerUUID = playerUUID;
    }
    
    public int getLevel() {
        return level;
    }
    
    public long getExperience() {
        return experience;
    }
    
    public long getExperienceForNextLevel() {
        return calculateRequiredXP(level + 1);
    }
    
    public long getExperienceInCurrentLevel() {
        return experience - calculateRequiredXP(level);
    }
    
    public double getProgressToNextLevel() {
        long currentLevelXP = calculateRequiredXP(level);
        long nextLevelXP = calculateRequiredXP(level + 1);
        long requiredXP = nextLevelXP - currentLevelXP;
        long progressXP = experience - currentLevelXP;
        return (double) progressXP / requiredXP;
    }
    
    public void addExperience(long amount) {
        this.experience += amount;
        checkLevelUp();
    }
    
    private void checkLevelUp() {
        int oldLevel = level;
        while (experience >= calculateRequiredXP(level + 1)) {
            level++;
        }
        if (level > oldLevel) {
            levelUp(oldLevel);
        }
    }

    public void levelUp(int oldLevel) {
        Player player = Bukkit.getPlayer(UUID.fromString(playerUUID));
        if (player == null || !player.isOnline()) return;

        int levelsGained = level - oldLevel;

        String levelUpMessage = "&b⚡ &f" + player.getName() + " &7has reached level &b" + level + "&7!";
        if (levelsGained > 1) {
            levelUpMessage += " &7(&b+" + levelsGained + " levels&7)";
        }
        Bukkit.broadcastMessage(ColorUtil.colorize(levelUpMessage));

        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
        
        if (level % 5 == 0) {
            String milestoneMessage = "&b⚔ &f" + player.getName() + " &7has reached a milestone: &bLevel " + level + "&7!";
            Bukkit.broadcastMessage(ColorUtil.colorize(milestoneMessage));
            
            player.playSound(player.getLocation(), Sound.BAT_DEATH, 1.0f, 1.0f);
        }

        long nextLevelXP = calculateRequiredXP(level + 1) - calculateRequiredXP(level);
        player.sendMessage(ColorUtil.colorize("&8&m                                                    "));
        player.sendMessage(ColorUtil.colorize("&b&l LEVEL UP!"));
        player.sendMessage(ColorUtil.colorize("&7 You are now level &b" + level));
        player.sendMessage(ColorUtil.colorize("&7 Next level requires: &b" + nextLevelXP + " XP"));
        player.sendMessage(ColorUtil.colorize("&8&m                                                    "));
    }

    public long calculateRequiredXP(int level) {
        if (level <= 1) return 0;
        
        if (level <= 10) {
            return (long) (500 * Math.pow(level, 1.5));
        } else if (level <= 30) {
            return (long) (1000 * Math.pow(level, 1.8));
        } else {
            return (long) (2000 * Math.pow(level, 2));
        }
    }
    
    public static long calculateKillXP(int killerLevel, int victimLevel) {
        int levelDiff = victimLevel - killerLevel;
        double baseXP = 100;
        
        if (levelDiff > 0) {
            return (long) (baseXP * (1 + (levelDiff * 0.2)));
        } else if (levelDiff < 0) {
            return (long) Math.max(baseXP * (1 + (levelDiff * 0.1)), baseXP * 0.1);
        }
        
        return (long) baseXP;
    }
}
