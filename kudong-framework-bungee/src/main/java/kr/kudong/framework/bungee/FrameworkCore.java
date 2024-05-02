package kr.kudong.framework.bungee;

import java.util.logging.Logger;

import kr.kudong.common.basic.comm.ProtocolKey;
import kr.kudong.framework.bungee.comm.FrameworkMessageReceiver;
import net.md_5.bungee.api.plugin.Plugin;

public class FrameworkCore extends Plugin
{
	private Logger logger;
	
	@Override
	public void onEnable()
	{
		this.logger = this.getLogger();
		
		getProxy().getPluginManager().registerListener(this, new FrameworkCoreListener(logger));
		getProxy().getPluginManager().registerListener(this, new FrameworkMessageReceiver(logger));
		getProxy().registerChannel( ProtocolKey.MAIN_CHANNEL );
		
		this.logger.info("=========# KUDONG framework enable #=========");	
	}
	
	@Override
	public void onDisable()
	{

	}
}
