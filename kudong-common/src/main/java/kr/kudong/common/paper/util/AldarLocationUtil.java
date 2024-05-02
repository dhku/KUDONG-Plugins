package kr.kudong.common.paper.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import kr.kudong.common.basic.util.AldarLocation;

public class AldarLocationUtil
{
	
	public static Location toBukkitLocation (AldarLocation loc) 
	{

		World w = Bukkit.getWorld(loc.world);
		return new Location(w,loc.x,loc.y,loc.z,loc.yaw,loc.pitch);
	}
	
	public static Vector toVector (AldarLocation loc) 
	{
		return new Vector(loc.x,loc.y,loc.z);
	}
	
	
	public static AldarLocation toAldarLocation(String server, Location loc)
	{
		return new AldarLocation(server, loc.getWorld().getName()
				,loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
	}
	
	public static AldarLocation toAldarLocation(String server,String world,Vector v)
	{
		return new AldarLocation(server, world
				,v.getX(), v.getY(), v.getZ(),0, 0);
	}
	
	

}
