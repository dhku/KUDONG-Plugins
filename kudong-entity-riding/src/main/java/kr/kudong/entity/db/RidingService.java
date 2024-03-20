package kr.kudong.entity.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.entity.RidingCore;
import kr.kudong.entity.controller.RidingManager;
import kr.kudong.entity.data.SteerablePreset;
import kr.kudong.entity.util.DBAccess;

public class RidingService
{
	private final Logger logger;
	private final JavaPlugin plugin;
	private RidingManager manager;
	private DBAccess dbManager;
	
	public RidingService(Logger logger,JavaPlugin plugin,DBAccess dbManager)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.manager = RidingCore.GetManager();
		this.dbManager = dbManager;
	}

	public List<SteerablePreset> selectRidingData(Player player)
	{
		UUID uuid = player.getUniqueId();
		
		Map<String, SteerablePreset> map = this.manager.getPresetMap();
		List<SteerablePreset> list = new ArrayList<>();
		
		try 
		{
			PreparedStatement ps = dbManager.query(SQLSchema.RidingTable_Select_Player);
			ps.setString(1, uuid.toString());
			ps.execute();
			
			ResultSet rs = ps.getResultSet();
			while (rs.next())
			{

				String presetname = rs.getString(1);
				
				if(map.containsKey(presetname))
				{
					list.add(map.get(presetname));
				}
			}
			rs.close();
		} 
		catch (SQLException e1)
		{
			logger.log(Level.SEVERE, "SQLException 에러", e1);
		}
		
		return list;
	}
	
	public void asyncInsertRidingData(Player player,SteerablePreset preset, Consumer<Boolean> callback)
	{
		UUID uuid = player.getUniqueId();
		dbManager.asyncQuery((ps)->{
			try
			{
				ps.setString(1, uuid.toString());
				ps.setString(2, preset.getPRESET_NAME());
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
		}, SQLSchema.RidingTable_Insert);
		
	}
	
	public void asyncDeleteRidingData(Player player,SteerablePreset preset, Consumer<Boolean> callback)
	{
		UUID uuid = player.getUniqueId();
		dbManager.asyncQuery((ps)->{
			try
			{
				ps.setString(1, uuid.toString());
				ps.setString(2, preset.getPRESET_NAME());
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
		}, SQLSchema.RidingTable_Delete);
	}
	
	
	public void deleteRidingData(Player player,SteerablePreset preset)
	{
		UUID uuid = player.getUniqueId();
		
		try // insert
		{
			PreparedStatement ps = dbManager.query(SQLSchema.RidingTable_Delete);
			ps.setString(1, uuid.toString());
			ps.setString(2, preset.getPRESET_NAME());
			ps.execute();
		} 
		catch (SQLException e1)
		{
			logger.log(Level.SEVERE, "SQLException 에러", e1);
		}

	}
	
	
	
	
	
}
