package kr.kudong.framework.bungee.comm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import kr.kudong.common.basic.comm.ProtocolKey;
import kr.kudong.common.basic.util.AldarLocation;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class FrameworkMessageReceiver implements Listener
{
	
	private Logger logger;
	private Map<UUID,String> map;
	
	public FrameworkMessageReceiver(Logger logger)
	{
		this.logger = logger;
		this.map = new HashMap<>();
	}

	/**
	 * 마인크래프트 서버 => 번지 도착 메세지를 수신합니다.
	 * @param msg
	 */
	public void receiveData(Message msg)
	{
        switch(msg.key)
        {
        	case ProtocolKey.TELEPORT_COORD:
        		this.handleTeleportCoordMessage(msg);
                break;
			case ProtocolKey.CHAT_MESSAGE:
				//다른 서버로 브로드캐스팅
				this.handleChatMessage(msg);
				break;    
        }

	}
	
	/**
	 * ServerSwitchEvnt가 불리기전에 HashMap 값을 저장합니다.
	 * @param msg
	 */
    private void handleTeleportCoordMessage(Message msg)
	{
    	ProxiedPlayer p = msg.player;
    	String loc = msg.data.readUTF();
    	AldarLocation l = AldarLocation.deserialize(loc);
    	
    	ServerInfo info = ProxyServer.getInstance().getServerInfo(l.server);
    	
    	if(info != null)
    	{
    		InetSocketAddress addr = info.getAddress();
    		
    		Socket s = new Socket();
    		try
			{
				s.connect(new InetSocketAddress(addr.getAddress(), addr.getPort()), 10); //good timeout is 10-20
				s.close();
			}
			catch(Exception e)
			{
				this.logger.log(Level.INFO,"서버 <"+l.server+">가 존재하지 않거나 오프라인으로 텔레포트 요청이 거부되었습니다.");
				return;
			}
    		map.put(p.getUniqueId(), loc);
    	}
	}
    
	/**
	 * ServerSwitchEvnt : 플레이어가 마인크래프트 서버에 접속한후에 불립니다.
	 * ServerSwitchEvnt에서 HashMap을 사용합니다.
	 * @param msg
	 */
    @EventHandler
    public void onTeleport(ServerSwitchEvent event)
    {
    	ProxiedPlayer p = event.getPlayer();
    	UUID uuid = p.getUniqueId();
    	
    	//TeleportCoord
    	if(map.containsKey(p.getUniqueId()))
    	{
    		//번지 => 서버
    		this.logger.log(Level.INFO,"ServerSwitchEvent");
    		ByteArrayDataOutput out = ByteStreams.newDataOutput();
    		out.writeUTF(ProtocolKey.TELEPORT_COORD);
    		out.writeUTF(map.get(uuid));
    		out.writeUTF(uuid.toString());
            p.getServer().getInfo().sendData(ProtocolKey.MAIN_CHANNEL,out.toByteArray());
            map.remove(uuid);
    	}
    }
 
    /**
     * 다른 서버로 브로드 캐스팅 합니다.
     * <주의> 마크 서버에 최소 1명의 플레이어가 존재해야지만 패킷이 전달됩니다!!!
     * @param msg
     */
	private void handleChatMessage(Message msg)
	{
    	ProxiedPlayer player = msg.player;
    	String senderServer = player.getServer().getInfo().getName();
    	
    	String id = msg.data.readUTF();
    	String name = msg.data.readUTF();
    	String chatMsg = msg.data.readUTF();
    	
    	this.logger.log(Level.INFO, "chatMsg relay>"+chatMsg);
    	
		for(ServerInfo info : ProxyServer.getInstance().getServers().values())
		{
			if(senderServer.equals(info.getName())) continue;
			this.logger.log(Level.INFO, "전송서버 = > "+info.getName());
			this.sendChatMessage(info, id , name , chatMsg);
		}
	}
    
	public void sendChatMessage(ServerInfo info, String id , String name, String chatMsg)
	{
	    Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();
	    if ( networkPlayers == null || networkPlayers.isEmpty() ) return;
	    
	    ByteArrayDataOutput out = ByteStreams.newDataOutput();
	    out.writeUTF( ProtocolKey.CHAT_MESSAGE ); 
	    out.writeUTF( id ); 
	    out.writeUTF( name );
	    out.writeUTF( chatMsg );
	    
	    ProxiedPlayer p = Iterables.getFirst(info.getPlayers(), null);
	    if(p != null)
	    {
	    	this.logger.log(Level.INFO, "전송합니다");
	    	p.getServer().getInfo().sendData( ProtocolKey.MAIN_CHANNEL , out.toByteArray());
	    }
	    else
	    {
	    	info.sendData( ProtocolKey.MAIN_CHANNEL , out.toByteArray());
	    }
	}

	@EventHandler
    public void on(PluginMessageEvent event)
    {
    	
	    this.logger.log(Level.INFO,"=====================");
	    this.logger.log(Level.INFO,"CHANNEL => "+event.getTag());

        if (!event.getTag().equalsIgnoreCase( ProtocolKey.MAIN_CHANNEL)) return;
        ByteArrayDataInput in = ByteStreams.newDataInput( event.getData() );
        String key = in.readUTF();
        
	    this.logger.log(Level.INFO,"KEY => "+key);
	    this.logger.log(Level.INFO,"=====================");
        
        if ( event.getReceiver() instanceof ProxiedPlayer )
        {
            ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
            Message msg = new Message(key,player,in);
            this.receiveData(msg);
        }
        else if(event.getReceiver() instanceof Server )
        {
          Server receiver = (Server) event.getReceiver();
          this.logger.log(Level.INFO,"서버 리시버 호출");
        }	

    }
    
//    // the receiver is a server when the proxy talks to a server
//    if ( event.getReceiver() instanceof Server )
//    {
//        Server receiver = (Server) event.getReceiver();
//        this.logger.log(Level.INFO,"2222222 => " + receiver.getInfo().getName());
//    }

}
