package kr.kudong.framework.bungee;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import kr.kudong.common.basic.comm.ProtocolKey;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class FrameworkCoreListener implements Listener
{
	private Logger logger;
	
	public FrameworkCoreListener(Logger log)
	{
		this.logger = log;
	}
	

	
//	@EventHandler
//	public void onChat(ChatEvent e)
//	{
//		ProxiedPlayer player = (ProxiedPlayer)e.getSender();
//		String senderServer = player.getServer().getInfo().getName();
//		String chatMsg = e.getMessage();
//		if (chatMsg.charAt(0) == '/')
//		{
//			this.logger.log(Level.INFO, "리턴");
//			return;
//		}
//			
//		for(ServerInfo info : ProxyServer.getInstance().getServers().values())
//		{
//			if(senderServer.equals(info.getName())) continue;
//			this.logger.log(Level.INFO, "전송서버 = > "+info.getName());
//			this.sendChatMessage(info, player, chatMsg);
//		}
//	}
	
	
//	public void sendCustomData(ProxiedPlayer player, String data1, int data2)
//	{
//	    Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();
//	    // perform a check to see if globally are no players
//	    if ( networkPlayers == null || networkPlayers.isEmpty() )
//	    {
//	        return;
//	    }
//	    ByteArrayDataOutput out = ByteStreams.newDataOutput();
//	    out.writeUTF( "MySubChannel" ); // the channel could be whatever you want
//	    out.writeUTF( data1 ); // this data could be whatever you want
//	    out.writeInt( data2 ); // this data could be whatever you want
//	 
//	    // we send the data to the server
//	    // using ServerInfo the packet is being queued if there are no players in the server
//	    // using only the server to send data the packet will be lost if no players are in it
//	    player.getServer().getInfo().sendData( "kudong:channel", out.toByteArray() );
//	}
	
}
