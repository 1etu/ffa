package net.hywave.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.lang.reflect.Field;

public class PingUtil {
    public int getPlayerPing(Player player) {
        try {
            Object n = player.getClass().getField("handle").get(player);
            Field pingField = n.getClass().getField("ping");
            return (int) pingField.get(n);
        } catch (Exception e) {
            return -1;
        }
    }

    public String getPingQuality(int ping) {
        if (ping < 50) {
            return ChatColor.GREEN + "Excellent";
        } else if (ping < 100) {
            return ChatColor.DARK_GREEN + "Good";
        } else if (ping < 150) {
            return ChatColor.YELLOW + "Okay";
        } else if (ping < 250) {
            return ChatColor.RED + "Poor";
        } else {
            return ChatColor.DARK_RED + "Unplayable";
        }
    }

    public String generatePingBar(int ping) {
        StringBuilder bar = new StringBuilder();

        if (ping < 50) {
            bar.append(ChatColor.GREEN + "■■■■■");
        } else if (ping < 100) {
            bar.append(ChatColor.DARK_GREEN + "■■■■").append(ChatColor.GRAY + "■");
        } else if (ping < 150) {
            bar.append(ChatColor.YELLOW + "■■■").append(ChatColor.GRAY + "■■");
        } else if (ping < 250) {
            bar.append(ChatColor.RED + "■■").append(ChatColor.GRAY + "■■■");
        } else {
            bar.append(ChatColor.DARK_RED + "■").append(ChatColor.GRAY + "■■■■");
        }

        return bar.toString();
    }
}
