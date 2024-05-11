package kr.kudong.framework.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import kr.kudong.framework.FrameworkCore;

public class NickNameResult
{
	private UUID uuid;
	private String original;
	private String nickName;
	
	public NickNameResult(UUID uuid,String original,String nickName)
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
	
	public static NickNameResult sendQuery(String nickName,String original)
	{
		NickNameResult q = null;
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
				q = new NickNameResult(uuid,ori,nic);
			}
			rs.close();
		} 
		catch (SQLException e1)
		{
			Bukkit.getServer().getLogger().log(Level.SEVERE, "SQLException 에러", e1);
		}		
		return q;
	}
	
	public static NickNameResult sendQuery(String name)
	{
		return sendQuery(name,name);
	}
	
	public static NickNameResult sendQuery(UUID uuid)
	{
		NickNameResult q = null;
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
				q = new NickNameResult(uuid,ori,nic);
			}
			rs.close();
		} 
		catch (SQLException e1)
		{
			Bukkit.getServer().getLogger().log(Level.SEVERE, "SQLException 에러", e1);
		}		
		return q;
	}
	
}
