package kr.kudong.framework.bungee;

import java.util.logging.Logger;

import kr.kudong.common.basic.comm.ProtocolKey;
import kr.kudong.common.basic.db.DBAccess;
import kr.kudong.common.bungee.config.ConfigLoader;
import kr.kudong.framework.bungee.comm.FrameworkConfig;
import kr.kudong.framework.bungee.comm.FrameworkMessageReceiver;
import kr.kudong.framework.bungee.db.SQLSchema;
import kr.kudong.framework.bungee.listener.FrameworkCoreListener;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.event.player.PlayerLoadEvent;
import net.md_5.bungee.api.plugin.Plugin;

public class FrameworkCore extends Plugin
{
	private Logger logger;
	private ConfigLoader configLoader;
	private FrameworkConfig config;
	
	@Override
	public void onEnable()
	{
		this.logger = this.getLogger();
		this.configLoader = new ConfigLoader(this, this.logger);
		this.config = new FrameworkConfig(logger);
		dbAccess = new DBAccess(logger);
		this.registerPluginChannel();
		this.registerConfig();
		
		dbAccess.start();
		dbAccess.simpleAsyncExecute(SQLSchema.NickNameTable);
		
		this.logger.info("=========# KUDONG framework enable #=========");	
	}

	private void registerConfig()
	{
		this.configLoader.registerModule("database", dbAccess);
		this.configLoader.registerModule("framework", this.config);
		this.configLoader.loadConfig();
	}
	
	private void registerPluginChannel()
	{
		getProxy().getPluginManager().registerListener(this, new FrameworkCoreListener(logger,this,this.dbAccess));
		getProxy().getPluginManager().registerListener(this, new FrameworkMessageReceiver(logger));
		getProxy().registerChannel( ProtocolKey.MAIN_CHANNEL );
	}
	
	@Override
	public void onDisable()
	{
		
	}
	
	public static DBAccess dbAccess;
}
