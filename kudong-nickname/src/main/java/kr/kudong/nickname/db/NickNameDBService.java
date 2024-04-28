package kr.kudong.nickname.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.common.basic.db.DBAccess;
import kr.kudong.nickname.controller.NickNamePlayer;

public class NickNameDBService
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private DBAccess dbAccess;
	
	public NickNameDBService(Logger logger,JavaPlugin plugin,DBAccess dbAccess)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.dbAccess = dbAccess;
	}
	
	public List<NickNamePlayer> selectNickNameAllPlayer()
	{
		List<NickNamePlayer> list = new ArrayList<>();
		try 
		{
			PreparedStatement ps = dbAccess.query(SQLSchema.NickNameTable_Select);
			ps.execute();
			
			ResultSet rs = ps.getResultSet();
			while (rs.next())
			{
				UUID uuid = UUID.fromString(rs.getString(1));
				String originalName = rs.getString(2);
				String nickName = rs.getString(3);
				String alias = rs.getString(4);
				list.add(new NickNamePlayer(uuid,originalName,nickName,alias));
			}
			rs.close();
		} 
		catch (SQLException e1)
		{
			logger.log(Level.SEVERE, "SQLException 에러", e1);
		}		
		return list;
	}
	
	public NickNamePlayer selectNickNamePlayer(Player player)
	{
		UUID uuid = player.getUniqueId();
		NickNamePlayer nickPlayer = null;
		
		try 
		{
			PreparedStatement ps = dbAccess.query(SQLSchema.NickNameTable_Select_Player);
			ps.setString(1, uuid.toString());
			ps.execute();
			
			ResultSet rs = ps.getResultSet();
			while (rs.next())
			{
				String originalName = rs.getString(1);
				String nickName = rs.getString(2);
				String alias = rs.getString(3);
				nickPlayer = new NickNamePlayer(uuid,originalName,nickName,alias);
			}
			
			rs.close();
		} 
		catch (SQLException e1)
		{
			logger.log(Level.SEVERE, "SQLException 에러", e1);
		}		
		return nickPlayer;
	}
	
	public boolean insertNickNamePlayer(NickNamePlayer player)
	{
		UUID uuid = player.getUniqueID();
		String originalName = player.getOriginalName();
		String nickName = player.getNickName();
		String alias = player.getAlias();
		try // insert
		{
			PreparedStatement ps = dbAccess.query(SQLSchema.NickNameTable_Insert);
			ps.setString(1, uuid.toString());
			ps.setString(2, originalName);
			ps.setString(3, nickName);
			ps.setString(4, alias);
			ps.execute();
		} 
		catch (SQLException e1)
		{
			logger.log(Level.SEVERE, "SQLException 에러", e1);
			return false;
		}
		return true;
	}
	
	public void asyncUpdateNickName(UUID uuid,String nickname, Consumer<Boolean> callback)
	{
		this.dbAccess.asyncQuery((ps)->{
			try
			{
				ps.setString(1, nickname);
				ps.setString(2, uuid.toString());
				ps.execute();
			}
			catch(SQLException e)
			{
				logger.log(Level.SEVERE, "SQLException 에러", e);
				callback.accept(false);
				return false;
			}
			callback.accept(true);
			return true;
		}, SQLSchema.NickNameTable_Update_NickName);
	}
	
	public void asyncUpdateAlias(UUID uuid, String alias, Consumer<Boolean> callback)
	{
		this.dbAccess.asyncQuery((ps)->{
			try
			{
				ps.setString(1, alias);
				ps.setString(2, uuid.toString());
				ps.execute();
			}
			catch(SQLException e)
			{
				logger.log(Level.SEVERE, "SQLException 에러", e);
				callback.accept(false);
				return false;
			}
			callback.accept(true);
			return true;
		}, SQLSchema.NickNameTable_Update_Alias);		
	}

}
