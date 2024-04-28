package kr.kudong.nickname.controller;

import java.util.UUID;
import org.bukkit.entity.Player;

public class NickNamePlayer
{
	private UUID uuid;
	private String originalName;
	private String nickName;
	private String alias;
	private Player player;
	private boolean hasNickname;

	public NickNamePlayer(UUID uuid, String originalName, String nickName, String alias)
	{
		this.uuid = uuid;
		this.originalName = originalName;
		this.nickName = nickName;
		this.alias = alias;
		if(this.nickName == null) hasNickname = false;
		else hasNickname = true;
	}
	
	public String getOriginalName()
	{
		return originalName;
	}

	public void setOriginalName(String originalName)
	{
		this.originalName = originalName;
	}
	
	public String getNickName()
	{
		return nickName;
	}
	
	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}

	public String getAlias()
	{
		return alias;
	}

	public void setAlias(String alias)
	{
		this.alias = alias;
	}
	
	public UUID getUniqueID()
	{
		return this.uuid;
	}
	
	public void setBukkitPlayer(Player player)
	{
		this.player = player;
	}
	
	public Player getBukkitPlayer()
	{
		return this.player;
	}

	public boolean hasNickname()
	{
		return hasNickname;
	}
	
	public void setHasNickname(boolean hasNickname)
	{
		this.hasNickname = hasNickname;
	}
	
	public boolean isOnline()
	{
		if(this.player == null)
			return false;
		else
			return true;
	}
	
	
}
