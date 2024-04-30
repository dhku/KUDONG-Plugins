package kr.kudong.framework;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.common.basic.db.DBAccess;
import kr.kudong.common.paper.config.ConfigLoader;
import kr.kudong.framework.command.FrameworkCommandManager;
import kr.kudong.framework.listener.FrameworkListener;
import kr.kudong.framework.reflection.ReflectionService;
import kr.kudong.framework.scoreboard.TownyScoreboard;
import net.milkbowl.vault.economy.Economy;

public class FrameworkCore extends JavaPlugin
{
	private static ReflectionService reflect;
	
	private Logger logger;
	private ConfigLoader configLoader;
	private FrameworkListener listener;
	private PluginManager pluginManager;
	private FrameworkCommandManager frameworkCommandManager;
	private DBAccess dbAccess;
	
	@Override
	public void onEnable()
	{
		FrameworkCore.plugin = this;
		FrameworkCore.reflect = new ReflectionService(this.logger, this);
		
		if(!this.setupEconomy()) return;
		if(!this.setupPlaceHolder()) return;
		
		/**
		 * 디펜던시 로드
		 */
		this.logger = this.getLogger();
		this.configLoader = new ConfigLoader(this, this.logger);
		this.dbAccess = new DBAccess(logger);
		this.frameworkCommandManager = new FrameworkCommandManager(this.logger, this);
		this.listener = new FrameworkListener(this.logger, this);
		
		this.registerConfig();
		this.registerEventListener();
		
		this.dbAccess.start();
		this.runScheduler();
		
		this.logger.log(Level.INFO, "Kudong-Framework 플러그인이 성공적으로 활성화 되었습니다!");
	}
	
	public DBAccess getDBAccess()
	{
		return this.dbAccess;
	}

	@Override
	public void onDisable()
	{
		this.dbAccess.stop();
		this.logger.log(Level.INFO, "Kudong-Framework 플러그인이 성공적으로 비활성화 되었습니다!");
	}
	
	private void registerEventListener()
	{
		this.pluginManager = Bukkit.getPluginManager();
		this.pluginManager.registerEvents(this.listener,this);
	}
	
	private void registerConfig()
	{
		this.configLoader.registerModule("database", this.dbAccess);
		this.configLoader.loadConfig();
	}
	
    private boolean setupPlaceHolder() 
    {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) 
        {
        	this.logger.log(Level.SEVERE, "PlaceholderAPI 디펜던시가 발견되지않았기에 플러그인이 비활성화 되었습니다!");
        	getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        return true;
    }
	
    private boolean setupEconomy() 
    {
        if (getServer().getPluginManager().getPlugin("Vault") == null) 
        {
        	this.logger.log(Level.SEVERE, "Vault 디펜던시가 발견되지않았기에 플러그인이 비활성화 되었습니다!");
        	getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) 
        {
        	this.logger.log(Level.SEVERE, "Economy관련 플러그인이 발견되지않았기에 플러그인이 비활성화 되었습니다!");
        	getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        
        econ = rsp.getProvider();
        
        if(econ == null)
        {
        	getServer().getPluginManager().disablePlugin(this);
        	return false;
        }

        return true;
    }
    
	private void runScheduler()
	{
		Bukkit.getScheduler().runTaskTimer(plugin, ()->{
			
			for(Player p : Bukkit.getOnlinePlayers())
			{
				TownyScoreboard.Update(p);
			}
			
		}, 0L, 20L);
		
	}
	
	public static JavaPlugin plugin;
	public static Economy econ;

}
