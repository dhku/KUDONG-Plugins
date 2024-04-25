package kr.kudong.book;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;

import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.book.event.EventListener;
import kr.kudong.book.util.CustomFilter;

import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.core.Logger;

public class AldarBookCore extends JavaPlugin implements CommandExecutor, Listener
{
	private static AldarBookCore instance;
	private AldarBookManager bookManager;
	private PluginManager pluginManager;
	private Logger logger;
	private CommandListener commandListener;

	@Override
	public void onEnable()
	{
		instance = this;
		logger = (Logger) LogManager.getRootLogger();
		((Logger) LogManager.getRootLogger()).addFilter(new CustomFilter());
		bookManager = new AldarBookManager(logger, this);

		pluginManager = Bukkit.getPluginManager();

		pluginManager.registerEvents(new EventListener(logger, bookManager), this);

		commandListener = new CommandListener(logger, bookManager, this);

		bookManager.addAllPlayerBookData();

	}

	@Override
	public void onDisable()
	{
		bookManager.clearAlldata();

	}

	public AldarBookManager getBookAPI()
	{
		return bookManager;
	}

	public static AldarBookCore getMain()
	{
		return instance;
	}

}
