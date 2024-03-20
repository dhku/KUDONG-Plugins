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
import kr.kudong.entity.gui.RidingMainGUI;


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
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player p = (Player) sender;
		
		if(args.length == 0) 
		{
			new RidingMainGUI(p).openGUI();
			return true;
		}
		
		if(args[0].equalsIgnoreCase("도움말") || args[0].equalsIgnoreCase("help"))
		{
            p.sendMessage("§a§l탈것 §f시스템 명령어 도움말");
            p.sendMessage("========================================");
            p.sendMessage("§e/탈것 §6: 탈것 관리 메뉴를 엽니다.");
            p.sendMessage("§e/탈것 채팅 §6: 마을 채팅모드로 전환합니다.");
            p.sendMessage("§e/마을 메뉴 §6: 마을 관리 메뉴를 엽니다.");
            p.sendMessage("§e/마을 점수 §6: 마을의 점수를 확인합니다.");
            p.sendMessage("§e/마을 정보 §6: 마을의 정보를 확인합니다.");
			return true;
		}
		
		
		return true;
	}

}
