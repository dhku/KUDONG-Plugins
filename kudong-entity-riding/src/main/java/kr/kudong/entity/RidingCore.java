package kr.kudong.entity;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.entity.command.CommandManager;
import kr.kudong.entity.controller.RidingManager;
import kr.kudong.entity.util.ConfigLoader;


public class RidingCore extends JavaPlugin
{

	private Logger logger;
	private ConfigLoader configLoader;
	private PluginManager pluginManager;
	private CommandManager commandManager;
	private RidingManager ridingManager;
	
	@Override
	public void onEnable()
	{
		/**
		 * 디펜던시 로드
		 */
		this.logger = this.getLogger();
		this.configLoader = new ConfigLoader(this, this.logger);
		/**
		 * 컨트롤러
		 */
		this.ridingManager = new RidingManager(this.logger,this);

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
	
	private void registerConfig()
	{
		this.configLoader.registerModule("riding", this.ridingManager.getConfig());
		this.configLoader.loadConfig();
	}

	private void registerEventListener()
	{
		this.pluginManager = Bukkit.getPluginManager();
		this.pluginManager.registerEvents(this.ridingManager.getListener(),this);
	}

}
