package kr.kudong.framework.command;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.framework.controller.FrameworkManager;
import kr.kudong.framework.task.PlayerMoveTask;
import kr.kudong.framework.task.PlayerWhitelistTask;

public class WhitelistCommand implements TabExecutor
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private PluginCommand cmd;
	private FrameworkManager manager;

	public WhitelistCommand(Logger logger, JavaPlugin plugin, FrameworkManager manager)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.manager = manager;
		this.cmd = this.plugin.getCommand("member");
		this.cmd.setExecutor(this);
	}
	
	public static void printHelpMessage(Player player)
	{
		player.sendMessage("§aKUDONG-FRAMEWORK §f명령어 도움말");
		player.sendMessage("========================================");
		player.sendMessage("§e/member 또는 /멤버 또는 /멤 또는 /apa 으로 사용가능");
		player.sendMessage("§e/member add §f<player>: 서버에 화이트리스트 플레이어를 추가합니다.");
		player.sendMessage("§e/member remove §f<player>: 서버에 화이트리스트 플레이어를 추가합니다.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		boolean isConsoleSender = false;
		Player player = null;

		if(sender instanceof ConsoleCommandSender) isConsoleSender = true;
		else player = (Player)sender;

		if(!isConsoleSender && (!player.hasPermission("kudong.member") && !player.hasPermission("kudong.admin")))
		{
			player.sendMessage("§c해당 명령어를 사용할 권한이 없습니다.");
			return true;
		}
		
		if(args.length == 0)
		{
			if(!isConsoleSender) WhitelistCommand.printHelpMessage(player);
			return true;
		}
		
		Bukkit.getScheduler().runTaskAsynchronously(this.plugin,
				new PlayerWhitelistTask(isConsoleSender, player, args, plugin));

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		// TODO Auto-generated method stub
		return null;
	}


}
