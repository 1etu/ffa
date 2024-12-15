package net.hywave.commands.stats;

import net.hywave.commands.BaseCommand;
import net.hywave.profile.PlayerProfile;
import net.hywave.profile.ProfileManager;
import net.hywave.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@BaseCommand.CommandInfo(
        name = "stats",
        description = "View player statistics",
        requiresPlayer = true
)
public class StatsCommand {
    private static final DecimalFormat df = new DecimalFormat("#.##");

    @BaseCommand.SubCommand(
            name = "stats",
            isDefault = true,
            maxArgs = 1,
            useCustomTabComplete = true
    )
    public void executeStats(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Player target = player;

        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(ColorUtil.colorize("&cPlayer not found!"));
                return;
            }
        }

        PlayerProfile profile = ProfileManager.getInstance().getProfile(target.getUniqueId());
        if (profile == null) {
            player.sendMessage(ColorUtil.colorize("&cProfile not found!"));
            return;
        }

        double progress = profile.getLevel().getProgressToNextLevel();
        int barLength = 20;
        int filledBars = (int) (progress * barLength);
        StringBuilder progressBar = new StringBuilder();
        for (int i = 0; i < barLength; i++) {
            if (i < filledBars) {
                progressBar.append("&a▮");
            } else {
                progressBar.append("&7▮");
            }
        }

        long currentXP = profile.getLevel().getExperienceInCurrentLevel();
        long neededXP = profile.getLevel().getExperienceForNextLevel() - profile.getLevel().calculateRequiredXP(profile.getCurrentLevel());

        player.sendMessage(ColorUtil.colorize("&8&m                                                    "));
        player.sendMessage(ColorUtil.colorize("&e" + target.getName() + "'s Stats"));
        player.sendMessage(ColorUtil.colorize("&7Role: " + profile.getRole().getColor() + profile.getRole().getDisplayName()));
        player.sendMessage(ColorUtil.colorize("&7Level: &f" + profile.getCurrentLevel()));
        player.sendMessage(ColorUtil.colorize("&7Progress: " + progressBar.toString() + " &8(&e" + String.format("%.1f", progress * 100) + "%&8)"));
        player.sendMessage(ColorUtil.colorize("&7XP: &f" + currentXP + "&7/&f" + neededXP));
        player.sendMessage(ColorUtil.colorize("&7Total XP: &f" + profile.getLevel().getExperience()));
        player.sendMessage(ColorUtil.colorize("\n"));
        player.sendMessage(ColorUtil.colorize("&7Kills: &a" + profile.getStats().getKills()));
        player.sendMessage(ColorUtil.colorize("&7Deaths: &c" + profile.getStats().getDeaths()));
        player.sendMessage(ColorUtil.colorize("&7K/D: &f" + df.format(profile.getStats().getKDRatio())));
        player.sendMessage(ColorUtil.colorize("&7Best Killstreak: &e" + profile.getStats().getBestKillStreak()));
        player.sendMessage(ColorUtil.colorize("&7Current Killstreak: &e" + profile.getStats().getCurrentKillStreak()));
        player.sendMessage(ColorUtil.colorize("&8&m                                                    "));
    }

    public List<String> handleStatsTabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            for (Player player : sender.getServer().getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(partial)) {
                    completions.add(player.getName());
                }
            }
        }

        return completions;
    }
}
