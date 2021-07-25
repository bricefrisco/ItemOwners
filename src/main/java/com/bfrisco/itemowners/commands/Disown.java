package com.bfrisco.itemowners.commands;

import com.bfrisco.itemowners.ItemOwners;
import com.bfrisco.itemowners.constants.ItemOwnerPermissions;
import com.bfrisco.itemowners.database.Item;
import com.bfrisco.itemowners.database.ItemEventRepository;
import com.bfrisco.itemowners.database.ItemRepository;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class Disown implements CommandExecutor {
    private final Plugin plugin;

    public Disown(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if (!player.hasPermission(ItemOwnerPermissions.DISOWN)) {
            player.sendMessage(ChatColor.RED + "You do not have permission to disown items.");
            return true;
        }

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "Please hold the item you would like to own.");
            return true;
        }

        String itemId = ItemOwners.getItemId(mainHand);
        if (itemId == null) {
            player.sendMessage(ChatColor.RED + "That item is not owned.");
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Item item = ItemRepository.findById(itemId);
                if (!item.getOwnerId().equals(player.getUniqueId().toString()) && !player.hasPermission(ItemOwnerPermissions.DISOWN_ALL_TOOLS)) {
                    player.sendMessage(ChatColor.RED + "You do not own this item.");
                    return;
                }

                List<String> loreAfter = mainHand.getItemMeta().getLore().stream()
                        .filter(lore -> !lore.startsWith(ChatColor.RED + "Item ID: ") && !lore.startsWith(ChatColor.RED + "Owner: "))
                        .collect(Collectors.toList());

                ItemMeta meta = mainHand.getItemMeta();
                meta.setLore(loreAfter);
                mainHand.setItemMeta(meta);
                player.sendMessage("Successfully disowned item.");

                ItemOwners.getBukkitLogger().info("Player " + player.getName() + " successfully disowned item " + itemId);

                ItemRepository.delete(itemId);
                ItemEventRepository.delete(itemId);
            } catch (SQLException se) {
                player.sendMessage("An internal error occurred while executing this command.");
            }
        });

        return true;
    }
}
