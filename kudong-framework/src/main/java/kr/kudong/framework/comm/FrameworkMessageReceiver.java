package kr.kudong.framework.comm;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import kr.kudong.common.basic.comm.ProtocolKey;
import kr.kudong.common.basic.util.AldarLocation;
import kr.kudong.common.paper.util.AldarLocationUtil;
import me.clip.placeholderapi.PlaceholderAPI;

public class FrameworkMessageReceiver implements PluginMessageListener
{
	private Logger logger;
	private JavaPlugin plugin;
	
	public FrameworkMessageReceiver(Logger logger,JavaPlugin plugin)
	{
		this.logger = logger;
		this.plugin = plugin;
	}
	
	public void receiveData(Message msg)
	{
		switch(msg.key)
		{
			case ProtocolKey.CHAT_MESSAGE:
				this.handleChatMessage(msg);
				break;
			case ProtocolKey.TELEPORT_COORD:
				this.handleTeleportCoord(msg);
				break;
			case ProtocolKey.TELEPORT_PLAYER:
				this.handleTeleportPlayer(msg);
				break;
		}
	}
	
	private void handleTeleportPlayer(Message msg)
	{
		UUID base = UUID.fromString(msg.data.readUTF());
		UUID target = UUID.fromString(msg.data.readUTF());
		
		Bukkit.getScheduler().runTaskLater(plugin, ()->{

			Player player = Bukkit.getServer().getPlayer(base);
			Player playerTarget = Bukkit.getServer().getPlayer(target);
			
			if(player != null && playerTarget != null)
			{
				player.teleport(playerTarget.getLocation());
			}
			else
				this.logger.log(Level.INFO,"텔레포트 실패 :-(");

		}, 5L);
		
	}

	private void handleChatMessage(Message msg)
	{
    	String id = msg.data.readUTF();
    	String name = msg.data.readUTF();
    	String chatMsg = msg.data.readUTF();

    	this.logger.log(Level.INFO, "출력>"+chatMsg);
    	
    	for(Player p : Bukkit.getServer().getOnlinePlayers())
    	{
    		p.sendMessage(chatMsg); 
    	}
	}
	
	private void receiveBungeeData(Message msg)
	{
		switch(msg.key)
		{

		}
		
	}

	private void handleTeleportCoord(Message msg)
	{
		String raw = msg.data.readUTF();
		UUID uuid = UUID.fromString(msg.data.readUTF());
		AldarLocation l = AldarLocation.deserialize(raw);
		this.logger.log(Level.INFO,"텔레포트 요청 수신");
		
		if(l.world.equals("unknown")) return;
		
		Bukkit.getScheduler().runTaskLater(plugin, ()->{

			Player player = Bukkit.getServer().getPlayer(uuid);
			if(player != null)
			{
				player.teleport(AldarLocationUtil.toBukkitLocation(l));
			}
			else
				this.logger.log(Level.INFO,"해당플레이어는 존재하지않아...");

		}, 5L);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) 
	{
		
	    ByteArrayDataInput in = ByteStreams.newDataInput(message);
	    String key = in.readUTF();
	    Message msg = new Message(key,player,in);
	    
	    this.logger.log(Level.INFO,"=====================");
	    this.logger.log(Level.INFO,"CHANNEL => "+channel);
	    this.logger.log(Level.INFO,"KEY => "+key);
	    this.logger.log(Level.INFO,"=====================");
	    
	    switch(channel)
		{
	    	case ProtocolKey.MAIN_CHANNEL:
	    		this.receiveData(msg);
	    		break;
	    	case ProtocolKey.BUNGEE_CHANNEL:
	    		this.receiveBungeeData(msg);
	    		break;
		}
	}

}
