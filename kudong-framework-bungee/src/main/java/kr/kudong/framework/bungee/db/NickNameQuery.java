package kr.kudong.framework.bungee.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

import kr.kudong.framework.bungee.FrameworkCore;
import net.md_5.bungee.api.ProxyServer;

public class NickNameQuery
{
	private UUID uuid;
	private String original;
	private String nickName;
	
	public NickNameQuery(UUID uuid,String original,String nickName)
	{
		this.uuid = uuid;
		this.original = original;
		this.nickName = nickName;
	}
	
	public UUID getUuid()
	{
		return uuid;
	}
	
	public String getOriginal()
	{
		return original;
	}
	
	public String getNickName()
	{
		return nickName;
	}
	
	public String getDisplayName()
	{
		return this.nickName != null ? this.nickName : this.original;
	}
	
	public static NickNameQuery getQuery(String nickName,String original)
	{
		NickNameQuery q = null;
		try 
		{
			PreparedStatement ps = FrameworkCore.dbAccess.query(SQLSchema.NickNameTable_Select_UUID);
			ps.setString(1, nickName);
			ps.setString(2, original);
			ps.execute();
			ResultSet rs = ps.getResultSet();
			while (rs.next())
			{
				UUID uuid = UUID.fromString(rs.getString(1));
				String ori = rs.getString(2);
				String nic = rs.getString(3);
				q = new NickNameQuery(uuid,ori,nic);
			}
			rs.close();
		} 
		catch (SQLException e1)
		{
			ProxyServer.getInstance().getLogger().log(Level.SEVERE, "SQLException 에러", e1);
		}		
		return q;
	}
	
	public static NickNameQuery getQuery(UUID uuid)
	{
		NickNameQuery q = null;
		try 
		{
			PreparedStatement ps = FrameworkCore.dbAccess.query(SQLSchema.NickNameTable_Select_Player);
			ps.setString(1, uuid.toString());
			ps.execute();
			ResultSet rs = ps.getResultSet();
			while (rs.next())
			{
				String ori = rs.getString(2);
				String nic = rs.getString(3);
				q = new NickNameQuery(uuid,ori,nic);
			}
			rs.close();
		} 
		catch (SQLException e1)
		{
			ProxyServer.getInstance().getLogger().log(Level.SEVERE, "SQLException 에러", e1);
		}		
		return q;
	}
	
}
