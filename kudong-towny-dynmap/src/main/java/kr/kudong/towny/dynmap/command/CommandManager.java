package kr.kudong.towny.dynmap.command;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import kr.cosine.towny.api.TownyAPI;
import kr.cosine.towny.data.TownyChunk;
import kr.kudong.towny.dynmap.controller.TownyDynmapManager;

public class CommandManager implements CommandExecutor
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private PluginCommand cmd;
	private TownyDynmapManager manager;
	
	public CommandManager(Logger logger,
			JavaPlugin plugin,
			TownyDynmapManager manager)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.manager = manager;
		
		this.cmd = this.plugin.getCommand("townydyn");
		this.cmd.setExecutor(this);
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player p = (Player) sender;

		if(args.length == 0) 
		{
          p.sendMessage("§a§l타우니 다이나믹맵 §f명령어 도움말");
          p.sendMessage("========================================");
          p.sendMessage("§e/타우니맵 리로드 §6: 실시간 맵을 리로드 합니다.");
          p.sendMessage("§e/타우니맵 정보 §6: 플러그인 정보를 확인합니다.");

			return true;
		}
		
		if(args[0].equalsIgnoreCase("정보") || args[0].equalsIgnoreCase("info"))
		{
            p.sendMessage("§a§l타우니 다이나믹맵 §f시스템 정보");
            p.sendMessage("========================================");
            p.sendMessage("§e플러그인: Kudong-towny-dynmap");
            p.sendMessage("§e버전: 1.0.0 Ver (Minecraft 1.20.4)");
            p.sendMessage("§e제작자: KUDONG");
            p.sendMessage("§e문의: dhku10@gmail.com");
			return true;
		}
		
		if(p.hasPermission("townydyn.admin"))
		{
			if(args[0].equalsIgnoreCase("리로드") || args[0].equalsIgnoreCase("reload"))
			{
	            p.sendMessage("§a타우니맵 리로드를 수행합니다.");
	            this.manager.reloadTownyChunks();
				
			}
			return true;
		}
	
		return true;
	}

}
