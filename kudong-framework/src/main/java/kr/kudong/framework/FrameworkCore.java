package kr.kudong.framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import kr.kudong.framework.command.BroadcastCommand;
import kr.kudong.framework.command.FrameworkCommand;
import kr.kudong.framework.command.FrameworkCommandManager;
import kr.kudong.framework.command.ScoreboardCommand;
import kr.kudong.framework.command.WhisperCommand;
import kr.kudong.framework.controller.FrameworkConfig;
import kr.kudong.framework.controller.FrameworkManager;
import kr.kudong.framework.db.NickNameResult;
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
	private FrameworkCommandManager cmd;
	private FrameworkMessageReceiver comm;
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
		dbAccess = new DBAccess(logger);
		
		this.manager = new FrameworkManager(this.logger, this, dbAccess);
		this.cmd = new FrameworkCommandManager(this.logger, this, this.manager);
		this.scoreboardListener = new ScoreboardListener(this.logger, this);
		this.chatManager = new ChatManager(this.logger, this);
		
		this.comm = new FrameworkMessageReceiver(this.logger, this);
		
		this.registerConfig();
		this.registerEventListener();
		this.registerPluginChannel();
		this.runScheduler();
		
		dbAccess.start();
		dbAccess.simpleAsyncExecute(SQLSchema.NickNameTable);
		
		this.logger.log(Level.INFO, "Kudong-Framework 플러그인이 성공적으로 활성화 되었습니다!");
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
		return dbAccess;
	}

	@Override
	public void onDisable()
	{
		dbAccess.stop();
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
		this.configLoader.registerModule("message", this.manager.getMsgConfig());
		this.configLoader.registerModule("database", dbAccess);
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
	
	public Map<UUID,NickNameResult> getJoinCacheMap()
	{
		return FrameworkManager.joinCache;
	}
	
	public static JavaPlugin plugin;
	public static Economy econ;
	public static DBAccess dbAccess;
	public static boolean isTownyInstalled = false;
	
}
