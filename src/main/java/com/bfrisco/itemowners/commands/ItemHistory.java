package com.bfrisco.itemowners.commands;

import com.bfrisco.itemowners.ItemOwners;
import com.bfrisco.itemowners.constants.ItemOwnerPermissions;
import com.bfrisco.itemowners.database.Item;
import com.bfrisco.itemowners.database.ItemEventPage;
import com.bfrisco.itemowners.database.ItemEventRepository;
import com.bfrisco.itemowners.database.ItemRepository;
import com.bfrisco.itemowners.exceptions.ChatMessageGeneratorException;
import com.bfrisco.itemowners.util.ChatMessageGenerator;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.regex.Pattern;

public class ItemHistory implements CommandExecutor {
    private final Plugin plugin;

    private static final Pattern PATTERN = Pattern.compile("^[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}");

    public ItemHistory(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        if (args == null || args.length == 0 || args.length > 2) return false;

        Player player = (Player) sender;

        if (!player.hasPermission(ItemOwnerPermissions.VIEW_OWN_TOOL_EVENTS) && !player.hasPermission(ItemOwnerPermissions.VIEW_ALL_TOOL_EVENTS)) {
            player.sendMessage(ChatColor.RED + "Permission not granted.");
            return true;
        }

        String itemId = args[0];

        if (!PATTERN.matcher(itemId).matches()) {
            player.sendMessage(ChatColor.RED + "Invalid item ID.");
            return true;
        }

        int pageNum = 1;
        if (args.length == 2) {
            try {
                pageNum = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                return true;
            }
        }

        if (pageNum < 1) {
            player.sendMessage(ChatColor.RED + "Page must be at least 1.");
            return true;
        }

        int finalPageNum = pageNum;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Item item = ItemRepository.findById(itemId);
                if (item == null) {
                    player.sendMessage(ChatColor.RED + "That item ID was not found.");
                    return;
                }

                if (item.getOwnerId().equals(player.getUniqueId().toString()) && !player.hasPermission(ItemOwnerPermissions.VIEW_OWN_TOOL_EVENTS)) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to view your own item events.");
                    return;
                }

                if (!item.getOwnerId().equals(player.getUniqueId().toString()) && !player.hasPermission(ItemOwnerPermissions.VIEW_ALL_TOOL_EVENTS)) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to view other's tool events.");
                    return;
                }

                ItemEventPage page = ItemEventRepository.findByItemId(itemId, finalPageNum);
                TextComponent message = new TextComponent();

                try {
                    TextComponent header = ChatMessageGenerator.generateHeader(page, itemId, item.getData());
                    message.addExtra(header);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    player.sendMessage("An internal error occurred while executing that command.");
                    return;
                }

                try {
                    TextComponent body = ChatMessageGenerator.generate(page);
                    message.addExtra(body);
                } catch (ChatMessageGeneratorException e) {
                    message.addExtra(e.getMessage());
                }

                player.spigot().sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage("An internal error occurred while executing that command.");
            }
        });

        return true;
    }
}
