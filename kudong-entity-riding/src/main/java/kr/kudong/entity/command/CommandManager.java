package kr.kudong.entity.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.entity.controller.RidingManager;


public class CommandManager implements CommandExecutor
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private PluginCommand cmd;
	private RidingManager manager;
	
	public CommandManager(Logger logger,
			JavaPlugin plugin,
			RidingManager manager)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.manager = manager;
		
		this.cmd = this.plugin.getCommand("ride");
		this.cmd.setExecutor(this);
//		this.cmd = this.plugin.getCommand(AldarPlayerCommand.COMMAND_ALDAR_CONNECT_LIST);
//		this.cmd.setExecutor(this);
//		this.cmd = this.plugin.getCommand(AldarPlayerCommand.COMMAND_ALDAR_BLOCK);
//		this.cmd.setExecutor(this);
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player p = (Player) sender;
		
		this.logger.log(Level.INFO, label);
		
		if(args.length == 0) 
		{
			if(label.equalsIgnoreCase("탈것")) 
			{
                p.sendMessage("§a§l탈것 §f시스템 명령어 도움말");
                p.sendMessage("========================================");
                p.sendMessage("§e/탈것 §6: 탈것 관리 메뉴를 엽니다.");
                p.sendMessage("§e/탈것 채팅 §6: 마을 채팅모드로 전환합니다.");
                p.sendMessage("§e/마을 메뉴 §6: 마을 관리 메뉴를 엽니다.");
                p.sendMessage("§e/마을 점수 §6: 마을의 점수를 확인합니다.");
                p.sendMessage("§e/마을 정보 §6: 마을의 정보를 확인합니다.");
			}
			else
			{
                p.sendMessage("§a§lTowny §fSystem Command Help");
                p.sendMessage("========================================");
                p.sendMessage("§e/towny §6: Teleport to Towny Core.");
                p.sendMessage("§e/towny chat §6: Towny Chat Mode");
                p.sendMessage("§e/towny menu §6: Open the Towny Management Menu");
                p.sendMessage("§e/towny point §6: Check to MyTowny Point");
                p.sendMessage("§e/towny info §6: Check to MyTowny Information");
                p.sendMessage("§e/towny remove §6: Remove the Towny.");
                p.sendMessage("§e/towny leave §6: Leave the Towny.");
			}
			return true;
		}
		
		if(args[0].equalsIgnoreCase("테스트") || args[0].equalsIgnoreCase("test"))
		{
			p.sendMessage("§a안녕§l실험 §f테스트2");
			return true;
		}
		
		
		return true;
	}

}
