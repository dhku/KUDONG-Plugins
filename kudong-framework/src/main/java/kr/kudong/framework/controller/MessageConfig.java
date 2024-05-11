package kr.kudong.framework.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import kr.kudong.common.basic.config.ConfigurationMember;

public class MessageConfig implements ConfigurationMember
{
	public static String Message_Not_Whitelist = "당신은 해당 서버에 입장하실수 없습니다.";
	private final Logger logger;
	private final JsonParser parser;
	private final Gson gson; 
	
	public MessageConfig(Logger logger)
	{
		this.logger = logger;
		this.gson = new Gson();
		this.parser = new JsonParser();
	}

	@Override
	public boolean installConfig(Map<String, Object> config)
	{
		Message_Not_Whitelist = config.getOrDefault("Message_No_Whitelist", Message_Not_Whitelist).toString();
		return true;
	}

	@Override
	public Map<String, Object> getModuleConfig()
	{
		Map<String, Object> map = new HashMap<>();
		map.put("Message_No_Whitelist", Message_Not_Whitelist);
		return map;
	}
	
}
