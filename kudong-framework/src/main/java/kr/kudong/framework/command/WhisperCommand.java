package kr.kudong.framework.command;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import kr.kudong.common.basic.comm.ProtocolKey;
import kr.kudong.framework.controller.FrameworkManager;
import kr.kudong.framework.db.NickNameResult;
import kr.kudong.framework.task.PlayerWhisperTask;
import net.md_5.bungee.api.chat.TextComponent;


public class WhisperCommand implements CommandExecutor
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private PluginCommand cmd;
	private FrameworkManager manager;

	public WhisperCommand(Logger logger, JavaPlugin plugin, FrameworkManager manager)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.manager = manager;
		this.cmd = this.plugin.getCommand("ws");
		this.cmd.setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		boolean isConsoleSender = false;
		Player player = null;

		if(sender instanceof ConsoleCommandSender)
		{
			isConsoleSender = true;
		}
		else
		{
			player = (Player)sender;
		}
		
		if(isConsoleSender) return true;

		if(args.length <= 1)
		{
			player.sendMessage(new TextComponent("§e/귓 <플레이어> <할말> §f: 플레이어에게 귓속말을 보냅니다."));
		}

		if(args.length > 1)
		{
			Bukkit.getScheduler().runTaskAsynchronously(this.plugin,new PlayerWhisperTask(isConsoleSender, player, args, plugin));
		}
		return true;
	}
}
