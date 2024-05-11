package kr.kudong.framework.command;

import java.util.Map;
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
import kr.kudong.common.basic.util.AldarLocation;
import kr.kudong.common.paper.util.AldarLocationUtil;
import kr.kudong.framework.controller.FrameworkConfig;
import kr.kudong.framework.controller.FrameworkManager;

public class FrameworkCommandManager implements CommandExecutor
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private PluginCommand cmd;
	private FrameworkManager manager;
	
	public FrameworkCommandManager(Logger logger, JavaPlugin plugin,FrameworkManager manager)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.manager = manager;
		this.cmd = this.plugin.getCommand("kudong");
		this.cmd.setExecutor(this);
	}
	
	public void printHelpMessage(Player player)
	{
		player.sendMessage("§aKUDONG-FRAMEWORK §f명령어 도움말");
		player.sendMessage("========================================");
		player.sendMessage("§e/kudong 또는 /kd 또는 /쿠동 으로 사용가능");
		player.sendMessage("§e/kudong server : 현재 서버를 확인합니다.");
		player.sendMessage("§e/kudong server §f<서버> : 해당 서버로 이동합니다.");
		player.sendMessage("§e/kudong move §f<player> <서버>");
		player.sendMessage("§e/kudong move §f<player> <서버> <X> <Y> <Z> : world 기본월드로 전송");
		player.sendMessage("§e/kudong move §f<player> <서버> <월드> <X> <Y> <Z>");
		player.sendMessage("§e/kudong move §f<player> <서버> <월드> <X> <Y> <Z> <YAW> <PITCH>");
		player.sendMessage("§e/kudong tp §f<target_player>");
		player.sendMessage("§e/kudong tp §f<player> <target_player>");
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

		if(FrameworkConfig.isBungeecord)
		{
			if((args[0].equalsIgnoreCase("move") || args[0].equalsIgnoreCase("이동")))
			{
					AldarLocation l = null;
					
					String username = args[1];
					UUID playeruuid = this.manager.findPlayer(username);
					
					if(playeruuid == null)
					{
						if(!isConsoleSender) player.sendMessage("§c플레이어<§e"+username+"§c>는 존재하지 않습니다!");
						else this.logger.log(Level.INFO, "플레이어<"+username+">는 존재하지 않습니다!");
						return true;
					}

					try
					{
						// /kudong move <player> <서버>
						if(args.length == 3)
						{
							l = new AldarLocation(args[2],"unknown",0,0,0);
						}
						// /kudong move <player> <서버> <X> <Y> <Z> : world 기본월드로 전송
						else if(args.length == 6)
						{
							l = new AldarLocation(args[2],"world",Double.parseDouble(args[3]),Double.parseDouble(args[4]),Double.parseDouble(args[5]));
						}
						// /kudong move <player> <서버> <월드> <X> <Y> <Z>
						else if(args.length == 7)
						{
							l = new AldarLocation(args[2],args[3],Double.parseDouble(args[4]),Double.parseDouble(args[5]),Double.parseDouble(args[6]));
						}
						// /kudong move <player> <서버> <월드> <X> <Y> <Z> <YAW> <PITCH>
						else if(args.length == 9)
						{
							l = new AldarLocation(args[2],args[3],Double.parseDouble(args[4]),Double.parseDouble(args[5]),Double.parseDouble(args[6]),Float.parseFloat(args[7]),Float.parseFloat(args[8]));
						}
					}
					catch(NumberFormatException e)
					{
						if(!isConsoleSender) player.sendMessage("§c숫자 입력이 잘못되었습니다 :-(");
						else this.logger.log(Level.INFO, "숫자 입력이 잘못되었습니다 :-(");
						return true;
					}

					if(l != null)
					{
						if(FrameworkConfig.server.equals(l.server))
						{
							Player pp = Bukkit.getPlayer(playeruuid);
							if(pp != null) pp.teleport(AldarLocationUtil.toBukkitLocation(l));
							return true;
						}
						this.sendTeleportCoordMessge(playeruuid ,username, l);
					}
					else
						if(!isConsoleSender)this.printHelpMessage(player);
				return true;
			}
			
			if((args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("서버")))
			{
				if(args.length == 1)
				{
					if(isConsoleSender) this.logger.log(Level.INFO, "현재서버는 <"+FrameworkConfig.server+"> 입니다.");
					else player.sendMessage("현재서버는 <"+FrameworkConfig.server+"> 입니다.");
					return true;
				}
				
				if(isConsoleSender)
				{
					this.logger.log(Level.INFO, "해당 명령어는 콘솔에서 수행할수 없습니다.");
					return true;
				}
				
				if(args.length == 2)
				{
					this.connectServer(player,args[1]);
				}
				else
				{
					this.printHelpMessage(player);
				}
			}
			
			if((args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("텔포")))
			{
				UUID playerUUID = null;
				UUID targetPlayerUUID = null;
				
				// /kudong tp <target_player>
				if(args.length == 2)
				{
					if(isConsoleSender) 
					{
						this.logger.log(Level.INFO, "해당 명령어는 콘솔에서 수행할수 없습니다.");
						return true;
					}

					String targetUsername = args[1];
					playerUUID = player.getUniqueId();
					targetPlayerUUID = this.manager.findPlayer(targetUsername);

					if(targetPlayerUUID == null)
					{
						player.sendMessage("§c해당 플레이어를 찾을수 없습니다.");
						return true;
					}
					
					Player pp = Bukkit.getPlayer(targetPlayerUUID);
					
					if(pp != null)
					{
						player.sendMessage("§6["+pp.getDisplayName()+"§6]님에게 텔레포트 되었습니다.");
						player.teleport(pp.getLocation());
						return true;
					}
					
					
					if(targetPlayerUUID.equals(playerUUID))
					{
						player.sendMessage("§c자기 자신은 텔레포트할수 없습니다!");
						return true;
					}
					
				}
				// /kudong tp <player> <target_player>
				else if(args.length == 3)
				{
					String username = args[1];
					String targetUsername = args[2];
					
					playerUUID = this.manager.findPlayer(username);
					targetPlayerUUID = this.manager.findPlayer(targetUsername);
					
					if(targetPlayerUUID == null || playerUUID == null)
					{
						if(!isConsoleSender) player.sendMessage("§c해당 플레이어를 찾을수 없습니다.");
						else this.logger.log(Level.INFO, "해당 플레이어를 찾을수 없습니다.");
						return true;
					}
				}
				
				if(targetPlayerUUID != null && playerUUID != null)
					this.sendTeleportPlayer(playerUUID, targetPlayerUUID);
				else
					if(!isConsoleSender)this.printHelpMessage(player);

				return true;
			}
		}
		else
		{
			if(!isConsoleSender) player.sendMessage("§cKUDONG-FRAMEWORK chat.yml에서 isBungeecord=true를 확인하세요");
			else this.logger.log(Level.INFO,"KUDONG-FRAMEWORK chat.yml에서 isBungeecord=true를 확인하세요");
		}

		return true;
	}
	
	public void sendTeleportPlayer(UUID uuid, UUID target)
	{
		Player dummyPlayer = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

		if(dummyPlayer == null)
		{
			this.logger.log(Level.INFO,"최소 1명의 플레이어가 서버에 접속해있어야 패킷 전달이 가능합니다.");
			return;
		}
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(ProtocolKey.TELEPORT_PLAYER);
		out.writeUTF(uuid.toString());
		out.writeUTF(target.toString());
		
		dummyPlayer.sendPluginMessage(this.plugin, ProtocolKey.MAIN_CHANNEL, out.toByteArray());
	}
	
	public void sendTeleportCoordMessge(UUID uuid, String username, AldarLocation loc)
	{
		Player dummyPlayer = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

		if(dummyPlayer == null)
		{
			this.logger.log(Level.INFO,"최소 1명의 플레이어가 서버에 접속해있어야 패킷 전달이 가능합니다.");
			return;
		}
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(ProtocolKey.TELEPORT_COORD);
		out.writeUTF(loc.serialize());
		out.writeUTF(uuid.toString());
		
		dummyPlayer.sendPluginMessage(this.plugin, ProtocolKey.MAIN_CHANNEL, out.toByteArray());

		out = ByteStreams.newDataOutput();
		out.writeUTF("ConnectOther");
		out.writeUTF(username);
		out.writeUTF(loc.server);
		
		dummyPlayer.sendPluginMessage(this.plugin, ProtocolKey.BUNGEE_CHANNEL, out.toByteArray());
	}
	
	public void connectServer(Player player, String server)
	{
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(server);
		player.sendPluginMessage(this.plugin, ProtocolKey.BUNGEE_CHANNEL, out.toByteArray());
	}
	
}
