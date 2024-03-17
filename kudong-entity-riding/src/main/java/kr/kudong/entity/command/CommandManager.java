package kr.kudong.entity.command;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
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

//		this.cmd = this.plugin.getCommand(AldarPlayerCommand.COMMAND_ALDAR_SERVER);
//		this.cmd.setExecutor(this);
//		this.cmd = this.plugin.getCommand(AldarPlayerCommand.COMMAND_ALDAR_CONNECT_LIST);
//		this.cmd.setExecutor(this);
//		this.cmd = this.plugin.getCommand(AldarPlayerCommand.COMMAND_ALDAR_BLOCK);
//		this.cmd.setExecutor(this);
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
//		switch (command.getName())
//		{
//			case AldarPlayerCommand.COMMAND_ALDAR_SERVER: this.COMMAND_ALDAR_SERVER(sender,args); break;
//			case AldarPlayerCommand.COMMAND_ALDAR_CONNECT_LIST: this.COMMAND_ALDAR_CONNECT_LIST(sender); break;
//			case AldarPlayerCommand.COMMAND_ALDAR_BLOCK: this.COMMAND_ALDAR_BLOCK(sender); break;	
//		}
		
		return true;
	}
	
}
