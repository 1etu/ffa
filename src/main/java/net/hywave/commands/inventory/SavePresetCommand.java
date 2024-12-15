package net.hywave.commands.inventory;

import net.hywave.commands.BaseCommand;
import net.hywave.inventory.InventoryManager;
import net.hywave.utils.ColorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@BaseCommand.CommandInfo(
    name = "savepreset",
    description = "Save current inventory as preset",
    requiresPlayer = true,
    permission = "hywave.inventory.savepreset"
)
public class SavePresetCommand {
    
    @BaseCommand.SubCommand(
        name = "savepreset",
        isDefault = true,
        description = "Save your current inventory layout as the default preset"
    )
    public void handleSavePreset(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        InventoryManager.getInstance().saveCurrentInventory(player);
    }
}
