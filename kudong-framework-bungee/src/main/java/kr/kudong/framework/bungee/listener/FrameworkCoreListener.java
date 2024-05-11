package kr.kudong.framework.bungee.listener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import kr.kudong.common.basic.comm.MojangAPIUtil;
import kr.kudong.common.basic.comm.ProtocolKey;
import kr.kudong.common.basic.db.DBAccess;
import kr.kudong.framework.bungee.comm.FrameworkConfig;
import kr.kudong.framework.bungee.db.NickNameQuery;
import kr.kudong.framework.bungee.db.SQLSchema;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.event.player.PlayerLoadEvent;
import me.neznamy.tab.api.event.plugin.PlaceholderRegisterEvent;
import me.neznamy.tab.api.tablist.TabListFormatManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class FrameworkCoreListener implements Listener
{
	private Logger logger;
	private Plugin plugin;
	private DBAccess db;
	private TabListFormatManager manager;
	private Map<String,NickNameQuery> map;
	
	public FrameworkCoreListener(Logger log,Plugin plugin,DBAccess db)
	{
		this.logger = log;
		this.db = db;
		this.plugin = plugin;
		this.map = new HashMap<>();
		this.manager = TabAPI.getInstance().getTabListFormatManager();
		this.registerTabEvent();
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
	
	private void registerTabEvent()
	{
		TabAPI.getInstance().getEventBus().register(PlayerLoadEvent.class, event -> {
		    TabPlayer tabPlayer = event.getPlayer();

		    db.asyncQuery((ps)->{
		    	String nickName = null;
				try
				{
					ps.setString(1, tabPlayer.getUniqueId().toString());
					ps.execute();
					
					ResultSet rs = ps.getResultSet();
					while (rs.next())
					{
						nickName = rs.getString(3);
					}
					
					rs.close();
				}
				catch(SQLException e)
				{
					logger.log(Level.SEVERE, "SQLException 에러", e);
					return false;
				}
				if(nickName != null)manager.setName(tabPlayer, nickName);
				manager.setPrefix(tabPlayer, "%arcprefix_prefix%");
		    	return true;
		    }, SQLSchema.NickNameTable_Select_Player);
		});
	}
	
    @EventHandler
    public void onLogin(PreLoginEvent event) 
    {
//    	UUID uuid = MojangAPIUtil.getUUIDfromMojangAPI(event.getConnection().getName());
    	String username = event.getConnection().getName();
    	NickNameQuery q = NickNameQuery.getQuery(username);
    	
    	if(FrameworkConfig.isMaintenenceMode)
    	{
    		if(q != null && q.getAlias() != null && q.getAlias().equals("op"))
    		{
    			map.put(username, q);
    		}
    		else
    		{
    			BaseComponent reason = new TextComponent("현재 메인테넌스 작업중입니다. 이용에 불편을 끼쳐드려 양해부탁드립니다.");
    			event.setReason(reason);
    			event.setCancelled(true);
    		}
    	}
    	else
    	{
    		map.put(username, q);
    	}
    }
	
    @EventHandler
    public void onPostLogin(PostLoginEvent event) 
    {
    	ProxiedPlayer player = event.getPlayer();
    
    	if(map.containsKey(player.getName()))
    	{
    		NickNameQuery q = this.map.get(player.getName());
    		
			String format = "[§2+§f] §b{player}";
			
			if(q != null)
				format = format.replace("{player}", q.getDisplayName());
			else
				format = format.replace("{player}", player.getName());
			
			BaseComponent r1 = new TextComponent(format);
			ProxyServer.getInstance().broadcast(r1);
    			
    		map.remove(player.getName());	
    	}

//		ProxyServer.getInstance().getScheduler().schedule(this.plugin, ()->{
//			
//			TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
//			if(tabPlayer != null) manager.setName(tabPlayer, name);
//			
//		}, 3L , TimeUnit.SECONDS);
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
