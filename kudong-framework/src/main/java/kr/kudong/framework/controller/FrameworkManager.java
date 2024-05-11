package kr.kudong.framework.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.common.basic.db.DBAccess;
import kr.kudong.framework.db.NickNameResult;
import kr.kudong.framework.listener.FrameworkListener;

public class FrameworkManager
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private final FrameworkListener listener;
	private FrameworkConfig config;
	private MessageConfig msgConfig;

	public FrameworkManager(Logger logger, JavaPlugin plugin, DBAccess dbAccess)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.config = new FrameworkConfig(logger,this);
		this.msgConfig = new MessageConfig(logger);
		this.listener = new FrameworkListener(this.logger,this.plugin,this);
	}
	
	public FrameworkConfig getConfig()
	{
		return config;
	}

	public MessageConfig getMsgConfig()
	{
		return msgConfig;
	}

	public FrameworkListener getListener()
	{
		return listener;
	}
	
	public static Map<UUID,NickNameResult> joinCache = new HashMap<>();

}
