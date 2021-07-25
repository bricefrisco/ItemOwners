package com.bfrisco.itemowners;

import com.bfrisco.itemowners.constants.DependencyNames;
import com.bfrisco.itemowners.listeners.SafeTradeListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class DependencyManager implements Listener {
    private final Plugin plugin;

    public DependencyManager(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEnable(PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();
        loadPlugin(plugin.getName());
    }

    private void loadPlugin(String name) {
        if (name == null || name.isEmpty()) return;

        Listener listener = null;
        switch (name) {
            case DependencyNames.SAFE_TRADE:
                listener = new SafeTradeListener(plugin);
                break;
            default:
                break;
        }

        if (listener != null) {
            ItemOwners.registerEvents(listener);
        }
    }
}
