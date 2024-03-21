package kr.kudong.entity.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
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
	
	private Map<UUID,List<SteerablePreset>> _purchasedMap;//구매한 정보

	
	public RidingPlayerMap(Logger logger,JavaPlugin plugin,RidingManager manager)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.manager = manager;
		this._entityMap = new HashMap<>();
		this._keyInput = new HashMap<>();
		this._schedulerMap = new HashMap<>();
		this._purchasedMap = new HashMap<>();

	}
	
	public void removePurchasedData(UUID uuid)
	{
		if(this._purchasedMap.containsKey(uuid))
		{
			List<SteerablePreset> p = this._purchasedMap.get(uuid);
			p.clear();
			this._purchasedMap.remove(uuid);
		}
	}
	
	public void removePresetInPurchasedData(UUID uuid,SteerablePreset purchased)
	{
		int idx = this.getIndexExistPreset(uuid, purchased);
		
		if(idx != -1 && this._purchasedMap.containsKey(uuid))
		{
			List<SteerablePreset> p = this._purchasedMap.get(uuid);
			p.remove(idx);
		}
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
	
	public void AddPreset(UUID uuid, SteerablePreset purchased)
	{
		if(this._purchasedMap.containsKey(uuid))
		{
			List<SteerablePreset> p = this._purchasedMap.get(uuid);
			p.add(purchased);
		}
		else
		{
			List<SteerablePreset> p2 = new ArrayList<>();
			p2.add(purchased);
			this._purchasedMap.put(uuid, p2);
		}
	}
	
	public void setPlayerPurchasedList(UUID uuid, List<SteerablePreset> purchased)
	{
		this._purchasedMap.put(uuid, purchased);
	}
	
	public boolean isExistPreset(UUID uuid, SteerablePreset purchased)
	{
		if(this._purchasedMap.containsKey(uuid))
		{
			List<SteerablePreset> list = this._purchasedMap.get(uuid);
			
			for(SteerablePreset p : list)
			{
				if(p.getPRESET_NAME().equals(purchased.getPRESET_NAME()))
				{
					return true;
				}
			}
			
		}

		return false;
	}
	
	public int getIndexExistPreset(UUID uuid, SteerablePreset purchased)
	{
		int count = 0;
		if(this._purchasedMap.containsKey(uuid))
		{
			List<SteerablePreset> list = this._purchasedMap.get(uuid);
			
			for(SteerablePreset p : list)
			{
				if(p.getPRESET_NAME().equals(purchased.getPRESET_NAME()))
				{
					return count;
				}
				count++;
			}
			
		}

		return -1;
	}
	
	public List<SteerablePreset> getPurchasedList(UUID uuid)
	{
		return this._purchasedMap.get(uuid);
	}
	
	public Map<UUID, List<SteerablePreset>> get_purchasedMap()
	{
		return _purchasedMap;
	}
	
}
