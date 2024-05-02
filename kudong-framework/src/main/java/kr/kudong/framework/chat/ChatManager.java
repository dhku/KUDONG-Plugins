package kr.kudong.framework.chat;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;


public class ChatManager
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private ChatListener listener;
	private ChatConfig config;
	
	public ChatManager(Logger logger, JavaPlugin plugin)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.config = new ChatConfig(logger,this);
		this.listener = new ChatListener(logger,plugin,this);
	}

	public ChatListener getListener()
	{
		return listener;
	}

	public ChatConfig getConfig()
	{
		return config;
	}

}
