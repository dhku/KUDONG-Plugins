package kr.kudong.framework.command;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.framework.controller.FrameworkManager;

public class FrameworkCommandManager
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private FrameworkManager manager;
	
	private ScoreboardCommand scoreboardCommand;
	private FrameworkCommand frameworkCommand;
	private BroadcastCommand broadcastCommand;
	private WhisperCommand whisperCommand;
	private WhitelistCommand whitelistCommand;
	
	public FrameworkCommandManager(Logger logger,JavaPlugin plugin,FrameworkManager manager)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.manager = manager;
		this.scoreboardCommand = new ScoreboardCommand(this.logger, this.plugin);
		this.frameworkCommand = new FrameworkCommand(this.logger, this.plugin , this.manager);
		this.broadcastCommand = new BroadcastCommand(this.logger, this.plugin , this.manager);
		this.whisperCommand = new WhisperCommand(this.logger, this.plugin , this.manager);
		this.whitelistCommand = new WhitelistCommand(this.logger, this.plugin , this.manager);
	}

}
