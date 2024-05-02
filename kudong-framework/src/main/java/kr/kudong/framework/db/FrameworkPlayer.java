package kr.kudong.framework.db;

import java.util.UUID;

public class FrameworkPlayer
{
	private UUID uuid;
	private String username;
	
	
	public FrameworkPlayer(UUID uuid,String username)
	{
		this.uuid = uuid;
		this.username = username;
	}
	
	public UUID getUuid()
	{
		return uuid;
	}
	public void setUuid(UUID uuid)
	{
		this.uuid = uuid;
	}
	public String getUsername()
	{
		return username;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}
}
