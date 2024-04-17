package kr.kudong.towny.dynmap;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapCommonAPI;

import kr.kudong.common.basic.db.DBAccess;
import kr.kudong.common.paper.config.ConfigLoader;
import kr.kudong.towny.dynmap.command.CommandManager;
import kr.kudong.towny.dynmap.controller.TownyDynmapManager;
import kr.kudong.towny.dynmap.listener.TownyDynmapListener;



public class TownyDynmapCore extends JavaPlugin
{
	private static JavaPlugin plugin;
	private Logger logger;
	private ConfigLoader configLoader;
	private PluginManager pluginManager;
	private TownyDynmapManager dynmapManager;
	private TownyDynmapListener listener;
	private CommandManager commandManager;
	private DynmapCommonAPI dynmapApi;
	private DBAccess dbAccess;
	
	@Override
	public void onEnable()
	{
		TownyDynmapCore.plugin = this;
		
		/**
		 * 디펜던시 로드
		 */
		if(!this.setupDynmap()) return;
		if(!this.setupTowny()) return;
		
		this.logger = this.getLogger();
		this.configLoader = new ConfigLoader(this, this.logger);
	
		/**
		 * 컨트롤러
		 */
		this.dynmapManager = new TownyDynmapManager(this.logger,this,this.dynmapApi);
		this.commandManager = new CommandManager(this.logger,this,this.dynmapManager);
		this.listener = new TownyDynmapListener(this.logger,this,this.dynmapManager);
		
		this.registerEventListener();
		
		this.dynmapManager.loadTownyChunks();
		this.logger.log(Level.INFO, "Kudong-Towny-Dynmap 플러그인이 성공적으로 활성화 되었습니다!");
		
	}

	@Override
	public void onDisable()
	{
		this.logger.log(Level.INFO, "Kudong-Towny-Dynmap 플러그인이 성공적으로 비활성화 되었습니다!");
	}
	
	private void registerEventListener()
	{
		this.pluginManager = Bukkit.getPluginManager();
		this.pluginManager.registerEvents(this.listener,this);
	}
	
	private boolean setupTowny()
	{
        if (getServer().getPluginManager().getPlugin(TOWNY_PLUGIN_NAME) == null) 
        {
        	this.logger.log(Level.SEVERE, TOWNY_PLUGIN_NAME + " 디펜던시가 발견되지않았기에 플러그인이 비활성화 되었습니다!");
        	getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        return true;
	}
	
	private boolean setupDynmap()
	{
		Plugin plugin = getServer().getPluginManager().getPlugin("dynmap");
        if (plugin == null) 
        {
        	this.logger.log(Level.SEVERE, "dynmap" + " 디펜던시가 발견되지않았기에 플러그인이 비활성화 되었습니다!");
        	getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        this.dynmapApi = (DynmapCommonAPI)plugin;
        return true;
	}
	
	private static final String TOWNY_PLUGIN_NAME = "HQTowny";
}
