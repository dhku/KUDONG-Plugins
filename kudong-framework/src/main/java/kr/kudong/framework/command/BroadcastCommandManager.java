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
import kr.kudong.framework.controller.FrameworkConfig;
import kr.kudong.framework.controller.FrameworkManager;

public class BroadcastCommandManager implements CommandExecutor
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private PluginCommand cmd;
	private FrameworkManager manager;
	
	public BroadcastCommandManager(Logger logger, JavaPlugin plugin,FrameworkManager manager)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.manager = manager;
		this.cmd = this.plugin.getCommand("bc");
		this.cmd.setExecutor(this);
	}
	
	public void printHelpMessage(Player player)
	{
		player.sendMessage("§e/bc chat §f<할말> : 전체 공지를 방송합니다.");
		player.sendMessage("§e/방송 채팅 §f<할말> : 전체 공지를 방송합니다.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		boolean isConsoleSender = false;
		Player player = null;
		
		if(sender instanceof ConsoleCommandSender)
			isConsoleSender = true;
		else player = (Player)sender;

		if(!isConsoleSender && !player.hasPermission("kudong.admin"))
		{
			player.sendMessage("§c해당 명령어를 사용할 권한이 없습니다.");
			return true;
		}
		
		if(args.length == 0)
		{
			if(!isConsoleSender)this.printHelpMessage(player);
			return true;
		}

		if((args[0].equalsIgnoreCase("chat") || args[0].equalsIgnoreCase("채팅")) && args.length >= 2)
		{
			StringBuilder s = new StringBuilder();
			
			for(int i=1; i < args.length; i++)
			{
				s.append(args[i]);
				s.append(" ");
			}
			
			String format = s.toString();
			
			if(FrameworkConfig.isBungeecord)
				this.sendBroadcastPlayer("chat1",format);
			else
			{
				for(Player p : Bukkit.getServer().getOnlinePlayers())
				{
					p.sendMessage("&l---------------------------");
					p.sendMessage(" ");
					p.sendMessage(" &6공지사항 &f: "+format);
					p.sendMessage(" ");
					p.sendMessage("&l---------------------------");
				}
			}
			
			if(isConsoleSender)
			{
				this.logger.log(Level.INFO,"&l---------------------------");
				this.logger.log(Level.INFO," ");
				this.logger.log(Level.INFO," &6공지사항 &f: "+format);
				this.logger.log(Level.INFO," ");
				this.logger.log(Level.INFO,"&l---------------------------");
			}
			
			return true;
		}
		
		if(!isConsoleSender)this.printHelpMessage(player);
		return true;
	}
	
	public void sendBroadcastPlayer(String type, String format)
	{
		Player dummyPlayer = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

		if(dummyPlayer == null)
		{
			this.logger.log(Level.INFO,"최소 1명의 플레이어가 서버에 접속해있어야 패킷 전달이 가능합니다.");
			return;
		}
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(ProtocolKey.BROADCAST_PLAYER);
		out.writeUTF(type); //type 
		out.writeUTF(format); //format
		
		dummyPlayer.sendPluginMessage(this.plugin, ProtocolKey.MAIN_CHANNEL, out.toByteArray());
	}
}
