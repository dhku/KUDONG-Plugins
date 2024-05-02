package kr.kudong.framework.comm;

import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataInput;

public class Message
{
	public String key;
	public Player player;
	public ByteArrayDataInput data;
	
	public Message(String key, Player player, ByteArrayDataInput data)
	{
		this.key = key;
		this.player = player;
		this.data = data;
	}

	
}
