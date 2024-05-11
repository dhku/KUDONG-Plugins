package kr.kudong.framework.listener;

import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import kr.kudong.common.basic.comm.ProtocolKey;
import kr.kudong.framework.controller.FrameworkConfig;
import kr.kudong.framework.controller.FrameworkManager;
import kr.kudong.framework.controller.MessageConfig;
import kr.kudong.framework.db.NickNameResult;

public class FrameworkListener implements Listener
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private final FrameworkManager manager;
	
	public FrameworkListener(Logger logger, JavaPlugin plugin, FrameworkManager manager)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.manager = manager;
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e)
	{
		Player player = e.getPlayer();
		
		NickNameResult result = NickNameResult.sendQuery(player.getUniqueId());
		
		if(result == null)
		{
			if(FrameworkConfig.isBungeecord)
			{
				sendBroadcastPlayer("whitelistkick", player.getName());
			}
			
			e.disallow(Result.KICK_WHITELIST, MessageConfig.Message_Not_Whitelist);
		}
		else
		{
			FrameworkManager.joinCache.put(player.getUniqueId(), result);
		}	
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
		FrameworkManager.joinCache.remove(player.getUniqueId());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		String cmd = event.getMessage();
		
//		this.logger.log(Level.INFO, "EventPriority.HIGHEST cmd>" +cmd);

		if(!player.hasPermission("kudong.admin")) return;

		if(FrameworkConfig.isBungeecord)
		{
			if(cmd.matches("/kick (.*)"))
			{
				sendPlayerCommand(uuid,cmd);
			}
			else if(cmd.matches("/ban (.*)"))
			{
				sendPlayerCommand(uuid,cmd);
			}
		}
	}
	
	public void sendPlayerCommand(UUID sender,String cmd)
	{
		Player dummyPlayer = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

		if(dummyPlayer == null)
		{
			this.logger.log(Level.INFO,"최소 1명의 플레이어가 서버에 접속해있어야 패킷 전달이 가능합니다.");
			return;
		}
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(ProtocolKey.COMMAND_PLAYER);
		out.writeUTF(sender.toString()); //format
		out.writeUTF(cmd); //format

		dummyPlayer.sendPluginMessage(this.plugin, ProtocolKey.MAIN_CHANNEL, out.toByteArray());
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
