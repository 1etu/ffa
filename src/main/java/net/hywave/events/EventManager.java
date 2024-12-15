package net.hywave.events;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private static EventManager instance;
    private final JavaPlugin plugin;
    private final List<BaseEvent> registeredEvents;

    private EventManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registeredEvents = new ArrayList<>();
    }

    public static void initialize(JavaPlugin plugin) {
        if (instance == null) {
            instance = new EventManager(plugin);
        }
    }

    public static EventManager getInstance() {
        return instance;
    }

    public <T extends BaseEvent> T registerEvent(T event) {
        registeredEvents.add(event);
        return event;
    }

    public void unregisterAll() {
        for (BaseEvent event : registeredEvents) {
            event.unregister();
        }
        registeredEvents.clear();
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
