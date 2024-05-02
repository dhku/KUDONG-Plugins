package kr.kudong.framework.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.common.basic.db.DBAccess;

public class FrameworkDBService
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private DBAccess dbAccess;
	
	public FrameworkDBService(Logger logger,JavaPlugin plugin,DBAccess dbAccess)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.dbAccess = dbAccess;
	}
	
	public boolean insertPlayerData(Player player)
	{
		UUID uuid = player.getUniqueId();
		String username = player.getName();

		try // insert
		{
			PreparedStatement ps = dbAccess.query(SQLSchema.FrameworkPlayerTable_Insert);
			ps.setString(1, uuid.toString());
			ps.setString(2, username);
			ps.execute();
		} 
		catch (SQLException e1)
		{
			logger.log(Level.SEVERE, "SQLException 에러", e1);
			return false;
		}
		return true;
	}
	
	public List<FrameworkPlayer> selectAllPlayer()
	{
		List<FrameworkPlayer> list = new ArrayList<>();
		try 
		{
			PreparedStatement ps = dbAccess.query(SQLSchema.FrameworkPlayerTable_Select);
			ps.execute();
			
			ResultSet rs = ps.getResultSet();
			while (rs.next())
			{
				UUID uuid = UUID.fromString(rs.getString(1));
				String username = rs.getString(2);
				list.add(new FrameworkPlayer(uuid,username));
			}
			rs.close();
		} 
		catch (SQLException e1)
		{
			logger.log(Level.SEVERE, "SQLException 에러", e1);
		}		
		return list;
	}
	
	public String getUsernameInPlayerData(UUID uuid)
	{
		String username = null;
		
		try 
		{
			PreparedStatement ps = dbAccess.query(SQLSchema.FrameworkPlayerTable_Select_Player);
			ps.setString(1, uuid.toString());
			ps.execute();
			
			ResultSet rs = ps.getResultSet();
			while (rs.next())
			{
				username = rs.getString(1);

			}
			rs.close();
		} 
		catch (SQLException e1)
		{
			logger.log(Level.SEVERE, "SQLException 에러", e1);
		}		
		return username;
	}
	
	public UUID getUsernameInPlayerData(String username)
	{
		UUID uuid = null;
		
		try 
		{
			PreparedStatement ps = dbAccess.query(SQLSchema.FrameworkPlayerTable_Select_Player_UUID);
			ps.setString(1, username);
			ps.execute();
			
			ResultSet rs = ps.getResultSet();
			while (rs.next())
			{
				String raw = rs.getString(1);
				
				if(raw != null)
					uuid = UUID.fromString(raw);

			}
			rs.close();
		} 
		catch (SQLException e1)
		{
			logger.log(Level.SEVERE, "SQLException 에러", e1);
		}		
		return uuid;
	}

}
