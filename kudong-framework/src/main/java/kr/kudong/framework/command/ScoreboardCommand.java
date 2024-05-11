package kr.kudong.framework.command;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.framework.FrameworkCore;
import kr.kudong.framework.scoreboard.TownyScoreboard;

public class ScoreboardCommandManager implements CommandExecutor
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private PluginCommand cmd;
	private Map<UUID,Boolean> map = TownyScoreboard.map;
	
	public ScoreboardCommandManager(Logger logger,
			JavaPlugin plugin)
	{
		this.logger = logger;
		this.plugin = plugin;
		
		this.cmd = this.plugin.getCommand("scb");
		this.cmd.setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		
		if(FrameworkCore.isTownyInstalled == false)
			return true;
		
		if(args.length == 0)
		{
			if(map.containsKey(uuid))
			{
				boolean isActivate = map.get(uuid);
				
				if(isActivate)
				{
					TownyScoreboard.Close(player);
					map.put(uuid, false);
				}
				else
				{
					TownyScoreboard.create(player);
					map.put(uuid, true);
				}		
			}
			return true;
		}

		return true;
	}
	
	private Player getPlayer(String displayName)
	{
//		char COLOR_CHAR = '\u00A7';
//		changedName = changedName.replaceAll("&", String.valueOf(COLOR_CHAR));
//		changedName = ChatColor.stripColor(changedName);
		
		for(Player p : Bukkit.getServer().getOnlinePlayers())
		{
			String pName = ChatColor.stripColor(p.getDisplayName());
			if(pName.equals(displayName)) return p;
		}
		return Bukkit.getPlayer(displayName);
	}

	
	
}
