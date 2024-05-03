package kr.kudong.framework.bungee.listener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import kr.kudong.common.basic.comm.ProtocolKey;
import kr.kudong.common.basic.db.DBAccess;
import kr.kudong.framework.bungee.db.SQLSchema;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class FrameworkCoreListener implements Listener
{
	private Logger logger;
	private DBAccess db;
	
	public FrameworkCoreListener(Logger log,DBAccess db)
	{
		this.logger = log;
		this.db = db;
	}
	
	public String getNickName(UUID uuid)
	{
		String nickName = null;
		try 
		{
			PreparedStatement ps = db.query(SQLSchema.NickNameTable_Select_Player);
			ps.setString(1, uuid.toString());
			ps.execute();
			
			ResultSet rs = ps.getResultSet();
			while (rs.next())
			{
				nickName = rs.getString(3);
			}
			
			rs.close();
		} 
		catch (SQLException e1)
		{
			logger.log(Level.SEVERE, "SQLException 에러", e1);
		}		
		return nickName;
	}
	
    @EventHandler
    public void onPostLogin(PostLoginEvent event) 
    {
    	ProxiedPlayer player = event.getPlayer();
    	String nickName = this.getNickName(player.getUniqueId());
		String format = "[§2+§f] §b{player}";
		format = format.replace("{player}", nickName != null ? nickName : player.getName());
		BaseComponent r1 = new TextComponent(format);
		ProxyServer.getInstance().broadcast(r1);
    }
	
	@EventHandler
	public void onChat(PlayerDisconnectEvent e)
	{
		ProxiedPlayer player = e.getPlayer();
		String format = "[§4-§f] §b{player}";
		String nickName = this.getNickName(player.getUniqueId());
		format = format.replace("{player}", nickName != null ? nickName : player.getName());
		BaseComponent r1 = new TextComponent(format);
		ProxyServer.getInstance().broadcast(r1);
		
//		for(ProxiedPlayer p :ProxyServer.getInstance().getPlayers())
//		{
//			player.sendMessage(r1);
//		}
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
