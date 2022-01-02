package com.bfrisco.itemowners.listeners;

import com.bfrisco.itemowners.ItemOwners;
import com.bfrisco.itemowners.constants.LogMessages;
import com.bfrisco.itemowners.database.ItemEventRepository;
import com.bfrisco.itemowners.database.ItemEventType;
//import de.oppermann.bastian.safetrade.events.TradeSuccessEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class SafeTradeListener implements Listener {
    private final Plugin plugin;

    public SafeTradeListener(Plugin plugin) {
        this.plugin = plugin;
    }

//    @EventHandler
//    public void onTradeSuccessEvent(TradeSuccessEvent event) {
//        for (ItemStack item : event.getItemsPlayer1To2()) {
//            String itemId = ItemOwners.getItemId(item);
//            if (itemId == null) continue;
//
//            ItemOwners.getBukkitLogger().info(String.format(LogMessages.TRADED, itemId, event.getPlayer1().getName(), event.getPlayer2().getName()));
//            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> ItemEventRepository.save(ItemEventType.TRADED, itemId, event.getPlayer2()));
//        }
//
//        for (ItemStack item : event.getItemsPlayer2To1()) {
//            String itemId = ItemOwners.getItemId(item);
//            if (itemId == null) continue;
//
//            ItemOwners.getBukkitLogger().info(String.format(LogMessages.TRADED, itemId, event.getPlayer2().getName(), event.getPlayer1().getName()));
//            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> ItemEventRepository.save(ItemEventType.TRADED, itemId, event.getPlayer1()));
//        }
//    }
}
