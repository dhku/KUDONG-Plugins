package kr.kudong.framework.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.common.basic.db.DBAccess;
import kr.kudong.framework.db.FrameworkDBService;
import kr.kudong.framework.db.FrameworkPlayer;
import kr.kudong.framework.listener.FrameworkListener;

public class FrameworkManager
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private final FrameworkDBService service;
	private final FrameworkListener listener;
	private FrameworkConfig config;
	public final Map<UUID,FrameworkPlayer> map;
	
	public FrameworkManager(Logger logger, JavaPlugin plugin, DBAccess dbAccess)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.config = new FrameworkConfig(logger,this);
		this.service = new FrameworkDBService(this.logger,this.plugin,dbAccess);
		this.listener = new FrameworkListener(this.logger,this.plugin,this);
		this.map = new HashMap<>();
	}
	
	public FrameworkConfig getConfig()
	{
		return config;
	}
	
	public UUID findPlayer(String name)
	{
		for(FrameworkPlayer np : this.map.values())
		{
			if(np.getUsername().equals(name)) return np.getUuid();
		}
		UUID uuid = this.service.getUsernameInPlayerData(name);
		if(uuid != null)
			this.map.put(uuid, new FrameworkPlayer(uuid,name));
		return uuid;
	}

	public FrameworkDBService getService()
	{
		return service;
	}

	public FrameworkListener getListener()
	{
		return listener;
	}
}
