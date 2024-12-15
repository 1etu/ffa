package net.hywave;

import net.hywave.commands.BaseCommand;
import net.hywave.commands.ping.PingCommand;
import net.hywave.commands.announcements.AnnouncementManager;
import net.hywave.commands.announcements.AnnouncementCommand;
import net.hywave.commands.inventory.SavePresetCommand;
import net.hywave.commands.inventory.ViewChangesCommand;
import net.hywave.commands.inventory.RevertChangesCommand;
import net.hywave.commands.stats.StatsCommand;
import net.hywave.events.EventManager;
import net.hywave.events.player.PlayerDeathListener;
import net.hywave.events.player.PlayerJoinListener;
import net.hywave.events.player.JoinMessageListener;
import net.hywave.inventory.InventoryManager;
import net.hywave.inventory.gui.InventoryChangeGUI;
import net.hywave.profile.ProfileManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BaseClass extends JavaPlugin {

    @Override
    public void onEnable() {
        EventManager.initialize(this);
        InventoryManager.initialize(this);
        ProfileManager.initialize(this);
        AnnouncementManager.initialize(this);


        EventManager.getInstance().registerEvent(new PlayerDeathListener(this));
        EventManager.getInstance().registerEvent(new PlayerJoinListener(this));
        EventManager.getInstance().registerEvent(new JoinMessageListener(this));
        EventManager.getInstance().registerEvent(new InventoryChangeGUI(this));

        BaseCommand cmdSys = new BaseCommand(this);
        cmdSys.registerCommands(new PingCommand());
        cmdSys.registerCommands(new AnnouncementCommand());
        cmdSys.registerCommands(new SavePresetCommand());
        cmdSys.registerCommands(new ViewChangesCommand());
        cmdSys.registerCommands(new RevertChangesCommand());
        cmdSys.registerCommands(new StatsCommand());
    }

    @Override
    public void onDisable() {
        EventManager.getInstance().unregisterAll();
    }
}
