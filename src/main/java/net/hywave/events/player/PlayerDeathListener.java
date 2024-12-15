package net.hywave.events.player;

import net.hywave.events.BaseEvent;
import net.hywave.inventory.InventoryManager;
import net.hywave.profile.PlayerLevel;
import net.hywave.utils.ColorUtil;
import net.hywave.utils.DropUtil;
import net.hywave.profile.ProfileManager;
import net.hywave.profile.PlayerProfile;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerDeathListener extends BaseEvent {

    public PlayerDeathListener(JavaPlugin plugin) {
        super(plugin);
        initializeDrops();
    }

    private void initializeDrops() {
        DropUtil.clearDrops();
        DropUtil.registerDrop(new ItemStack(Material.GOLDEN_APPLE), 75.0);
        DropUtil.registerDrop(new ItemStack(Material.DIAMOND), 15.0);
        DropUtil.registerDrop(new ItemStack(Material.EMERALD), 10.0);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLowHealth(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        double finalHealth = player.getHealth() - event.getFinalDamage();
        
        if (finalHealth <= 1.0) {
            event.setCancelled(true);
            player.setHealth(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.getDrops().clear();
        
        Player victim = event.getEntity();
        handlePlayerDeath(victim);
    }

    private void handlePlayerDeath(Player victim) {
        Player killer = victim.getKiller();
        Location deathLocation = victim.getLocation();
        
        DropUtil.processDrops(deathLocation);
        ProfileManager profileManager = ProfileManager.getInstance();
        
        if (killer != null) {
            PlayerProfile killerProfile = profileManager.getProfile(killer.getUniqueId());
            PlayerProfile victimProfile = profileManager.getProfile(victim.getUniqueId());
            
            if (killerProfile != null) {
                killerProfile.getStats().incrementKills();
                
                long xpReward = PlayerLevel.calculateKillXP(
                    killerProfile.getCurrentLevel(),
                    victimProfile != null ? victimProfile.getCurrentLevel() : 1
                );
                killerProfile.addExperience(xpReward);
                
                String healthBar = formatHealthBar(killer.getHealth());
                victim.sendMessage(ColorUtil.colorize("&cYou've been killed by &f" + killer.getName() + " &cwith " + healthBar + " &chealth"));
                killer.sendMessage(ColorUtil.colorize("&aYou've killed &f" + victim.getName() + " &awith " + healthBar + " &ahealth remaining"));
                killer.sendMessage(ColorUtil.colorize("&b+" + xpReward + " XP"));
                
                int killStreak = killerProfile.getStats().getCurrentKillStreak();
                if (killStreak > 2) {
                    Bukkit.broadcastMessage(ColorUtil.colorize("&6" + killer.getName() + " &eis on a &c" + killStreak + " &ekill streak!"));
                }
                
                double progress = killerProfile.getLevel().getProgressToNextLevel() * 100;
                killer.sendMessage(ColorUtil.colorize("&7Level " + killerProfile.getCurrentLevel() + " &8(&e" + String.format("%.1f", progress) + "%&8)"));
            }
            
            if (victimProfile != null) {
                victimProfile.getStats().incrementDeaths();
            }
        }
        
        startRespawnCountdown(victim);
    }
    
    private String formatHealthBar(double health) {
        int hearts = (int) (health / 2);
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i < hearts) {
                bar.append("&c❤");
            } else {
                bar.append("&8❤");
            }
        }
        return bar.toString();
    }
    
    private void startRespawnCountdown(Player player) {
        new BukkitRunnable() {
            int countdown = 5;
            
            @Override
            public void run() {
                if (countdown > 0) {
                    String color = getCountdownColor(countdown);
                    player.sendTitle(
                        ColorUtil.colorize(color + "Respawning in"),
                        ColorUtil.colorize(color + countdown)
                    );
                    countdown--;
                } else {
                    player.setGameMode(GameMode.SURVIVAL);
                    player.teleport(player.getWorld().getSpawnLocation());
                    player.sendTitle(
                        ColorUtil.colorize("&aRespawned!"),
                        ""
                    );
                    InventoryManager.getInstance().applyPreset(player);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    
    private String getCountdownColor(int seconds) {
        switch (seconds) {
            case 5: return "&a";
            case 4: return "&2";
            case 3: return "&e";
            case 2: return "&6";
            case 1: return "&c";
            default: return "&f";
        }
    }
}