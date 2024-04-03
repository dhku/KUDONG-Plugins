package kr.kudong.framework.command;

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

public class CommandManager implements CommandExecutor
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private PluginCommand cmd;
	
	public CommandManager(Logger logger,
			JavaPlugin plugin)
	{
		this.logger = logger;
		this.plugin = plugin;
		
		this.cmd = this.plugin.getCommand("kudong");
		this.cmd.setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player p = (Player) sender;
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
