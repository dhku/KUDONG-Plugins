package kr.kudong.entity.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kr.kudong.entity.data.SteerablePreset;
import kr.kudong.entity.util.ConfigurationMember;

public class RidingConfig implements ConfigurationMember
{
	private final Logger logger;
	private final JsonParser parser;
	private final RidingManager manager;
	private final Gson gson; 
	
	public RidingConfig(Logger logger, RidingManager manager)
	{
		this.logger = logger;
		this.manager = manager;
		this.gson = new Gson();
		this.parser = new JsonParser();
	}
	
	@Override
	public boolean installConfig(Map<String, Object> config)
	{
		try
		{
			String raw = config.getOrDefault("preset","["+gson.toJson(new SteerablePreset()+"]")).toString();
			//this.logger.log(Level.INFO,raw);
			
			SteerablePreset[] list = gson.fromJson(raw, SteerablePreset[].class);
			this.manager.setPresetList(Arrays.asList(list));
			
			Map<String,SteerablePreset> map = new HashMap<>();
			
			for(SteerablePreset p : list)
			{
				map.put(p.getPRESET_NAME(), p);
			}
			
			this.manager.setPresetMap(map);
			
			this.logger.log(Level.INFO,"탈것 "+this.manager.getPresetList().size()+"개의 프리셋이 로드되었습니다.");
			
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
		map.put("preset", "["+gson.toJson(new SteerablePreset()+"]"));
		return map;
	}

}
