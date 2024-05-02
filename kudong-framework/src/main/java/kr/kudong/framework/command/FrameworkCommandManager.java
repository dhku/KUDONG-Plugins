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
import kr.kudong.framework.chat.ChatConfig;
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

		UUID uuid = player.getUniqueId();

		if(args.length == 0)
		{

			return true;
		}
		
		if(ChatConfig.isBungeecord)
		{
			if((args[0].equalsIgnoreCase("move") || args[0].equalsIgnoreCase("이동")))
			{

					AldarLocation l = null;
					
					String username = args[1];
					UUID playeruuid = this.manager.findPlayer(username);
					
					if(playeruuid == null)
					{
						if(!isConsoleSender) player.sendMessage("플레이어<"+username+">는 존재하지 않습니다!");
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
						if(!isConsoleSender) player.sendMessage("숫자 입력이 잘못되었습니다 :-(");
						return true;
					}

					if(l != null)
						this.sendTeleportCoordMessge(playeruuid ,username, l);

				
				return true;
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
						player.sendMessage("해당 명령어는 콘솔에서 수행할수 없습니다.");
						return true;
					}

					String targetUsername = args[1];
					playerUUID = player.getUniqueId();
					targetPlayerUUID = this.manager.findPlayer(targetUsername);
					
					if(targetPlayerUUID == null)
					{
						//실패
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
						//실패
						return true;
					}
				}

				return true;
			}

		}
		else
		{
			if(!isConsoleSender) player.sendMessage("KUDONG-FRAMEWORK chat.yml에서 isBungeecord=true를 확인하세요");
			else this.logger.log(Level.INFO,"KUDONG-FRAMEWORK chat.yml에서 isBungeecord=true를 확인하세요");
		}
		



		return true;
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
	
}
