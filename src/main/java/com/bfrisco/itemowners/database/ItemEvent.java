package com.bfrisco.itemowners.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Date;

@DatabaseTable(tableName = "events")
public class ItemEvent {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(index = true, canBeNull = false)
    private String itemId;

    @DatabaseField(index = true, canBeNull = false)
    private Date date;

    @DatabaseField(canBeNull = false)
    private String itemEventType;

    @DatabaseField
    private String world;

    @DatabaseField
    private int x;
    @DatabaseField
    private int y;
    @DatabaseField
    private int z;

    @DatabaseField(index = true)
    private String playerId;

    public long getId() {
        return id;
    }

    public String getItemId() {
        return itemId;
    }

    public Date getDate() {
        return date;
    }

    public String getItemEventType() {
        return itemEventType;
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setItemEventType(String itemEventType) {
        this.itemEventType = itemEventType;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    @Override
    public String toString() {
        return "ItemEvent{" +
                "id=" + id +
                ", itemId='" + itemId + '\'' +
                ", date=" + date +
                ", itemEventType='" + itemEventType + '\'' +
                ", world='" + world + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", playerId='" + playerId + '\'' +
                '}';
    }
}
