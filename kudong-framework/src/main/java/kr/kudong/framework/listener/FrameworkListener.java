package kr.kudong.framework.listener;

import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.framework.controller.FrameworkManager;
import kr.kudong.framework.db.FrameworkDBService;
import kr.kudong.framework.db.FrameworkPlayer;

public class FrameworkListener implements Listener
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private final FrameworkManager manager;
	
	public FrameworkListener(Logger logger, JavaPlugin plugin, FrameworkManager manager)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.manager = manager;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();	
		
		FrameworkDBService db = this.manager.getService();
		String username = db.getUsernameInPlayerData(uuid);

		if(username == null)
		{
			db.insertPlayerData(player);
			this.manager.map.put(uuid, new FrameworkPlayer(uuid, player.getName()));
		}
		else
			this.manager.map.put(uuid, new FrameworkPlayer(uuid, username));
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
	}

}
