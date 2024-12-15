package net.hywave.commands.inventory;

import net.hywave.commands.BaseCommand;
import net.hywave.inventory.gui.InventoryChangeGUI;
import net.hywave.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import java.util.Map;

@BaseCommand.CommandInfo(
    name = "viewchanges",
    description = "View inventory layout changes",
    requiresPlayer = true
)
public class ViewChangesCommand {
    
    @BaseCommand.SubCommand(
        name = "viewchanges",
        isDefault = true,
        description = "View your inventory layout changes"
    )
    public void handleViewChanges(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        
        if (!player.hasMetadata("inventory_changes")) {
            player.sendMessage(ColorUtil.colorize("&c&lISSUE: &7No recent inventory changes to display."));
            return;
        }

        MetadataValue metadata = player.getMetadata("inventory_changes").get(0);
        Object[] changes = (Object[]) metadata.value();
        @SuppressWarnings("unchecked")
        Map<Integer, ItemStack> before = (Map<Integer, ItemStack>) changes[0];
        @SuppressWarnings("unchecked")
        Map<Integer, ItemStack> after = (Map<Integer, ItemStack>) changes[1];

        InventoryChangeGUI.showChanges(player, before, after);
        player.removeMetadata("inventory_changes", Bukkit.getPluginManager().getPlugin("sdk"));
    }
}
