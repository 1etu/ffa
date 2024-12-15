package net.hywave.events.player;

import net.hywave.events.BaseEvent;
import net.hywave.profile.ProfileManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerJoinListener extends BaseEvent {
    
    public PlayerJoinListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        ProfileManager.getInstance().loadProfile(player).thenAccept(profile -> {
            player.getInventory().clear();
            profile.getInventoryPreset().forEach((slot, item) -> {
                if (slot < 0) {
                    switch (slot) {
                        case -4: player.getInventory().setHelmet(item); break;
                        case -3: player.getInventory().setChestplate(item); break;
                        case -2: player.getInventory().setLeggings(item); break;
                        case -1: player.getInventory().setBoots(item); break;
                    }
                } else {
                    player.getInventory().setItem(slot, item);
                }
            });
        });
    }
}
