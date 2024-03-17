package kr.kudong.entity.controller;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import kr.kudong.entity.data.RidingPlayerMap;
import kr.kudong.entity.listener.EntityRidingListener;
import kr.kudong.entity.listener.KeyInputListener;

public class RidingManager
{
	private Logger logger;
	private JavaPlugin plugin;
	private EntityRidingListener listener;
	private RidingPlayerMap map;
	private RidingConfig config;
	private RidingScheduler scheduler;
	private KeyInputListener keyListener;
	private ProtocolManager protocolManager;
	
	public RidingManager(Logger logger,JavaPlugin plugin)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.config = new RidingConfig(this.logger);
		this.protocolManager = ProtocolLibrary.getProtocolManager();
		this.map = new RidingPlayerMap(this.logger,this.plugin,this);
		this.scheduler = new RidingScheduler(this.logger,this.plugin,this);
		this.listener = new EntityRidingListener(this.logger,this.plugin,this);
		this.keyListener = new KeyInputListener(this.logger,this.plugin,this);
		this.protocolManager.addPacketListener(this.keyListener);
	}

	public EntityRidingListener getListener()
	{
		return listener;
	}

	public RidingConfig getConfig()
	{
		return config;
	}

	public RidingPlayerMap getRidingPlayerMap()
	{
		return map;
	}

	public RidingScheduler getScheduler()
	{
		return scheduler;
	}

}
