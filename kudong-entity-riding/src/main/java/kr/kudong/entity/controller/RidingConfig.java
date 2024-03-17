package kr.kudong.entity.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import kr.kudong.entity.util.ConfigurationMember;

public class RidingConfig implements ConfigurationMember
{
	private final Logger logger;

	public RidingConfig(Logger logger)
	{
		this.logger = logger;
	}
	
	@Override
	public boolean installConfig(Map<String, Object> config)
	{
		try
		{
//			String raw = config.getOrDefault("lobby-server", DEFAULT_LOCATION).toString();
//			this.lobbyServerLoc = AldarLocation.deserialize(raw);
//			this.lobbyServer = this.lobbyServerLoc.server;
//			
//			logger.log(Level.INFO, "lobby-server: "+lobbyServerLoc.serialize());
//			raw = config.getOrDefault("first-join-location", DEFAULT_LOCATION).toString();
//			this.firstJoinLoc = AldarLocation.deserialize(raw);
//			logger.log(Level.INFO, "first-join-location: "+firstJoinLoc.serialize());

		} catch (Exception e)
		{
			this.logger.log(Level.WARNING, "entitymanage config 로드 실패", e);
			return false;
		}
		return true;
	}

	@Override
	public Map<String, Object> getModuleConfig()
	{
		Map<String, Object> map = new HashMap<>();
//		map.put("lobby-server", this.lobbyServerLoc.serialize());
//		map.put("first-join-location", this.firstJoinLoc.serialize());
		return map;
	}

}
