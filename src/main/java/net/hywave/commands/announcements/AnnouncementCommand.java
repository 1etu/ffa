package net.hywave.commands.announcements;

import net.hywave.commands.BaseCommand;
import net.hywave.utils.ColorUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

@BaseCommand.CommandInfo(
    name = "announcement",
    description = "Manage server announcements",
    aliases = {"announce", "broadcast"},
    permission = "hywave.announcement"
)
public class AnnouncementCommand {
    private final AnnouncementManager manager;

    public AnnouncementCommand() {
        this.manager = AnnouncementManager.getInstance();
    }

    @BaseCommand.SubCommand(
        name = "list",
        description = "List all announcements",
        permission = "hywave.announcement.list"
    )
    public void handleList(CommandSender sender, String[] args) {
        List<String> announcements = manager.getAnnouncements();
        if (announcements.isEmpty()) {
            sender.sendMessage(ColorUtil.colorize("&c&lISSUE: &7No announcements found."));
            return;
        }

        sender.sendMessage(ColorUtil.colorize("&8&l» &7Current Announcements:"));
        for (int i = 0; i < announcements.size(); i++) {
            sender.sendMessage(ColorUtil.colorize("&8" + (i + 1) + ". &7" + announcements.get(i)));
        }
    }

    @BaseCommand.SubCommand(
        name = "announcement",
        isDefault = true,
        description = "Show announcement help"
    )
    public void handleDefault(CommandSender sender, String[] args) {
        sender.sendMessage(ColorUtil.colorize("&8&l» &7Announcement Commands:"));
        sender.sendMessage(ColorUtil.colorize("&8• &7/announcement list &8- &7List all announcements"));
        sender.sendMessage(ColorUtil.colorize("&8• &7/announcement add <message> &8- &7Add new announcement"));
        sender.sendMessage(ColorUtil.colorize("&8• &7/announcement remove <index> &8- &7Remove an announcement"));
        sender.sendMessage(ColorUtil.colorize("&8• &7/announcement set <seconds> &8- &7Set announcement interval"));
    }

    @BaseCommand.SubCommand(
        name = "add",
        description = "Add a new announcement",
        usage = "<message>",
        permission = "hywave.announcement.add",
        minArgs = 1
    )
    public void handleAdd(CommandSender sender, String[] args) {
        String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        manager.addAnnouncement(message);
        sender.sendMessage(ColorUtil.colorize("&a&lSUCCESS: &7Announcement added successfully."));
    }

    @BaseCommand.SubCommand(
        name = "remove",
        description = "Remove an announcement",
        usage = "<index>",
        permission = "hywave.announcement.remove",
        minArgs = 1,
        maxArgs = 1
    )
    public void handleRemove(CommandSender sender, String[] args) {
        try {
            int index = Integer.parseInt(args[1]) - 1;
            manager.removeAnnouncement(index);
            sender.sendMessage(ColorUtil.colorize("&a&lSUCCESS: &7Announcement removed successfully."));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            sender.sendMessage(ColorUtil.colorize("&c&lISSUE: &7Invalid announcement index."));
        }
    }

    @BaseCommand.SubCommand(
        name = "set",
        description = "Set announcement interval",
        usage = "<seconds>",
        permission = "hywave.announcement.set",
        minArgs = 1,
        maxArgs = 1
    )
    public void handleSetInterval(CommandSender sender, String[] args) {
        try {
            int interval = Integer.parseInt(args[1]);
            if (interval < 10) {
                sender.sendMessage(ColorUtil.colorize("&c&lISSUE: &7Interval must be at least 10 seconds."));
                return;
            }
            manager.setInterval(interval);
            sender.sendMessage(ColorUtil.colorize("&a&lSUCCESS: &7Announcement interval set to " + interval + " seconds."));
        } catch (NumberFormatException e) {
            sender.sendMessage(ColorUtil.colorize("&c&lISSUE: &7Invalid interval value."));
        }
    }
}