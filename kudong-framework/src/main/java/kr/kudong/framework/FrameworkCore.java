package kr.kudong.framework;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import kr.kudong.common.basic.comm.ProtocolKey;
import kr.kudong.common.basic.db.DBAccess;
import kr.kudong.common.paper.config.ConfigLoader;
import kr.kudong.framework.chat.ChatListener;
import kr.kudong.framework.chat.ChatManager;
import kr.kudong.framework.comm.FrameworkMessageReceiver;
import kr.kudong.framework.command.BroadcastCommandManager;
import kr.kudong.framework.command.FrameworkCommandManager;
import kr.kudong.framework.command.ScoreboardCommandManager;
import kr.kudong.framework.controller.FrameworkConfig;
import kr.kudong.framework.controller.FrameworkManager;
import kr.kudong.framework.db.FrameworkPlayer;
import kr.kudong.framework.db.SQLSchema;
import kr.kudong.framework.listener.ScoreboardListener;
import kr.kudong.framework.reflection.ReflectionService;
import kr.kudong.framework.scoreboard.TownyScoreboard;
import net.milkbowl.vault.economy.Economy;

public class FrameworkCore extends JavaPlugin 
{
	private static ReflectionService reflect;
	
	private Logger logger;
	private ConfigLoader configLoader;
	private ScoreboardListener scoreboardListener;
	private PluginManager pluginManager;
	private ScoreboardCommandManager scoreboardCommandManager;
	private FrameworkCommandManager frameworkCommandManager;
	private BroadcastCommandManager broadcastCommandManager;
	private FrameworkMessageReceiver comm;
	private DBAccess dbAccess;
	private ChatManager chatManager;
	private FrameworkManager manager;
	
	@Override
	public void onEnable()
	{
		FrameworkCore.plugin = this;
		FrameworkCore.reflect = new ReflectionService(this.logger, this);
		
		if(!this.setupEconomy()) return;
		if(!this.setupPlaceHolder()) return;
		this.checkTowny();
		/**
		 * 디펜던시 로드
		 */
		this.logger = this.getLogger();
		this.configLoader = new ConfigLoader(this, this.logger);
		this.dbAccess = new DBAccess(logger);
		
		this.manager = new FrameworkManager(this.logger, this, this.dbAccess);
		
		this.scoreboardCommandManager = new ScoreboardCommandManager(this.logger, this);
		this.frameworkCommandManager = new FrameworkCommandManager(this.logger, this , this.manager);
		this.broadcastCommandManager = new BroadcastCommandManager(this.logger, this , this.manager);
		
		this.scoreboardListener = new ScoreboardListener(this.logger, this);
		this.chatManager = new ChatManager(this.logger, this);
		
		this.comm = new FrameworkMessageReceiver(this.logger, this);
		
		this.registerConfig();
		this.registerEventListener();
		this.registerPluginChannel();
		
		this.dbAccess.start();
		this.runScheduler();
		
		this.dbAccess.simpleAsyncExecute(SQLSchema.FrameworkPlayerTable);
		this.loadPlayerData();
		
		this.logger.log(Level.INFO, "Kudong-Framework 플러그인이 성공적으로 활성화 되었습니다!");
	}
	
	private void loadPlayerData()
	{
		List<FrameworkPlayer> list = this.manager.getService().selectAllPlayer();
		for(FrameworkPlayer p : list)
			this.manager.map.put(p.getUuid(), p);
	}

	private void registerPluginChannel()
	{
		if(FrameworkConfig.isBungeecord)
		{
			plugin.getServer().getMessenger().registerOutgoingPluginChannel(this, ProtocolKey.MAIN_CHANNEL);
			plugin.getServer().getMessenger().registerIncomingPluginChannel(this, ProtocolKey.MAIN_CHANNEL, this.comm);
			plugin.getServer().getMessenger().registerOutgoingPluginChannel(this, ProtocolKey.BUNGEE_CHANNEL);
			plugin.getServer().getMessenger().registerIncomingPluginChannel(this, ProtocolKey.BUNGEE_CHANNEL, this.comm);
		}
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
		this.pluginManager.registerEvents(this.scoreboardListener,this);
		this.pluginManager.registerEvents(this.manager.getListener(),this);
		this.pluginManager.registerEvents(this.chatManager.getListener(),this);
	}
	
	private void registerConfig()
	{
		this.configLoader.registerModule("framework", this.manager.getConfig());
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
	
	
	private void checkTowny()
	{
        if (getServer().getPluginManager().getPlugin("HQTowny") != null) 
        {
        	isTownyInstalled = true;
        }
	}
	
	public static JavaPlugin plugin;
	public static Economy econ;
	public static boolean isTownyInstalled = false;

}
