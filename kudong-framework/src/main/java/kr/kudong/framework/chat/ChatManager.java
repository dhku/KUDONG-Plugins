package kr.kudong.framework.chat;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.framework.controller.FrameworkConfig;


public class ChatManager
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private ChatListener listener;
	
	public ChatManager(Logger logger, JavaPlugin plugin)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.listener = new ChatListener(logger,plugin,this);
	}

	public ChatListener getListener()
	{
		return listener;
	}
	
}
