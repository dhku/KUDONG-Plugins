package kr.kudong.framework.bungee.comm;

import com.google.common.io.ByteArrayDataInput;

import net.md_5.bungee.api.connection.Connection;

public class Message
{
	public String key;
	public Connection player;
	public ByteArrayDataInput data;
	
	public Message(String key, Connection player, ByteArrayDataInput data)
	{
		this.key = key;
		this.player = player;
		this.data = data;
	}

	
}
