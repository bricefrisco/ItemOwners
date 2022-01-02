# ItemOwners - /own items and view their history

ItemOwners is a plugin that allows players to own items and view an items history.
- Every event on the item, such as storing the item in a chest, destroying, breaking, and everything between is tracked and stored for a configurable duration of time.
- Players can (configurable by permissions) view their own items to track down what has happened to it, such as if they had given the item to another player and it was lost.
- Moderators or admins can (configurable by permissions) view all items and their events.

## Images
#### An owned item
![Owned Item](https://i.imgur.com/jRLZ4kY.jpg)

#### ItemHistory
![ItemHistory](https://i.imgur.com/GgPlfFi.jpg)

Hovering over **[loc]** will show you the location, and hovering over **[pl]** will show you the player that had the item.

#### ItemsOwned
![ItemsOwned](https://i.imgur.com/H0Q70VM.jpg)

Hovering over the Item ID will show the item name, enchantments, and metadata.



## Supported Versions
- Minecraft **1.16.x or newer**: ItemOwners **1.0.1**

## Commands
- ***/own***
  
  **Description:** *Own an item that is in your hand*
  
  **Permissions:** 
    - *ItemOwners.own - Own items*


- ***/disown***
  
  **Description:** *Disowns an item that is in your hand*
  
  **Permissions:**
    - *ItemOwners.disown.own - Disown your own items*
    - *ItemOwners.disown.all - Disown **any** item*


- ***/itemhistory <item id> (page)***
  
  **Description:** *View an items history in reverse-chronological order.*
  
  **Permissions:**
    - *ItemOwners.view.own - View history for your own items*
    - *ItemOwners.view.all - View history for **any** item*


- ***/itemsowned <player name> (page)***
  
  **Description:** View the items owned by a specified player.
  
  **Permissions:**
    - *ItemOwners.list.own - View the items you own*
    - *ItemOwners.list.any - View the items **any** player owns*