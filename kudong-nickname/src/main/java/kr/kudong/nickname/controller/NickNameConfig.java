package kr.kudong.nickname.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import kr.kudong.common.basic.config.ConfigurationMember;

public class NickNameConfig implements ConfigurationMember
{
	public static String joinMessage = "[§2+§f] §b{player}";
	public static String quitMessage = "[§4-§f] §b{player}";
	
	private final Logger logger;
	private final JsonParser parser;
	private final NickNameManager manager;
	private final Gson gson; 
	
	public NickNameConfig(Logger logger, NickNameManager manager)
	{
		this.logger = logger;
		this.manager = manager;
		this.gson = new Gson();
		this.parser = new JsonParser();
	}

	@Override
	public boolean installConfig(Map<String, Object> config)
	{
		joinMessage = config.getOrDefault("joinMessage",joinMessage).toString();
		quitMessage = config.getOrDefault("quitMessage",quitMessage).toString();
		return true;
	}

	@Override
	public Map<String, Object> getModuleConfig()
	{
		Map<String, Object> map = new HashMap<>();
		map.put("joinMessage", joinMessage);
		map.put("quitMessage", quitMessage);
		return map;
	}

}
