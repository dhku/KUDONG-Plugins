package kr.kudong.entity;

import java.text.DecimalFormat;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.common.basic.db.DBAccess;
import kr.kudong.common.paper.config.ConfigLoader;
import kr.kudong.entity.command.CommandManager;
import kr.kudong.entity.controller.RidingManager;
import kr.kudong.entity.db.RidingService;
import kr.kudong.entity.db.SQLSchema;
import kr.kudong.framework.FrameworkCore;
import net.milkbowl.vault.economy.Economy;


public class RidingCore extends JavaPlugin
{
	private Logger logger;
	private ConfigLoader configLoader;
	private PluginManager pluginManager;
	private CommandManager commandManager;
	private Economy econ;
	private DBAccess dbAccess;
	
	@Override
	public void onEnable()
	{
		RidingCore.plugin = this;
		
		/**
		 * 디펜던시 로드
		 */
		if(!this.setupFramework()) return;
		if(!this.setupEconomy()) return;

		this.logger = this.getLogger();
		this.configLoader = new ConfigLoader(this, this.logger);
	
		/**
		 * 컨트롤러
		 */
		RidingCore.ridingManager = new RidingManager(this.logger,this,this.econ);
		RidingCore.dbService = new RidingService(this.logger,this, this.dbAccess);
		
		//IEssentials essentials = (IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");

		/**
		 * 커맨드
		 */
		this.commandManager = new CommandManager(this.logger, this, ridingManager);
		
		/**
		 * 콘피그,이벤트 리스너 등록
		 */
		this.registerConfig();
		this.registerEventListener();
		
		
		this.dbAccess.simpleAsyncExecute(SQLSchema.RidingTable);
		this.dbAccess.simpleAsyncExecute(SQLSchema.RidingPlayerTable);
		
		this.logger.log(Level.INFO, "Kudong-Entity-Riding 플러그인이 성공적으로 활성화 되었습니다!");

	}

	@Override
	public void onDisable()
	{
		this.clearData();
		this.logger.log(Level.INFO, "Kudong-Entity-Riding 플러그인이 성공적으로 비활성화 되었습니다!");
	}
	
	public void clearData()
	{
		for(Player p : Bukkit.getServer().getOnlinePlayers())
		{
			RidingCore.ridingManager.getListener().removeSteerableEntity(p);
		}
	}
	
	public static JavaPlugin GetPlugin()
	{
		return plugin;
	}
	
	public static RidingManager GetManager()
	{
		return ridingManager;
	}

	public static RidingService getDbService()
	{
		return dbService;
	}

	private void registerConfig()
	{
		this.configLoader.registerModule("riding", ridingManager.getConfig());
		this.configLoader.loadConfig();
	}

	private void registerEventListener()
	{
		this.pluginManager = Bukkit.getPluginManager();
		this.pluginManager.registerEvents(ridingManager.getListener(),this);
		this.pluginManager.registerEvents(ridingManager.getGuiListener(),this);
	}
	
    private boolean setupFramework() 
    {
        if (getServer().getPluginManager().getPlugin("Kudong-framework") == null) 
        {
        	this.logger.log(Level.SEVERE, "Kudong-framework 디펜던시가 발견되지않았기에 플러그인이 비활성화 되었습니다!");
        	getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        
		Plugin core =  Bukkit.getPluginManager().getPlugin("Kudong-framework");
		this.dbAccess = ((FrameworkCore)core).getDBAccess();
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
    
    
    public static DecimalFormat formatter = new DecimalFormat("###,###.##"); 
	private static RidingManager ridingManager;
	private static RidingService dbService;
	private static JavaPlugin plugin;
	
	

}
