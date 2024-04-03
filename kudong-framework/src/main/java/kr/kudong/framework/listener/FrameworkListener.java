package kr.kudong.framework.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class FrameworkListener implements Listener
{
	private final Logger logger;
	private final JavaPlugin plugin;
	
	public FrameworkListener(Logger logger, JavaPlugin plugin)
	{
		this.logger = logger;
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Player player = e.getPlayer();
		
//		Bukkit.getScheduler().runTaskLater(plugin, ()->{
//			String changedName = player.getDisplayName();
//			
//			char COLOR_CHAR = '\u00A7';
//			changedName = changedName.replaceAll("&", String.valueOf(COLOR_CHAR));
//			changedName = ChatColor.stripColor(changedName);
//		}, 3L);
	}

}
