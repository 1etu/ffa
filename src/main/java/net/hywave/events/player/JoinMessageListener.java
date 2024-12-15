package net.hywave.events.player;

import net.hywave.events.BaseEvent;
import net.hywave.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class JoinMessageListener extends BaseEvent {
    private static final Random RANDOM = new Random();
    private static final List<String> JOIN_MESSAGES = Arrays.asList(
        "has landed!",
        "joined the party!",
        "made an entrance!",
        "dropped in!",
        "appeared out of thin air!",
        "crashed the party!",
        "snuck in!",
        "spawned in!",
        "entered the chat!",
        "has arrived!",
        "joined the game!",
        "is here to play!",
        "made their debut!",
        "showed up!"
    );

    public JoinMessageListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        String nameColor = getNameColor();
        
        int playerCount = Bukkit.getOnlinePlayers().size();
        String countColor = getPlayerCountColor(playerCount);
        
        String randomMessage = JOIN_MESSAGES.get(RANDOM.nextInt(JOIN_MESSAGES.size()));
        
        event.setJoinMessage(ColorUtil.colorize(
            nameColor + playerName + " &7" + randomMessage + " " +
            "&8(" + countColor + playerCount + "&7/" + countColor + "32&8)"
        ));
    }

    private String getNameColor() {
        return "&f";
    }

    private String getPlayerCountColor(int playerCount) {
        if (playerCount <= 20) {
            return "&a";
        } else if (playerCount <= 28) {
            return "&e";
        } else {
            return "&c";
        }
    }
}
