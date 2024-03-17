package kr.kudong.entity.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.entity.controller.RidingManager;

public class RidingPlayerMap
{
	private Logger logger;
	private JavaPlugin plugin;
	private RidingManager manager;
	private Map<UUID,SteerableEntity> _entityMap;
	private Map<UUID,KeyInputState> _keyInput;
	private Map<UUID,Integer> _schedulerMap;
	
	public RidingPlayerMap(Logger logger,JavaPlugin plugin,RidingManager manager)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.manager = manager;
		this._entityMap = new HashMap<>();
		this._keyInput = new HashMap<>();
		this._schedulerMap = new HashMap<>();
	}
	
	public void registerRidingPlayerInput(UUID uuid)
	{
		this._keyInput.put(uuid, new KeyInputState());
	}
	
	public void removeRidingPlayerInput(UUID uuid)
	{
		this._keyInput.remove(uuid);
	}
	
	public KeyInputState getRidingPlayerInput(UUID uuid)
	{
		return this._keyInput.get(uuid);
	}
	
	public boolean containsPlayerInput(UUID uuid)
	{
		return this._keyInput.containsKey(uuid);
	}
	
	public void registerEntity(UUID uuid, SteerableEntity entity)
	{
		this._entityMap.put(uuid, entity);
	}
	
	public void removeEntity(UUID uuid)
	{
		this._entityMap.remove(uuid);
	}
	
	public SteerableEntity getEntity(UUID uuid)
	{
		return this._entityMap.get(uuid);
	}
	
	public boolean containsEntity(UUID uuid)
	{
		return this._entityMap.containsKey(uuid);
	}
	
	public void registerScheduler(UUID uuid,int TaskID)
	{
		this._schedulerMap.put(uuid,TaskID);
	}
	
	public void removeScheduler(UUID uuid)
	{
		this._schedulerMap.remove(uuid);
	}
	
	public int getScheduler(UUID uuid)
	{
		return this._schedulerMap.get(uuid);
	}
	
	public boolean containsScheduler(UUID uuid)
	{
		return this._schedulerMap.containsKey(uuid);
	}

}
