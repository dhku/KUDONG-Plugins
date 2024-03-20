package kr.kudong.entity.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


import kr.kudong.entity.data.KeyInputState;
import kr.kudong.entity.data.RidingPlayerMap;
import kr.kudong.entity.data.SteerableEntity;

public class RidingScheduler
{
	private Logger logger;
	private JavaPlugin plugin;
	private RidingManager manager;
	private RidingPlayerMap map;
	
	public RidingScheduler(Logger logger,JavaPlugin plugin,RidingManager manager)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.manager = manager;
		this.map = this.manager.getRidingPlayerMap();
	}
	
	public int startRidingScheduler(Player player)
	{
		UUID uuid = player.getUniqueId();
		SteerableEntity steerableEntity = this.map.getEntity(uuid);
		KeyInputState state = this.map.getRidingPlayerInput(uuid);

		if(steerableEntity.isInit() == false && steerableEntity.isCasualMode() == false)
		{
			Vector v = steerableEntity.getEntity().getLocation().getDirection().clone();
			steerableEntity.setInertiaDirection(v);
			steerableEntity.setInit(true);
		}
		
		return new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if(player.isInsideVehicle())
				{
					steerableEntity.updatePhysics(player,state);
				}
			}
		}.runTaskTimer(plugin, 0L, 1L).getTaskId();
	}
	
	public void stopRidingScheduler(int taskID)
	{
		Bukkit.getScheduler().cancelTask(taskID);
	}
	

	
}
