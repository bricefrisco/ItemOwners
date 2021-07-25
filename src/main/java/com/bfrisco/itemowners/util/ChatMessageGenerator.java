package com.bfrisco.itemowners.util;

import com.bfrisco.itemowners.database.Item;
import com.bfrisco.itemowners.database.ItemEvent;
import com.bfrisco.itemowners.database.ItemEventPage;
import com.bfrisco.itemowners.database.ItemPage;
import com.bfrisco.itemowners.exceptions.ChatMessageGeneratorException;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

public final class ChatMessageGenerator {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("[M/d/yy hh:mm a]");

    public static TextComponent generateHeader(ItemEventPage page, String itemId, String itemData) throws IOException {
        TextComponent message = new net.md_5.bungee.api.chat.TextComponent(ChatColor.YELLOW + " ---- " + ChatColor.GOLD + "ItemHistory"
                + ChatColor.YELLOW + " -- " + ChatColor.GOLD + "Page " + ChatColor.RED + page.getCurrentPage() +
                ChatColor.GOLD + "/" + ChatColor.RED + page.getTotalPages() + ChatColor.YELLOW + " ----\n");

        message.addExtra(ChatColor.GOLD + "Item ID: ");

        TextComponent id = new TextComponent(ChatColor.GOLD + "[" + ChatColor.RED + itemId + ChatColor.GOLD + "]\n");
        id.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, generateTooltip(itemData)));
        message.addExtra(id);

        return message;
    }

    public static TextComponent generateHeader(ItemPage page, String playerName) {
        TextComponent message = new TextComponent(ChatColor.YELLOW + " ---- " + ChatColor.GOLD + "ItemsOwned"
                + ChatColor.YELLOW + " -- " + ChatColor.GOLD + "Page " + ChatColor.RED + page.getCurrentPage() +
                ChatColor.GOLD + "/" + ChatColor.RED + page.getTotalPages() + ChatColor.YELLOW + " ----\n");

        message.addExtra(ChatColor.GOLD + "Player: " + ChatColor.RED + playerName + "\n");

        return message;
    }

    public static TextComponent generate(ItemPage page) throws ChatMessageGeneratorException, IOException {
        TextComponent message = new TextComponent();

        if (page.getTotalPages() == 0) {
            throw new ChatMessageGeneratorException(ChatColor.RED + "No items owned by that player.");
        }

        if (page.getCurrentPage() > page.getTotalPages()) {
            throw new ChatMessageGeneratorException("Unknown chapter.");
        }

        int count = 1;
        for (Item item : page.getResult()) {
            message.addExtra(ChatColor.GRAY + DATE_FORMAT.format(item.getDate()) + ": ");

            TextComponent itemIdPart = new TextComponent(ChatColor.WHITE + "[" + ChatColor.YELLOW + item.getId() + ChatColor.WHITE + "]");
            itemIdPart.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, generateTooltip(item.getData())));
            message.addExtra(itemIdPart);

            if (count != page.getResult().size()) {
                message.addExtra("\n");
            }

            count++;
        }

        return message;
    }

    public static TextComponent generate(ItemEventPage page) throws ChatMessageGeneratorException {
        TextComponent message = new TextComponent();

        if (page.getTotalPages() == 0) {
            throw new ChatMessageGeneratorException(ChatColor.RED + "No history found for that item ID.");
        }

        if (page.getCurrentPage() > page.getTotalPages()) {
            throw new ChatMessageGeneratorException(ChatColor.RED + "Unknown chapter.");
        }

        int count = 1;
        for (ItemEvent event : page.getResult()) {
            message.addExtra(ChatColor.GRAY + DATE_FORMAT.format(event.getDate()) + ": ");
            message.addExtra(ChatColor.WHITE + event.getItemEventType() + " ");

            StringBuilder locationText = new StringBuilder();
            if (event.getWorld() != null) {
                locationText.append(event.getWorld()).append(", ");
            }

            locationText.append(event.getX()).append(", ").append(event.getY()).append(", ").append(event.getZ());

            TextComponent loc = new TextComponent(ChatColor.GOLD + "[loc]");
            loc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(locationText.toString())));
            message.addExtra(loc);

            if (event.getPlayerId() != null) {
                TextComponent pl = new TextComponent(" " + ChatColor.GOLD + "[pl]");
                pl.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Bukkit.getOfflinePlayer(UUID.fromString(event.getPlayerId())).getName())));
                message.addExtra(pl);
            }

            if (count != page.getResult().size()) {
                message.addExtra("\n");
            }

            count++;
        }

        return message;
    }

    private static BaseComponent[] generateTooltip(String data) throws IOException {
        TextComponent text = new TextComponent();

        ItemStack item = ItemSerialization.fromBase64(data);

        String name = item.getType().toString()
                .toLowerCase(Locale.ROOT)
                .replace("_", " ");

        text.addExtra(ChatColor.AQUA + WordUtils.capitalize(name));

        if (item.getItemMeta() != null) {
            text.addExtra("\n");

            item.getItemMeta().getEnchants().keySet().forEach(enchant -> {
                text.addExtra(ChatColor.GRAY + WordUtils.capitalize(enchant.getKey().getKey()) + " " +
                        RomanNumber.toRoman(item.getItemMeta().getEnchants().get(enchant)) +
                        "\n");
            });

            if (item.getItemMeta().getLore() != null) {
                text.addExtra(String.join("\n", item.getItemMeta().getLore()));
            }
        }

        return new ComponentBuilder(text).create();
    }
}
