package kr.kudong.framework.task;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import kr.kudong.common.basic.comm.ProtocolKey;
import kr.kudong.common.basic.util.AldarLocation;
import kr.kudong.common.paper.util.AldarLocationUtil;
import kr.kudong.framework.command.FrameworkCommand;
import kr.kudong.framework.controller.FrameworkConfig;
import kr.kudong.framework.db.NickNameResult;

public class PlayerMoveTask implements Runnable
{
	private final boolean isConsoleSender;
	private final Player player;
	private final String[] args;
	private final Logger logger;
	private final JavaPlugin plugin;
	
	public PlayerMoveTask(boolean isConsoleSender, Player player, String[] args,JavaPlugin plugin)
	{
		this.isConsoleSender = isConsoleSender;
		this.player = player;
		this.args = args;
		this.logger = Bukkit.getLogger();
		this.plugin = plugin;
	}

	@Override
	public void run()
	{
		if((args[0].equalsIgnoreCase("open") || args[0].equalsIgnoreCase("개방")) && args.length == 1)
		{
			this.openProxyServer(player);
			return;
		}
		
		if((args[0].equalsIgnoreCase("close") || args[0].equalsIgnoreCase("폐쇄")) && args.length == 1)
		{
			this.closeProxyServer(player);
			return;
		}
		
		if((args[0].equalsIgnoreCase("tab") || args[0].equalsIgnoreCase("탭")) && args.length == 1)
		{
			this.reloadPlayerlistTab(player);
			return;
		}

		if((args[0].equalsIgnoreCase("move") || args[0].equalsIgnoreCase("이동")))
		{
				AldarLocation l = null;
				
				String username = args[1];
				
				NickNameResult result = NickNameResult.sendQuery(username);
			
				if(result == null)
				{
					if(!isConsoleSender) player.sendMessage("§c플레이어<§e"+username+"§c>는 존재하지 않습니다!");
					else this.logger.log(Level.INFO, "플레이어<"+username+">는 존재하지 않습니다!");
					return;
				}
				
				UUID playeruuid = result.getUuid();
				
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
					return;
				}

				if(l != null)
				{
					if(FrameworkConfig.server.equals(l.server))
					{
						if(l.world.equals("unknown")) return;
						
						final AldarLocation l2 = l;
						
						Bukkit.getScheduler().runTask(plugin, ()->{
							Player pp = Bukkit.getPlayer(playeruuid);
							if(pp != null) pp.teleport(AldarLocationUtil.toBukkitLocation(l2));
						});
						
						return;
					}
					
					this.sendTeleportCoordMessge(playeruuid ,result.getOriginal(), l);
				}
				else
					if(!isConsoleSender)FrameworkCommand.printHelpMessage(player);
			return;
		}
		
		if((args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("서버")))
		{
			if(args.length == 1)
			{
				if(isConsoleSender) this.logger.log(Level.INFO, "현재서버는 <"+FrameworkConfig.server+"> 입니다.");
				else player.sendMessage("현재서버는 <"+FrameworkConfig.server+"> 입니다.");
				return;
			}
			
			if(isConsoleSender)
			{
				this.logger.log(Level.INFO, "해당 명령어는 콘솔에서 수행할수 없습니다.");
				return;
			}
			
			if(args.length == 2)
			{
				this.connectServer(player,args[1]);
			}
			else
			{
				FrameworkCommand.printHelpMessage(player);
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
					return;
				}

				String targetUsername = args[1];
				playerUUID = player.getUniqueId();
				NickNameResult result = NickNameResult.sendQuery(targetUsername);
				
				if(result == null)
				{
					player.sendMessage("§c해당 플레이어를 찾을수 없습니다.");
					return;
				}
				
				targetPlayerUUID = result.getUuid();
				Player pp = Bukkit.getPlayer(targetPlayerUUID);
				
				if(targetPlayerUUID.equals(playerUUID))
				{
					player.sendMessage("§c자기 자신은 텔레포트할수 없습니다!");
					return;
				}
				
				if(pp != null)
				{
					Bukkit.getScheduler().runTask(plugin, ()->{
						player.sendMessage("§6["+pp.getDisplayName()+"§6]님에게 텔레포트 되었습니다.");
						player.teleport(pp.getLocation());
					});
					return;
				}
			}
			// /kudong tp <player> <target_player>
			else if(args.length == 3)
			{
				String username = args[1];
				String targetUsername = args[2];
				
				NickNameResult r1 = NickNameResult.sendQuery(username);
				NickNameResult r2 = NickNameResult.sendQuery(targetUsername);
				
				if(r1 == null || r2 == null)
				{
					if(!isConsoleSender) player.sendMessage("§c해당 플레이어를 찾을수 없습니다.");
					else this.logger.log(Level.INFO, "해당 플레이어를 찾을수 없습니다.");
					return;
				}
				
				playerUUID = r1.getUuid();
				targetPlayerUUID = r2.getUuid();
			}
			
			if(targetPlayerUUID != null && playerUUID != null)
				this.sendTeleportPlayer(playerUUID, targetPlayerUUID);
			else
				if(!isConsoleSender)FrameworkCommand.printHelpMessage(player);

			return;
		}
		
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
	
	public void openProxyServer(Player player)
	{
		Player dummyPlayer = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

		if(dummyPlayer == null)
		{
			this.logger.log(Level.INFO,"최소 1명의 플레이어가 서버에 접속해있어야 패킷 전달이 가능합니다.");
			return;
		}
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(ProtocolKey.OPEN_PROXY_SERVER);
		out.writeUTF(player.getUniqueId().toString());
		dummyPlayer.sendPluginMessage(this.plugin, ProtocolKey.MAIN_CHANNEL, out.toByteArray());
	}
	
	public void closeProxyServer(Player player)
	{
		Player dummyPlayer = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

		if(dummyPlayer == null)
		{
			this.logger.log(Level.INFO,"최소 1명의 플레이어가 서버에 접속해있어야 패킷 전달이 가능합니다.");
			return;
		}
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(ProtocolKey.CLOSE_PROXY_SERVER);
		out.writeUTF(player.getUniqueId().toString());
		dummyPlayer.sendPluginMessage(this.plugin, ProtocolKey.MAIN_CHANNEL, out.toByteArray());
	}
	
	
	private void reloadPlayerlistTab(Player player)
	{
		Player dummyPlayer = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

		if(dummyPlayer == null)
		{
			this.logger.log(Level.INFO,"최소 1명의 플레이어가 서버에 접속해있어야 패킷 전달이 가능합니다.");
			return;
		}
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(ProtocolKey.TAB_RELOAD);
		out.writeUTF(player.getUniqueId().toString());
		dummyPlayer.sendPluginMessage(this.plugin, ProtocolKey.MAIN_CHANNEL, out.toByteArray());
	}
	
}
