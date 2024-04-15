package kr.kudong.framework;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.common.basic.db.DBAccess;
import kr.kudong.common.paper.config.ConfigLoader;
import kr.kudong.framework.command.CommandManager;
import kr.kudong.framework.listener.FrameworkListener;
import kr.kudong.framework.reflection.ReflectionService;

public class FrameworkCore extends JavaPlugin
{
	private static JavaPlugin plugin;
	private static ReflectionService reflect;
	
	private Logger logger;
	private ConfigLoader configLoader;
	private FrameworkListener listener;
	private PluginManager pluginManager;
	private CommandManager commandManager;
	private DBAccess dbAccess;
	
	@Override
	public void onEnable()
	{
		FrameworkCore.plugin = this;
		FrameworkCore.reflect = new ReflectionService(this.logger, this);
		/**
		 * 디펜던시 로드
		 */
		this.logger = this.getLogger();
		this.configLoader = new ConfigLoader(this, this.logger);
		this.dbAccess = new DBAccess(logger);
		this.commandManager = new CommandManager(this.logger, this);
		this.listener = new FrameworkListener(this.logger, this);
		
		this.registerConfig();
		this.registerEventListener();
		
		this.dbAccess.start();
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

}
