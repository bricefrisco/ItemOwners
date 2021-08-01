package com.bfrisco.itemowners.database;

import com.bfrisco.itemowners.ItemOwners;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.Date;
import java.sql.SQLException;

public class DataRetentionPurger {
    private static final long DAY = 24 * 60 * 60 * 1000;
    private static final long HOUR = 60 * 60 * 1000;

    public static void schedule(Plugin plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            try {
                long retentionInDays = ItemOwners.getBukkitConfig().getLong("retention-period");
                Date date = new Date(System.currentTimeMillis() - (retentionInDays * DAY));
                long numRemoved = ItemEventRepository.deleteBefore(date);
                if (numRemoved > 0) {
                    ItemOwners.getBukkitLogger().info("Successfully purged " + numRemoved + " item events that " +
                            "occurred before the retention period (" + retentionInDays + " days)");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, HOUR, HOUR);
    }
}
