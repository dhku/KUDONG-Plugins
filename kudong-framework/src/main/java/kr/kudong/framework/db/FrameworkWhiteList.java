package kr.kudong.framework.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import kr.kudong.framework.FrameworkCore;

public class FrameworkWhiteList
{
	public static boolean insertWhiteList(String username,UUID uuid)
	{
		try 
		{
			PreparedStatement ps = FrameworkCore.dbAccess.query(SQLSchema.NickNameTable_Insert_WhiteList);
			ps.setString(1, uuid.toString());
			ps.setString(2, username);
			ps.execute();
		} 
		catch (SQLException e1)
		{
			Bukkit.getServer().getLogger().log(Level.SEVERE, "SQLException 에러", e1);
			return false;
		}		
		return true;
	}
	
	public static boolean deleteWhiteList(UUID uuid)
	{
		try 
		{
			PreparedStatement ps = FrameworkCore.dbAccess.query(SQLSchema.NickNameTable_Delete_WhiteList);
			ps.setString(1, uuid.toString());
			ps.execute();
		} 
		catch (SQLException e1)
		{
			Bukkit.getServer().getLogger().log(Level.SEVERE, "SQLException 에러", e1);
			return false;
		}		
		return true;
	}
}
