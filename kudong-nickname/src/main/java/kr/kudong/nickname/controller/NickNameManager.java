package kr.kudong.nickname.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.common.basic.db.DBAccess;
import kr.kudong.framework.reflection.ReflectionService;
import kr.kudong.nickname.db.NickNameDBService;
import kr.kudong.nickname.listener.NickNameListener;


public class NickNameManager
{
	private Logger logger;
	private JavaPlugin plugin;
	private DBAccess dbAccess;
	private NickNameListener listener;
	private NickNameDBService service;
	private Map<UUID,NickNamePlayer> map;
	
	public NickNameManager(Logger logger,JavaPlugin plugin,DBAccess dbAccess)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.dbAccess = dbAccess;
		this.map = new HashMap<>();
		this.listener = new NickNameListener(this.logger,this.plugin, this);
		this.service = new NickNameDBService(this.logger, this.plugin, this.dbAccess);
	}
	
	public void registerNickNamePlayer(UUID uuid,NickNamePlayer np)
	{
		this.map.put(uuid, np);
	}

	public void removeNickNamePlayer(UUID uuid)
	{
		this.map.remove(uuid);
	}
	
	public boolean containsNickNamePlayer(UUID uuid)
	{
		return this.map.containsKey(uuid);
	}
	
	public NickNamePlayer getNickNamePlayer(UUID uuid)
	{
		return this.map.get(uuid);
	}
	
	public NickNameDBService getService()
	{
		return service;
	}

	public void setService(NickNameDBService service)
	{
		this.service = service;
	}
	
	public Map<UUID, NickNamePlayer> getMap()
	{
		return map;
	}

	public void setMap(Map<UUID, NickNamePlayer> map)
	{
		this.map = map;
	}

	public void applyNickName(NickNamePlayer np)
	{
		Player player = np.getBukkitPlayer();
		String nickname = np.getNickName();
		if(nickname != null)
		{
			this.hideAllPlayer(player);
			ReflectionService.setGameProfile(player, nickname);
			this.showAllPlayer(player);
		}
	}
	
	public void applyNickName(Player player, String nickname)
	{
		if(nickname != null)
		{
			this.hideAllPlayer(player);
			ReflectionService.setGameProfile(player, nickname);
			this.showAllPlayer(player);
		}
	}
	
	public void hideAllPlayer(Player player)
	{
		for (Player p : Bukkit.getServer().getOnlinePlayers())
			      p.hidePlayer(this.plugin, player);
	}
	
	public void showAllPlayer(Player player)
	{
		for (Player p : Bukkit.getServer().getOnlinePlayers())
			      p.showPlayer(this.plugin, player);
	}
	
	public NickNameListener getListener()
	{
		return listener;
	}
	
}
