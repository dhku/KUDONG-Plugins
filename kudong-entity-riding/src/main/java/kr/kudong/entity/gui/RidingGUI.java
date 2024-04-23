package kr.kudong.entity.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import kr.kudong.entity.RidingCore;
import kr.kudong.entity.controller.RidingManager;

public abstract class RidingGUI
{
	
	protected String name;
    protected Inventory inv;
    protected UUID uuid;
    protected JavaPlugin plugin;
	protected RidingManager manager;
	
    public RidingGUI(String name,Player player,int size) {
        this.name = name;
        this.uuid = player.getUniqueId();
        this.inv = Bukkit.createInventory(null, size, name);
        this.plugin = RidingCore.GetPlugin();
        this.manager = RidingCore.GetManager();
        this.init();
    }
    
    abstract void init();
    
    protected void setItem(String title, List<String> description, Material itemType, short data, int amount, int slot) {
        ItemStack item = new ItemStack(itemType);
        item.setAmount(amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(title);
        if(description!=null)
            meta.setLore(description);
        item.setItemMeta(meta);
        item.setDurability(data);
        inv.setItem(slot, item);
    }
    
    protected void setItem(String title, List<String> description, ItemStack item, short data, int amount, int slot) {
        item.setAmount(amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(title);
        if(description!=null)
            meta.setLore(description);
        item.setItemMeta(meta);
        item.setDurability(data);
        inv.setItem(slot, item);
    }

    protected void setHead(String title, List<String> description, String owner, int amount, int slot) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        item.setDurability((short) 3);
        item.setAmount(amount);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        new BukkitRunnable() {
            @Override
            public void run() {
                if(owner == null) return;
                meta.setOwner(owner);
                item.setItemMeta(meta);
                inv.setItem(slot,item);
            }
        }.runTask(this.plugin);
        meta.setDisplayName(title);
        meta.setLore(description);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    protected void setHead(String title, List<String> description, OfflinePlayer op, int amount, int slot) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        item.setDurability((short) 3);
        item.setAmount(amount);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        new BukkitRunnable() {
            @Override
            public void run() {
                meta.setOwningPlayer(op);
                item.setItemMeta(meta);
                inv.setItem(slot,item);
            }
        }.runTask(this.plugin);
        meta.setDisplayName(title);
        meta.setLore(description);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    protected void setItem(String title, List<String> description, Material itemType, short data, int amount, int slot,boolean glowing) {
        ItemStack item = new ItemStack(itemType);
        item.setAmount(amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(title);
        if(description!=null)
            meta.setLore(description);
        if(glowing) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        if(glowing) {
            item.addUnsafeEnchantment(Enchantment.LURE,1);
        }
        item.setDurability(data);
        inv.setItem(slot, item);
    }


    public void refresh() {
        init();
    }

    public void closeGUI() {
        guiMap.remove(uuid);
    }

    public static RidingGUI getGUI(Player p){
        UUID uuid = p.getUniqueId();
        return guiMap.getOrDefault(uuid, null);
    }

    public void openGUI() {
        RidingGUI ridingGUI = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                getPlayer().openInventory(inv);
                guiMap.put(uuid,ridingGUI);
            }
        }.runTaskLater(this.plugin,1L);
    }
    
    protected Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
    
    public abstract void clickEvent(InventoryClickEvent e);
    public abstract void dragEvent(InventoryDragEvent e);
    public abstract void closeEvent(InventoryCloseEvent e);
    
	private static Map<UUID,RidingGUI> guiMap = new HashMap<>();
}
