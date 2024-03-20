package kr.kudong.entity.controller;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import kr.kudong.entity.data.RidingPlayerMap;
import kr.kudong.entity.data.SteerablePreset;
import kr.kudong.entity.listener.EntityRidingListener;
import kr.kudong.entity.listener.GUIEventListener;
import kr.kudong.entity.listener.KeyInputListener;
import net.milkbowl.vault.economy.Economy;

public class RidingManager
{
	private Logger logger;
	private JavaPlugin plugin;
	private EntityRidingListener listener;
	private GUIEventListener guiListener;
	private RidingConfig config;
	private RidingScheduler scheduler;
	private KeyInputListener keyListener;
	private ProtocolManager protocolManager;
	private RidingPlayerMap map;
	private List<SteerablePreset> presetList;
	private Map<String,SteerablePreset> presetMap;
	private Economy econ;
	
	public RidingManager(Logger logger,JavaPlugin plugin,Economy econ)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.econ = econ;
		this.config = new RidingConfig(this.logger,this);
		this.protocolManager = ProtocolLibrary.getProtocolManager();
		
		this.map = new RidingPlayerMap(this.logger,this.plugin,this);
		this.scheduler = new RidingScheduler(this.logger,this.plugin,this);
		this.listener = new EntityRidingListener(this.logger,this.plugin,this);
		this.guiListener = new GUIEventListener();
		this.keyListener = new KeyInputListener(this.logger,this.plugin,this);
		this.protocolManager.addPacketListener(this.keyListener);
	}
	
	

	public Map<String, SteerablePreset> getPresetMap()
	{
		return presetMap;
	}

	public void setPresetMap(Map<String, SteerablePreset> presetMap)
	{
		this.presetMap = presetMap;
	}
	
	public Logger getLogger()
	{
		return logger;
	}

	public Economy getEcon()
	{
		return econ;
	}

	public GUIEventListener getGuiListener()
	{
		return guiListener;
	}

	public List<SteerablePreset> getPresetList()
	{
		return presetList;
	}

	public void setPresetList(List<SteerablePreset> presetList)
	{
		this.presetList = presetList;
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
