package kr.kudong.framework.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.framework.FrameworkCore;
import kr.kudong.framework.scoreboard.TownyScoreboard;
import me.clip.placeholderapi.PlaceholderAPI;

public class ScoreboardListener implements Listener
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private Map<UUID,Boolean> map = TownyScoreboard.map;
	
	public ScoreboardListener(Logger logger, JavaPlugin plugin)
	{
		this.logger = logger;
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		if(FrameworkCore.isTownyInstalled == false)
			return;
		
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		Bukkit.getScheduler().runTaskLater(plugin, ()->{
			
			if(!map.containsKey(uuid))
			{
				map.put(uuid, true);
				TownyScoreboard.create(player);
			}
			else
			{
				boolean isActivate = map.get(uuid);
				
				if(isActivate)
					TownyScoreboard.create(player);
				else
					TownyScoreboard.Close(player);
			}
		},10L);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
	}

}
