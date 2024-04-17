package kr.kudong.towny.dynmap.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
	private TownyAPI towny;
	private MarkerSet mark;
	private MarkerAPI markerapi;
	private DynmapCommonAPI dynmapApi;
	
	public TownyDynmapManager(Logger logger,JavaPlugin plugin,DynmapCommonAPI dynmapApi)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.towny = TownyAPI.INSTANCE;
		this.dynmapApi = dynmapApi;
		this.markerapi = this.dynmapApi.getMarkerAPI();
		this.mark = this.markerapi.createMarkerSet("towny", "towny", null , false);
	}
	
	public void loadTownyChunks()
	{
		for(OfflinePlayer p : Bukkit.getOfflinePlayers())
		{
			this.logger.log(Level.INFO, "플레이어 네임:"+ p.getName());
			
			UUID playerUUID = p.getUniqueId();
			List<List<Vector>> result = new ArrayList<>();
			
			List<TownyChunk> chunks = this.towny.getPlayerTownyChunks(playerUUID);

			this.logger.log(Level.INFO, "1 ");
			this.logger.log(Level.INFO, "청크사이즈 " + chunks.size());
			
			if(chunks.isEmpty()) continue;
			
			//=====================================
			
			List<Vertex> chunkList = new ArrayList<>(); 
			
			for(TownyChunk c : chunks)
			{
				int chunkX = c.getX();
				int chunkZ = c.getZ();
				
				if(!c.getWorldName().equals("world")) continue;
				
				chunkList.add(new Vertex(chunkX, chunkZ));
				this.logger.log(Level.INFO, "청크:" + chunkX + "/ "+chunkZ);
			}
			
			
			this.logger.log(Level.INFO, "2 ");
			//======================================
			//DFS
			
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

			
			this.logger.log(Level.INFO, "3 ");
			//=======================================
			for(List<Vertex> area :areaList)
			{
				this.logger.log(Level.INFO,"<area>");
				for(Vertex v : area)
				{
					this.logger.log(Level.INFO,"벡터:"+v.getX()+"/"+v.getZ());
				}
				
				this.logger.log(Level.INFO,"==========================");
			}
			
			
			
			for(List<Vertex> area :areaList)
			{
				List<Vertex> coordinate = new ArrayList<>();
				List<List<Vector>> checkPoly = new ArrayList<>();
				
				
				for(Vertex v : area)
				{
					int x = v.getX() * 16;
					int z = v.getZ() * 16;
					

					List<Vertex> offsetList = new ArrayList<>(); 
					
					offsetList.add(new Vertex(x , z - 16)); 	//v2
					offsetList.add(new Vertex(x + 16 ,z - 16)); //v1
					offsetList.add(new Vertex(x + 16, z)); 		//v4
					offsetList.add(new Vertex(x, z)); 			//v3
					
					List<Vector> offsetList2 = new ArrayList<>(); 
					offsetList2.add(new Vector(x ,0, z - 16)); 		//v2
					offsetList2.add(new Vector(x + 16 ,0,z - 16)); 	//v1
					offsetList2.add(new Vector(x + 16,0, z)); 		//v4
					offsetList2.add(new Vector(x,0, z)); 			//v3
					
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
				
				
				for(Vertex v : coordinate)
				{
					this.logger.log(Level.INFO,"coordinate 벡터:"+v.getX()+"/"+v.getZ() + "/ cnt="+v.getCnt());
				}
				this.logger.log(Level.INFO,"========>>>>");
				
				Vertex start = coordinate.get(0);
				Queue<Vertex> q = new LinkedList<>();
				List<Vertex> visited2 = new ArrayList<>(); 
				List<Vector> points = new ArrayList<>(); 
				
				
				q.add(start);
				visited2.add(start);
				points.add(new Vector(start.getX(),0,start.getZ()));
				
				
			    while(!q.isEmpty())
			    {
			    	Vertex v1 = q.poll();
			    	
			    	int cnt1 = v1.getCnt();
			    	
					int dx[] = new int[]{ 0,16,0,-16 };
					int dz[] = new int[]{ -16,0,16,0 };
					
			        for(int i=0 ; i<4 ; i++)
			        {
			            int nx = v1.getX() + dx[i];
			            int nz = v1.getZ() + dz[i];
			            
			            int findIndex = coordinate.indexOf(new Vertex(nx,nz));
						if(findIndex == -1) continue;
						else
						{
							Vertex v2 = coordinate.get(findIndex);
							int cnt2 = v2.getCnt();
							
							if(v1.equals(v2) || visited2.contains(v2)) continue;
							//
					    	if((2 <= cnt1 && cnt1 <=3) && (2 <= cnt2 && cnt2 <=3)) 
					    	{
					    		Vector vec = new Vector((v2.getX()-v1.getX())/2,0,(v2.getZ()-v1.getZ())/2);
					    		
					    		
					    		int count = 0;
					    		
					    		Vector vec1 = TownyDynmapMathUtil.rotateAroundAxisY(vec.clone(), 45);
					    		vec1.add(new Vector(v1.getX(),0,v1.getZ()));
					    		Vector vec2 = TownyDynmapMathUtil.rotateAroundAxisY(vec.clone(), -45);
					    		vec2.add(new Vector(v1.getX(),0,v1.getZ()));
					    		
					    		for(List<Vector> check : checkPoly)
					    		{
					    			if(TownyDynmapMathUtil.wn_PnPoly(vec1, check) == 1)
					    				count++;
					    			
					    			if(TownyDynmapMathUtil.wn_PnPoly(vec2, check) == 1)
					    				count++;
					    			
					    			if(count == 2) break;
					    		}
					    		
					    		if(count == 2) 
					    		{
					    			this.logger.log(Level.INFO,"2 2 겹치는거 2개");
					    			continue;
					    		}
					    		else
					    		{
					    			this.logger.log(Level.INFO,"2 2 겹치는거 1개");
						    		this.logger.log(Level.INFO,"points 벡터:"+v2.getX()+"/"+v2.getZ());
						    		q.add(v2);
						    		visited2.add(v2);
						    		points.add(new Vector(v2.getX(),0,v2.getZ()));
						    		break;
					    		}
					    	}
					    	else if((cnt1 >= 2 && cnt2 >= 2))
					    	{
					    		continue;
					    	}
					    	else
					    	{
					    		this.logger.log(Level.INFO,"points 벡터:"+v2.getX()+"/"+v2.getZ());
					    		q.add(v2);
					    		visited2.add(v2);
					    		points.add(new Vector(v2.getX(),0,v2.getZ()));
					    		break;
					    	}
						}
			        }

			    }
			    
			    TownyDynmapMathUtil.sortPoints(points);
			    
			    for(Vector v : points)
			    {
			    	this.logger.log(Level.INFO,"벡터:"+v.getX()+"/"+v.getZ());
			    	
			    }
			    
			    this.logger.log(Level.INFO,"========================");
			    
			    result.add(points);
			}
			
			Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(plugin, ()->{
				
				for(List<Vector> v : result)
				{
					DivisionAreaMarker marker = new DivisionAreaMarker(v, "world", mark, markerapi);
					marker.drawPaint();
				}
				
			}, 20L);
			
		}
	}
	
}
