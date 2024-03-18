package kr.kudong.entity.listener;

import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import kr.kudong.entity.controller.RidingManager;
import kr.kudong.entity.data.RidingPlayerMap;
import kr.kudong.entity.data.SteerableEntity;
import kr.kudong.entity.data.SteerablePreset;

public class EntityRidingListener implements Listener
{
	private Logger logger;
	private RidingManager manager;
	private JavaPlugin plugin;
	private RidingPlayerMap map;
	
	public EntityRidingListener(Logger logger,JavaPlugin plugin,RidingManager manager)
	{
		this.logger = logger;
		this.manager = manager;
		this.plugin = plugin;
		this.map = manager.getRidingPlayerMap();
	}
	
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		this.removeSteerableEntity(player);
	}
	
	//탈것 장착
	@EventHandler
	private void entityPassenger(PlayerSwapHandItemsEvent event) 
	{
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		
		if (!player.isInsideVehicle() && !this.map.containsEntity(uuid)) 
		{
			SteerableEntity entity = this.createSteerableEntity(player,new SteerablePreset());
			this.map.registerEntity(uuid, entity);
			this.map.registerRidingPlayerInput(uuid);
			
			int TaskID = this.manager.getScheduler().startRidingScheduler(player);
			this.map.registerScheduler(uuid, TaskID);   
		}
		else if(player.isInsideVehicle() && this.map.containsEntity(uuid))
		{
			this.removeSteerableEntity(player);
		}
	}
	
	public SteerableEntity createSteerableEntity(Player player, SteerablePreset preset)
	{
		Location loc = player.getLocation();
		
		Entity entity = player.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);	
		ArmorStand armor = (ArmorStand)entity;
		armor.setSmall(true);
		armor.setVisible(false);
		entity.addPassenger(player);

		SteerableEntity e = new SteerableEntity(player,entity,preset);
		return e;
	}
	
	public void removeSteerableEntity(Player player)
	{
		UUID uuid = player.getUniqueId();
		
		//STOP AND REMOVE SCHEDULER
		if (map.containsScheduler(uuid)) {
			int taskId = this.map.getScheduler(uuid);
			Bukkit.getScheduler().cancelTask(taskId);
			this.map.removeScheduler(uuid);
		}
		
		//REMOVE
		if(this.map.containsEntity(uuid))
		{
			SteerableEntity e = this.map.getEntity(uuid);
			e.destroyEntity();
			e.clear();
			this.map.removeEntity(uuid);
			this.map.removeRidingPlayerInput(uuid);
		}

	}

}
