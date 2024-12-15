package net.hywave.commands.ping;

import net.hywave.commands.BaseCommand;
import net.hywave.utils.PingUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import net.hywave.utils.ColorUtil;

@BaseCommand.CommandInfo(
        name = "ping",
        description = "Check your and other players' ping",
        aliases = {"latency", "lag"},
        requiresPlayer = true
)
public class PingCommand {

    @BaseCommand.SubCommand(
            name = "ping",
            isDefault = true,
            maxArgs = 2,
            useCustomTabComplete = true
    )
    public void handlePing(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            int ping = new PingUtil().getPlayerPing(player);
            String quality = new PingUtil().getPingQuality(ping);
            String bar = new PingUtil().generatePingBar(ping);
            sender.sendMessage(ColorUtil.colorize("&7Your ping is" + " &f" + ping + "ms&7. &8(" + bar + " - " + quality + "&8)"));
            return;
        }

        Player target = player.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ColorUtil.colorize("&&lISSUE: &cThe issued player (" + args[0] + ") is not online."));
            return;
        }

        int targetPing = new PingUtil().getPlayerPing(target);
        String quality = new PingUtil().getPingQuality(targetPing);
        String bar = new PingUtil().generatePingBar(targetPing);

        TextComponent pingInfo = new TextComponent(ColorUtil.colorize("&7" +target.getName() + "'s ping is" + " &f" + targetPing + "ms&7. &8(" + bar + " - " + quality + "&8)"));
        pingInfo.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ColorUtil.colorize("&eClick to compare")).create()));
        pingInfo.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ping " + player.getName() + " " + target.getName()));

        player.spigot().sendMessage(pingInfo);

        if (args.length == 2) {
            int playerPing = new PingUtil().getPlayerPing(player);
            int pingDifference = Math.abs(playerPing - targetPing);
            player.sendMessage(ColorUtil.colorize(String.format("&7The difference between your and %s's ping is &f%dms", target.getName(), pingDifference)));
        }
    }

    public List<String> handlePingTabComplete(CommandSender sender, String[] args) {
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