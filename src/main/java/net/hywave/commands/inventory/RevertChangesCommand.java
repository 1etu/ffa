package net.hywave.commands.inventory;

import net.hywave.commands.BaseCommand;
import net.hywave.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import java.util.Map;

@BaseCommand.CommandInfo(
    name = "revertchanges",
    description = "Revert recent inventory changes",
    requiresPlayer = true
)
public class RevertChangesCommand {
    
    @BaseCommand.SubCommand(
        name = "revertchanges",
        isDefault = true,
        description = "Revert your recent inventory changes"
    )
    public void handleRevertChanges(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        
        if (!player.hasMetadata("inventory_changes")) {
            player.sendMessage(ColorUtil.colorize("&c&lISSUE: &7No recent inventory changes to revert."));
            return;
        }

        MetadataValue metadata = player.getMetadata("inventory_changes").get(0);
        Object[] changes = (Object[]) metadata.value();
        @SuppressWarnings("unchecked")
        Map<Integer, ItemStack> before = (Map<Integer, ItemStack>) changes[0];

        for (Map.Entry<Integer, ItemStack> entry : before.entrySet()) {
            int slot = entry.getKey();
            if (slot >= 0 && slot <= 35) {
                player.getInventory().setItem(slot, entry.getValue() != null ? entry.getValue().clone() : null);
            }
        }

        player.sendMessage(ColorUtil.colorize("&a&lSUCCESS: &7Your inventory has been reverted to its previous state."));
        player.removeMetadata("inventory_changes", Bukkit.getPluginManager().getPlugin("sdk"));
    }
}
