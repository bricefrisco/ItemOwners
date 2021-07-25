package com.bfrisco.itemowners.commands;

import com.bfrisco.itemowners.constants.ItemOwnerPermissions;
import com.bfrisco.itemowners.database.ItemPage;
import com.bfrisco.itemowners.database.ItemRepository;
import com.bfrisco.itemowners.exceptions.ChatMessageGeneratorException;
import com.bfrisco.itemowners.util.ChatMessageGenerator;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

public class ItemsOwned implements CommandExecutor {
    private final Plugin plugin;

    public ItemsOwned(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        if (args == null || args.length == 0 || args.length > 2) return false;

        Player player = (Player) sender;

        if (!player.hasPermission(ItemOwnerPermissions.LIST_OWN_TOOLS) && !player.hasPermission(ItemOwnerPermissions.LIST_ALL_TOOLS)) {
            player.sendMessage(ChatColor.RED + "Permission not granted.");
            return true;
        }

        final String playerName = args[0];

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
                if (!op.hasPlayedBefore()) {
                    player.sendMessage(ChatColor.RED + "Player not found.");
                    return;
                }

                String playerId = op.getUniqueId().toString();

                if (!playerId.equals(player.getUniqueId().toString()) && !player.hasPermission(ItemOwnerPermissions.LIST_ALL_TOOLS)) {
                    player.sendMessage(ChatColor.RED + "Permission not granted.");
                    return;
                }

                int pageNum = 1;
                if (args.length == 2) {
                    try {
                        pageNum = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Page must be a number.");
                        return;
                    }
                }

                if (pageNum < 1) {
                    player.sendMessage(ChatColor.RED + "Page must be at least 1.");
                    return;
                }

                ItemPage page = ItemRepository.findByPlayerId(playerId, pageNum);

                TextComponent message = new TextComponent();
                message.addExtra(ChatMessageGenerator.generateHeader(page, playerName));

                try {
                    TextComponent body = ChatMessageGenerator.generate(page);
                    message.addExtra(body);
                } catch (ChatMessageGeneratorException cme) {
                    message.addExtra(cme.getMessage());
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    player.sendMessage("An internal error occurred while executing that command.");
                    return;
                }

                player.spigot().sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return true;
    }
}
