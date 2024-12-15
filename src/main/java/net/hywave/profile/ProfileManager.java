package net.hywave.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.hywave.inventory.ItemStackAdapter;
import net.hywave.utils.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ProfileManager {
    private static ProfileManager instance;
    private final JavaPlugin plugin;
    private final File profilesFile;
    private final Map<UUID, PlayerProfile> profiles;
    private final Gson gson;

    private ProfileManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.profilesFile = new File(plugin.getDataFolder(), "data/profiles.json");
        this.profiles = new HashMap<>();
        this.gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
                .setPrettyPrinting()
                .create();
        loadProfiles();
    }

    public static void initialize(JavaPlugin plugin) {
        if (instance == null) {
            instance = new ProfileManager(plugin);
        }
    }

    public static ProfileManager getInstance() {
        return instance;
    }

    private void loadProfiles() {
        if (!profilesFile.exists()) {
            profilesFile.getParentFile().mkdirs();
            saveProfiles();
            return;
        }

        try (Reader reader = new FileReader(profilesFile)) {
            Type type = new TypeToken<Map<UUID, PlayerProfile>>(){}.getType();
            Map<UUID, PlayerProfile> loadedProfiles = gson.fromJson(reader, type);
            if (loadedProfiles != null) {
                profiles.putAll(loadedProfiles);
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to load profiles: " + e.getMessage());
        }
    }

    public void saveProfiles() {
        try (Writer writer = new FileWriter(profilesFile)) {
            gson.toJson(profiles, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save profiles: " + e.getMessage());
        }
    }

    public CompletableFuture<PlayerProfile> loadProfile(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            PlayerProfile profile = profiles.get(player.getUniqueId());
            
            if (profile == null) {
                player.sendMessage(ColorUtil.colorize("&ePlease wait, we're creating a profile for your account..."));
                profile = new PlayerProfile(player.getUniqueId(), player.getName());
                profiles.put(player.getUniqueId(), profile);
                saveProfiles();
            } else {
                player.sendMessage(ColorUtil.colorize("&aProfile loaded successfully!"));
                if (!profile.getName().equals(player.getName())) {
                    profile.setName(player.getName());
                    saveProfiles();
                }
            }
            
            return profile;
        });
    }

    public PlayerProfile getProfile(UUID uuid) {
        return profiles.get(uuid);
    }

    public void updateProfile(PlayerProfile profile) {
        profiles.put(profile.getUuid(), profile);
        saveProfiles();
    }
}
