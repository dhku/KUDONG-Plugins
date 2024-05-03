package kr.kudong.framework.bungee.command;

import java.util.logging.Logger;

import kr.kudong.common.basic.db.DBAccess;
import net.md_5.bungee.api.plugin.Plugin;

public class CommandManager
{
	private final Plugin plugin;
	private final Logger logger;
	private final DBAccess dbAccess;
	
	public CommandManager(Plugin plugin, Logger logger,DBAccess dbAccess)
	{
		this.plugin = plugin;
		this.logger = logger;
		this.dbAccess = dbAccess;
		this.plugin.getProxy().getPluginManager().registerCommand(plugin,
				new WhisperCommand(logger, dbAccess));
	}
}
