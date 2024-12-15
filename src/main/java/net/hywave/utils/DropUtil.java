package net.hywave.utils;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DropUtil {
    private static final Map<ItemStack, Double> dropChances = new HashMap<>();
    private static final Random random = new Random();

    public static void registerDrop(ItemStack item, double chance) {
        if (chance > 0 && chance <= 100) {
            dropChances.put(item, chance);
        }
    }

    public static void clearDrops() {
        dropChances.clear();
    }

    public static void processDrops(Location location) {
        if (location == null) return;

        dropChances.forEach((item, chance) -> {
            if (random.nextDouble() * 100 <= chance) {
                location.getWorld().dropItemNaturally(location, item);
            }
        });
    }
}
