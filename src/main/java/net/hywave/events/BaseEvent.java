package net.hywave.events;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class BaseEvent implements Listener {
    protected final JavaPlugin plugin;

    public BaseEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        register();
    }

    private void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    public void unregister() {
        org.bukkit.event.HandlerList.unregisterAll(this);
    }
}
