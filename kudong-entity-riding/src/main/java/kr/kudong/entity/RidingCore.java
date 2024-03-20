package kr.kudong.entity;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.entity.command.CommandManager;
import kr.kudong.entity.controller.RidingManager;
import kr.kudong.entity.util.ConfigLoader;
import net.milkbowl.vault.economy.Economy;


public class RidingCore extends JavaPlugin
{

	private Logger logger;
	private ConfigLoader configLoader;
	private PluginManager pluginManager;
	private CommandManager commandManager;
	private Economy econ = null;
	private static RidingManager ridingManager;
	private static JavaPlugin plugin;
	
	@Override
	public void onEnable()
	{
		/**
		 * 디펜던시 로드
		 */
		RidingCore.plugin = this;
		this.logger = this.getLogger();
		this.configLoader = new ConfigLoader(this, this.logger);
		
		if(!this.setupEconomy())return;

		/**
		 * 컨트롤러
		 */
		RidingCore.ridingManager = new RidingManager(this.logger,this,this.econ);

		/**
		 * 커맨드
		 */
		this.commandManager = new CommandManager(this.logger, this, this.ridingManager);
		/**
		 * 콘피그,이벤트 리스너 등록
		 */
		this.registerConfig();
		this.registerEventListener();
		//this.registerMessageProtocols();

	}

	@Override
	public void onDisable()
	{
		// TODO Auto-generated method stub

	}
	
	public static JavaPlugin GetPlugin()
	{
		return plugin;
	}
	
	public static RidingManager GetManager()
	{
		return ridingManager;
	}
	
	private void registerConfig()
	{
		this.configLoader.registerModule("riding", this.ridingManager.getConfig());
		this.configLoader.loadConfig();
	}

	private void registerEventListener()
	{
		this.pluginManager = Bukkit.getPluginManager();
		this.pluginManager.registerEvents(this.ridingManager.getListener(),this);
		this.pluginManager.registerEvents(this.ridingManager.getGuiListener(),this);
	}
	
    private boolean setupEconomy() {
   
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
        	this.logger.log(Level.SEVERE, "Vault 디펜던시가 발견되지않았기에 플러그인이 비활성화 되었습니다!");
        	getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
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

}
