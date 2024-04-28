package kr.kudong.towny.dynmap.listener;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import kr.cosine.towny.api.TownyAPI;
import kr.cosine.towny.data.TownyChunk;
import kr.cosine.towny.event.TownyChunkCreateEvent;
import kr.cosine.towny.event.TownyChunkDeleteEvent;
import kr.kudong.towny.dynmap.controller.TownyDynmapManager;
import kr.kudong.towny.dynmap.controller.TownyDynmapPlayer;



public class TownyDynmapListener implements Listener
{
	private Logger logger;
	private TownyDynmapManager manager;
	private TownyAPI api;
	private JavaPlugin plugin;
	
	public TownyDynmapListener(Logger logger,JavaPlugin plugin,TownyDynmapManager manager)
	{
		this.logger = logger;
		this.manager = manager;
		this.plugin = plugin;
		this.api = TownyAPI.INSTANCE;
	}
	
	@EventHandler
	public void onCreateTownyChunk(TownyChunkCreateEvent e)
	{
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		
		if(manager.containsTownyDynmapPlayer(uuid))
		{
			TownyDynmapPlayer player = manager.getTownyDynmapPlayer(uuid);
			player.clearMarker();
			manager.loadPlayerTownyChunks(player);
			manager.drawPlayerTownyChunks(player);
		}
		else
		{
			TownyDynmapPlayer player = new TownyDynmapPlayer(p);
			manager.loadPlayerTownyChunks(player);
			manager.drawPlayerTownyChunks(player);
		}
	}
	
	@EventHandler
	public void onDeleteTownyChunk(TownyChunkDeleteEvent e)
	{
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		
		if(manager.containsTownyDynmapPlayer(uuid))
		{
			TownyDynmapPlayer player = manager.getTownyDynmapPlayer(uuid);
			player.clearMarker();
			manager.loadPlayerTownyChunks(player);
			manager.drawPlayerTownyChunks(player);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		
	}
	
}
