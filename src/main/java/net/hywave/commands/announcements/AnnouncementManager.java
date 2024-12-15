package net.hywave.commands.announcements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.hywave.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementManager {
    private static AnnouncementManager instance;
    private final File dataFile;
    private final List<String> announcements;
    private int currentIndex;
    private int interval;
    private int taskId;
    private final JavaPlugin plugin;

    private AnnouncementManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.announcements = new ArrayList<>();
        this.dataFile = new File(plugin.getDataFolder(), "data/announcements.json");
        this.interval = 300;
        this.currentIndex = 0;
        this.taskId = -1;
        loadData();
        startAnnouncer();
    }

    public static void initialize(JavaPlugin plugin) {
        if (instance == null) {
            instance = new AnnouncementManager(plugin);
        }
    }

    public static AnnouncementManager getInstance() {
        return instance;
    }

    private void loadData() {
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            saveData();
            return;
        }

        try (Reader reader = new FileReader(dataFile)) {
            AnnouncementData data = new Gson().fromJson(reader, AnnouncementData.class);
            announcements.clear();
            announcements.addAll(data.announcements);
            interval = data.interval;
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to load announcements: " + e.getMessage());
        }
    }

    private void saveData() {
        try (Writer writer = new FileWriter(dataFile)) {
            AnnouncementData data = new AnnouncementData(announcements, interval);
            new GsonBuilder().setPrettyPrinting().create().toJson(data, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save announcements: " + e.getMessage());
        }
    }

    private void startAnnouncer() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (!announcements.isEmpty()) {
                Bukkit.broadcastMessage(ColorUtil.colorize(announcements.get(currentIndex)));
                currentIndex = (currentIndex + 1) % announcements.size();
            }
        }, interval * 20L, interval * 20L);
    }

    public List<String> getAnnouncements() {
        return new ArrayList<>(announcements);
    }

    public void addAnnouncement(String message) {
        announcements.add(message);
        saveData();
    }

    public void removeAnnouncement(int index) {
        announcements.remove(index);
        saveData();
    }

    public void setInterval(int seconds) {
        this.interval = seconds;
        saveData();
        startAnnouncer();
    }

    private static class AnnouncementData {
        private final List<String> announcements;
        private final int interval;

        public AnnouncementData(List<String> announcements, int interval) {
            this.announcements = announcements;
            this.interval = interval;
        }
    }
}