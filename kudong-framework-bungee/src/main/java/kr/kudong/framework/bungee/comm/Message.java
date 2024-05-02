package kr.kudong.framework.bungee.comm;

import com.google.common.io.ByteArrayDataInput;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Message
{
	public String key;
	public ProxiedPlayer player;
	public ByteArrayDataInput data;
	
	public Message(String key, ProxiedPlayer player, ByteArrayDataInput data)
	{
		this.key = key;
		this.player = player;
		this.data = data;
	}

	
}
