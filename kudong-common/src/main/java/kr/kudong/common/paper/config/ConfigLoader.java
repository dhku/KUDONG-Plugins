package kr.kudong.common.paper.config;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class ConfigLoader extends AbsConfigLoader
{
	
	public ConfigLoader(JavaPlugin plugin, Logger logger)
	{
		File f = plugin.getDataFolder();
		this.init(f, logger);
	}

}
