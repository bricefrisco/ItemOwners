package com.bfrisco.itemowners.commands;
import com.bfrisco.itemowners.ItemOwners;
import com.bfrisco.itemowners.constants.ItemOwnerPermissions;
import com.bfrisco.itemowners.database.ItemRepository;
import com.bfrisco.itemowners.util.ItemIDGenerator;
import com.bfrisco.itemowners.util.ItemSerialization;
import com.bfrisco.itemowners.util.RateLimiter;
import org.apache.commons.lang.WordUtils;
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
import java.util.*;

public class Own implements CommandExecutor {
    private static final String OWNER_FORMAT = "Owner: %s";
    private static final String ITEM_ID_FORMAT = "Item ID: %s";
    private final RateLimiter limiter = new RateLimiter(60 * 1000);
    private final Plugin plugin;

    public Own(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if (!player.hasPermission(ItemOwnerPermissions.OWN)) {
            player.sendMessage(ChatColor.RED + "You do not have permission to own items.");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "Please hold the item you would like to own.");
            return true;
        }

        if (ItemOwners.isNotValid(item)) {
            player.sendMessage(ChatColor.RED + "That item cannot be owned.");
            return true;
        }

        if (ItemOwners.isOwned(item)) {
            player.sendMessage(ChatColor.RED + "That item already has an owner.");
            return true;
        }

        if (ItemOwners.getBukkitConfig().getStringList("disabled-worlds").contains(player.getWorld().getName())) {
            player.sendMessage(ChatColor.RED + "You cannot own items in this world " + player.getWorld().getName() + ".");
            return true;
        }

        if (limiter.isLimited(player.getUniqueId())) {
           long secondsLeft = limiter.secondsLeft(player.getUniqueId());
           player.sendMessage(ChatColor.RED + "Please wait " + secondsLeft + " seconds and try again.");
           return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String itemId = generateItemId();

                // Give display name to items which do not already have one
                ItemMeta meta = item.getItemMeta();
                if (!meta.hasDisplayName()) {
                    String name = item.getType().toString()
                            .toLowerCase(Locale.ROOT)
                            .replace("_", " ");
                    meta.setDisplayName(WordUtils.capitalize(name));
                    item.setItemMeta(meta);
                }

                // Serialize and store the item
                String data = ItemSerialization.toBase64(item);
                ItemRepository.save(itemId, player.getUniqueId().toString(), data);

                // Add lore to item
                List<String> lore = addItemLore(item.getItemMeta().getLore(), itemId, player.getName());
                meta.setLore(lore);
                item.setItemMeta(meta);

                ItemOwners.getBukkitLogger().info("Player " + player.getName() + " has owned an item with generated ID: " + itemId);
                player.sendMessage(ChatColor.GREEN + "Successfully owned item with generated ID: " + itemId + ". Please take a screenshot of your " +
                        "owned tool with F2, keep the ID for your records");
            } catch (Exception e) {
                ItemOwners.getBukkitLogger().warning("Error occurred while generating item ID: " + e.getMessage());
                player.sendMessage(ChatColor.RED + "Unexpected error occurred while generating and storing item ID.");
            }
        });

        return true;
    }

    private String generateItemId() throws SQLException {
        String itemId;

        do {
            itemId = ItemIDGenerator.generate();
        } while (ItemRepository.exists(itemId));

        return itemId;
    }

    private List<String> addItemLore(List<String> lore, String id, String playerName) {
        if (lore == null) lore = new ArrayList<>();
        lore.add(ChatColor.RED + String.format(OWNER_FORMAT, playerName));
        lore.add(ChatColor.RED + String.format(ITEM_ID_FORMAT, id));
        return lore;
    }
}
