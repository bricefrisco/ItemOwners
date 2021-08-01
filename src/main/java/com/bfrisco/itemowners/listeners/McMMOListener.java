package com.bfrisco.itemowners.listeners;

import com.bfrisco.itemowners.ItemOwners;
import com.bfrisco.itemowners.constants.LogMessages;
import com.bfrisco.itemowners.database.ItemEventRepository;
import com.bfrisco.itemowners.database.ItemEventType;
import com.gmail.nossr50.events.skills.salvage.McMMOPlayerSalvageCheckEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class McMMOListener implements Listener {
    private final Plugin plugin;

    public McMMOListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMcMMOPlayerSalvageCheckEvent(McMMOPlayerSalvageCheckEvent event) {
        String itemId = ItemOwners.getItemId(event.getSalvageItem());
        if (itemId == null) return;
        ItemOwners.getBukkitLogger().info(String.format(LogMessages.SALVAGED, itemId, event.getPlayer().getName()));
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> ItemEventRepository.save(ItemEventType.SALVAGED, itemId, event.getPlayer()));
    }
}
