package kr.kudong.framework.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import kr.kudong.common.paper.config.ConfigurationMember;

public class ChatConfig implements ConfigurationMember
{
	public static String format = "§f{name} §8» §f{message}";
	public static boolean isBungeecord = false;
	
	private final Logger logger;
	private final JsonParser parser;
	private final ChatManager manager;
	private final Gson gson; 
	
	
	public ChatConfig(Logger logger, ChatManager manager)
	{
		this.logger = logger;
		this.manager = manager;
		this.gson = new Gson();
		this.parser = new JsonParser();
	}

	@Override
	public boolean installConfig(Map<String, Object> config)
	{
		format = config.getOrDefault("ChatFormat",format).toString();
		isBungeecord = Boolean.valueOf(config.getOrDefault("isBungeecord","false").toString());
		return true;
	}

	@Override
	public Map<String, Object> getModuleConfig()
	{
		Map<String, Object> map = new HashMap<>();
		map.put("ChatFormat", format);
		map.put("isBungeecord", isBungeecord);
		return map;
	}

}
