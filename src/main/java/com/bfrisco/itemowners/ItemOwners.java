package com.bfrisco.itemowners;
import com.bfrisco.itemowners.commands.Own;
import com.bfrisco.itemowners.commands.Disown;
import com.bfrisco.itemowners.commands.ItemHistory;
import com.bfrisco.itemowners.commands.ItemsOwned;
import com.bfrisco.itemowners.database.ItemEventRepository;
import com.bfrisco.itemowners.database.ItemRepository;
import com.bfrisco.itemowners.listeners.OwnedItemListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemOwners extends JavaPlugin {
    private static ItemOwners plugin;
    private static Logger logger;
    private static FileConfiguration config;
    private static File dataFolder;
    private static final List<Material> VALID_ITEMS = new ArrayList<>();

    public ItemOwners() throws SQLException {
        plugin = this;
        logger = getLogger();
        config = getConfig();
        dataFolder = getDataFolder();

        for (String item : getConfig().getStringList("items")) {
            try {
                VALID_ITEMS.add(Material.valueOf(item));
            } catch (Exception e) {
                ItemOwners.getBukkitLogger().log(Level.WARNING, String.format("Ignoring item '%s' - not a valid material name!", item));
            }
        }

        ItemRepository.init(loadFile("items.db"));
        ItemEventRepository.init(loadFile("events.db"));
    }


    @Override
    public void onEnable() {
        getCommand("own").setExecutor(new Own(this));
        getCommand("disown").setExecutor(new Disown(this));
        getCommand("itemhistory").setExecutor(new ItemHistory(this));
        getCommand("itemsowned").setExecutor(new ItemsOwned(this));

        this.getConfig().options().copyDefaults();
        saveDefaultConfig();

        registerEvents(new DependencyManager(this));
        registerEvents(new OwnedItemListener(this));
    }

    public static void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    public static boolean isNotValid(ItemStack item) {
        if (item == null) return true;
        if (item.getAmount() != 1) return true;
        if (item.getItemMeta() == null) return true;
        return !VALID_ITEMS.contains(item.getType());
    }

    public static String getItemId(ItemStack item) {
        if (item == null || item.getItemMeta() == null) return null;

        List<String> lore = item.getItemMeta().getLore();
        if (lore == null || lore.size() == 0) return null;

        for (String line : lore) {
            if (line != null && line.startsWith(ChatColor.RED + "Item ID: ")) return line.split(": ")[1];
        }

        return null;
    }

    public static String getItemId(ItemStack item1, ItemStack item2) {
        String itemId = getItemId(item1);
        if (itemId != null) return itemId;
        return getItemId(item2);
    }

    public static boolean isOwned(ItemStack item) {
        return getItemId(item) != null;
    }

    private static File loadFile(String string) {
        File file = new File(dataFolder, string);
        return loadFile(file);
    }

    private static File loadFile(File file) {
        if (!file.exists()) {
            try {
                if (file.getParent() != null) {
                    file.getParentFile().mkdirs();
                }

                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    public static Logger getBukkitLogger() {
        return logger;
    }

    public static FileConfiguration getBukkitConfig() {
        return config;
    }
}
