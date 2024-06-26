package kr.kudong.towny.dynmap.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import kr.cosine.towny.api.TownyAPI;
import kr.cosine.towny.data.TownyChunk;
import kr.kudong.towny.dynmap.marker.DivisionAreaMarker;
import kr.kudong.towny.dynmap.marker.Vertex;
import kr.kudong.towny.dynmap.util.TownyDynmapMathUtil;


public class TownyDynmapManager
{
	private Logger logger;
	private JavaPlugin plugin;
	public TownyAPI towny;
	private MarkerSet mark;
	private MarkerAPI markerapi;
	private DynmapCommonAPI dynmapApi;
	private Map<UUID,TownyDynmapPlayer> map;
	
	public TownyDynmapManager(Logger logger,JavaPlugin plugin,DynmapCommonAPI dynmapApi)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.towny = TownyAPI.INSTANCE;
		this.dynmapApi = dynmapApi;
		this.markerapi = this.dynmapApi.getMarkerAPI();
		this.mark = this.markerapi.createMarkerSet("towny", "towny", null , false);
		this.map = new HashMap<>();
		
	}
	
	public boolean containsTownyDynmapPlayer(UUID uuid)
	{
		return this.map.containsKey(uuid);
	}
	
	public void addTownyDynmapPlayer(UUID uuid,TownyDynmapPlayer player)
	{
		this.map.put(uuid, player);
	}
	
	public void removeTownyDynmapPlayer(UUID uuid)
	{
		this.map.remove(uuid);
	}
	
	public TownyDynmapPlayer getTownyDynmapPlayer(UUID uuid)
	{
		return this.map.get(uuid);
	}
	
	public void loadTownyChunks()
	{
		this.logger.log(Level.INFO, "Start loading Towny Chunks in Dynmap....");
		long beforeTime = System.currentTimeMillis();
		for(OfflinePlayer p : Bukkit.getOfflinePlayers())
		{
			UUID playerUUID = p.getUniqueId();
			TownyDynmapPlayer player = new TownyDynmapPlayer(p.getName(),playerUUID);
			this.map.put(playerUUID, player);
			this.loadPlayerTownyChunks(player);
		}
		long afterTime = System.currentTimeMillis(); 
		long secDiffTime = afterTime - beforeTime; 
		this.logger.log(Level.INFO, "Load Complete Towny Chunks in Dynmap! (" + secDiffTime+"ms)");
		this.drawAllPlayerTownyChunks();
	}
	
	public void reloadTownyChunks()
	{
		for(TownyDynmapPlayer player : map.values())
		{
			player.clearMarker();
		}	
		this.map.clear();
		this.loadTownyChunks();
	}
	
	public void loadPlayerTownyChunks(TownyDynmapPlayer player)
	{
		List<TownyChunk> chunks = this.towny.getPlayerTownyChunks(player.getUuid());
		
		if(chunks.isEmpty()) return;
		
		for(World w : Bukkit.getWorlds())
		{
			String world = w.getName();
			List<Vertex> chunkList = this.convertChunktoVertex(chunks,world); 
			List<List<Vector>> result = this.calculatePlayerTownyChunks(chunkList,player.getUuid(),world);
			
			for(List<Vector> v : result)
			{
				DivisionAreaMarker marker = new DivisionAreaMarker(player.getName(),v, world, mark, markerapi);
				player.addMarker(marker);
			}
		}
	}
	
	private List<Vertex> convertChunktoVertex(List<TownyChunk> chunks, String world)
	{
		List<Vertex> chunkList = new ArrayList<>(); 
		
		for(TownyChunk c : chunks)
		{
			int chunkX = c.getX();
			int chunkZ = c.getZ();
			
			if(!c.getWorldName().equals(world)) continue;
			
			chunkList.add(new Vertex(chunkX, chunkZ));
			//this.logger.log(Level.INFO, "청크:" + chunkX + "/ "+chunkZ);
		}
		
		return chunkList;
	}

	public void drawAllPlayerTownyChunks()
	{
		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(plugin, ()->{
			
			for(TownyDynmapPlayer player : map.values())
			{
				player.drawPaint();
			}
			
		}, 20L);
	}
	
	public void drawPlayerTownyChunks(TownyDynmapPlayer player)
	{
		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(plugin, ()->{
			player.drawPaint();
		}, 5L);
	}
	
	/**
	 * 청크를 계산합니다.
	 * @param playerUUID
	 * @return
	 */
	private List<List<Vector>> calculatePlayerTownyChunks(List<Vertex> chunkList, UUID playerUUID, String world)
	{
		List<List<Vector>> result = new ArrayList<>();
		
		//======================================
		//각플레이어가 가지고있는 구역을 구분합니다.
		//붙어있는 땅은 하나로 통합시키는 과정이 포함됩니다.
		
		List<List<Vertex>> areaList = new ArrayList<>();
		List<Vertex> visited = new ArrayList<>(); 
		
		for(Vertex v : chunkList)
		{
			if(visited.contains(v)) continue;
			
			areaList.add(new ArrayList<>());
			List<Vertex> area = areaList.get(areaList.size()-1);
			
			Queue<Vertex> q = new LinkedList<>();
			area.add(v);
        	visited.add(v);
			q.add(v);
			
		    while(!q.isEmpty())
		    {
		    	Vertex vertex = q.poll();

				int chunkX = vertex.getX();
				int chunkZ = vertex.getZ();
		    	
				int dx[] = new int[]{ -1,0,1,0 };
				int dz[] = new int[]{ 0,1,0,-1 };
				
		        for(int i=0 ; i<4 ; i++)
		        {
		            int nx = chunkX + dx[i];
		            int nz = chunkZ + dz[i];
		            Vertex visit = new Vertex(nx,nz);
		            
		            //범위에 포함되지않는 좌표
		            if(!chunkList.contains(visit)) continue;
		            else if(visited.contains(visit)) continue;
		            else
		            {
		            	q.add(visit);
		            	area.add(visit);
		            	visited.add(visit);
		            }	
		        }
		    }
		}


		//===========================================================
		
		for(List<Vertex> area :areaList)
		{
			List<Vertex> coordinate = new ArrayList<>();
			List<List<Vector>> checkPoly = new ArrayList<>();

			// 겹치는 점들을 포함해서 각 점마다의 교점 갯수를 카운팅합니다.
			//==================================================
			
			Collections.sort(area);
			
			for(Vertex v : area)
			{
				int x = v.getX() * 16;
				int z = v.getZ() * 16;
				

				List<Vertex> offsetList = new ArrayList<>(); 
				
				offsetList.add(new Vertex(x, z)); 	
				offsetList.add(new Vertex(x + 16 ,z)); 
				offsetList.add(new Vertex(x + 16, z + 16)); 		
				offsetList.add(new Vertex(x, z + 16)); 			
				
				List<Vector> offsetList2 = new ArrayList<>(); 
				offsetList2.add(new Vector(x, 0, z)); 		
				offsetList2.add(new Vector(x + 16, 0, z)); 	
				offsetList2.add(new Vector(x + 16, 0, z + 16)); 		
				offsetList2.add(new Vector(x, 0, z + 16)); 			
				
				checkPoly.add(offsetList2);

				for(Vertex offset : offsetList)
				{
					int findIndex = coordinate.indexOf(offset);
					if(findIndex == -1)
						coordinate.add(offset);
					else
					{
						coordinate.get(findIndex).addCount();
					}	
				}
			}
			
			//=======================================================
			
			Vertex start = coordinate.get(0);
			Queue<Vertex> q = new LinkedList<>();
			List<Vertex> visited2 = new ArrayList<>(); 
			List<Vector> points = new ArrayList<>(); 
			
			//트리거를 투입
			q.add(start);
			visited2.add(start);
			points.add(new Vector(start.getX(),0,start.getZ()));
			
			//this.logger.log(Level.INFO, "스타트:"+start.getX()/16+"/"+start.getZ()/16);
			
		    while(!q.isEmpty())
		    {
		    	Vertex v1 = q.poll();
		    	
		    	//시계 방향으로 검사합니다 (위 오른쪽 아래 왼쪽 순)
				int dx[] = new int[]{ 0,16,0,-16 };
				int dz[] = new int[]{ -16,0,16,0 };
				
				List<Vertex> select = new ArrayList<>();
				
		        for(int i=0 ; i<4 ; i++)
		        {
		            int nx = v1.getX() + dx[i];
		            int nz = v1.getZ() + dz[i];
		            
		            int findIndex = coordinate.indexOf(new Vertex(nx,nz));
					if(findIndex == -1) continue;
					else
					{
						Vertex v2 = coordinate.get(findIndex);
						
						//도착지에 도달했으면 끝
						if(v2.equals(start)) break;
						
			    		Vector vec = new Vector((v2.getX()-v1.getX())/2,0,(v2.getZ()-v1.getZ())/2);
			    		
			    		int left = 0;
			    		int right = 0;
			    		
			    		//(v1,v2) 직선이 내부에 포함되어있는지 확인합니다.
			    		//vec 벡터의 절반을 45,-45로 회전시켜 판별합니다.
			    		Vector vec1 = TownyDynmapMathUtil.rotateAroundAxisY(vec.clone(), 45);
			    		vec1.add(new Vector(v1.getX(),0,v1.getZ()));
			    		Vector vec2 = TownyDynmapMathUtil.rotateAroundAxisY(vec.clone(), -45);
			    		vec2.add(new Vector(v1.getX(),0,v1.getZ()));
			    		
			    		for(List<Vector> check : checkPoly)
			    		{
			    			if(TownyDynmapMathUtil.wn_PnPoly(vec1, check) == 1)
			    				left = 1;
			    			
			    			if(TownyDynmapMathUtil.wn_PnPoly(vec2, check) == 1)
			    				right = 1;
			    		}
			    	
			    		if(left == 0 && right == 1) 
			    		{
			    			select.add(v2);
			    		}
					}
		        }
		        
		        for(Vertex v2 : select)
		        {
	    			if((select.size() == 2) && visited2.contains(v2)) continue;
			    	q.add(v2);
			    	visited2.add(v2);
			    	points.add(new Vector(v2.getX(),0,v2.getZ()));
			    	//this.logger.log(Level.INFO, "포인트:"+v2.getX()/16+"/"+v2.getZ()/16);
			    	break;
		        }
		    }
		    
		    //points를 시계방향으로 정렬합니다. 
		    TownyDynmapMathUtil.sortPoints(points);
		    result.add(points);
		    
		    //==================================================
		    
		    //정리
		    visited2.clear();
		    coordinate.clear();
		    
		    for(List<Vector> c1 : checkPoly)
		    	c1.clear();
		    

		}

	    chunkList.clear();
		
		visited.clear();
		
	    for(List<Vertex> c1 : areaList)
	    	c1.clear();
		
		return result;
		
	}
	
}
