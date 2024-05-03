package kr.kudong.common.bungee.config;

import net.md_5.bungee.api.plugin.Plugin;
import kr.kudong.common.basic.config.AbsConfigLoader;

import java.io.File;
import java.util.logging.Logger;


public class ConfigLoader extends AbsConfigLoader
{

	public ConfigLoader(Plugin plugin, Logger logger)
	{
		File f = plugin.getDataFolder();
		this.init(f, logger);
	}
}