package kr.kudong.entity.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.User;
import com.earth2me.essentials.UserMap;

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
            p.sendMessage("§e/탈것 or /ride or /xkf or /xkfrjt §6: 탈것 관리 메뉴를 엽니다.");
            p.sendMessage("§e/탈것 도움말 §6: 탈것 명령어를 확인합니다.");
            p.sendMessage("§e/탈것 정보 §6: 플러그인 정보를 확인합니다.");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("정보") || args[0].equalsIgnoreCase("info"))
		{
            p.sendMessage("§a§l탈것 §f시스템 정보");
            p.sendMessage("========================================");
            p.sendMessage("§e플러그인: Kudong-entity-riding");
            p.sendMessage("§e버전: 1.0.0 Ver (Minecraft 1.20.4)");
            p.sendMessage("§e제작자: KUDONG");
            p.sendMessage("§e문의: dhku10@gmail.com");
			return true;
		}

		return true;
	}

}
