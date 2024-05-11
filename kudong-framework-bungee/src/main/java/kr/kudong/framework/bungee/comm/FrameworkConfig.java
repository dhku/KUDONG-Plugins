package kr.kudong.framework.bungee.comm;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import kr.kudong.common.basic.config.ConfigurationMember;

public class FrameworkConfig implements ConfigurationMember
{
	public static boolean isMaintenenceMode = false;
	
	private final Logger logger;
	private final JsonParser parser;
	private final Gson gson; 
	
	public FrameworkConfig(Logger logger)
	{
		this.logger = logger;
		this.gson = new Gson();
		this.parser = new JsonParser();
	}

	@Override
	public boolean installConfig(Map<String, Object> config)
	{
		isMaintenenceMode = Boolean.valueOf(config.getOrDefault("isMaintenenceMode","false").toString());
		return true;
	}

	@Override
	public Map<String, Object> getModuleConfig()
	{
		Map<String, Object> map = new HashMap<>();
		map.put("isMaintenenceMode", isMaintenenceMode);
		return map;
	}

}
