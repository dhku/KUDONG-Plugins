package kr.kudong.common.basic.util;

import java.io.Serializable;

//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.World;

public class AldarLocation implements Serializable
{
	private static final long serialVersionUID = -9069288393913114606L;
	
	public final String server;
	public final String world;
	public final double x;
	public final double y;
	public final double z;
	public final float yaw;
	public final float pitch;
	
	public AldarLocation(String server, String world, double x, double y, double z, float yaw, float pitch)
	{
		this.server = server;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public AldarLocation(String server, String world, double x, double y, double z)
	{
		this.server = server;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = 0;
		this.pitch = 0;
	}
	
	public AldarLocation(String server, AldarLocation loc)
	{
		this.server = server;
		this.world = loc.world;
		this.x = loc.x;
		this.y = loc.y;
		this.z = loc.z;
		this.yaw = loc.yaw;
		this.pitch = loc.pitch;
	}
	
	public static AldarLocation deserialize(String raw)
	{
		String arr[] = raw.split(",");
		AldarLocation loc = null;
		try
		{
			String server = arr[0];
			String world = arr[1];
			double x = Double.parseDouble(arr[2]);
			double y = Double.parseDouble(arr[3]);
			double z = Double.parseDouble(arr[4]);
			float yaw = Float.parseFloat(arr[5]);
			float pitch = Float.parseFloat(arr[6]);
			loc = new AldarLocation(server, world, x, y, z, yaw, pitch);
		}
		catch(Exception e){}
		
		return loc;
	}
	
	public String serialize()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(this.server).append(',');
		builder.append(this.world).append(',');
		builder.append(this.x).append(',');
		builder.append(this.y).append(',');
		builder.append(this.z).append(',');
		builder.append(this.yaw).append(',');
		builder.append(this.pitch);
		return builder.toString();	
	}
	
	@Override
	public String toString()
	{
		return this.serialize();
	}
	
//	public Location toLocation() {
//		
//		World w = Bukkit.getWorld(world);
//		return new Location(w,x,y,z,yaw,pitch);
//		
//	}
	
	public AldarLocation clone()
	{
		return new AldarLocation(this.server,this);
	}
	
	public static AldarLocation fromCommandString(String cmdarg)
	{
		String[] argarr = cmdarg.split(",");
		if(!(argarr.length == 5 || argarr.length == 7))
		{
			return null;
		}


		String server;
		String world;
		double x,y,z;
		float yaw = 0, pitch = 0;
		
		server = argarr[0];
		world = argarr[1];
		try
		{
			x = Double.parseDouble(argarr[2]);
			y = Double.parseDouble(argarr[3]);
			z = Double.parseDouble(argarr[4]);
			
			if(argarr.length == 7)
			{
				yaw = Float.parseFloat(argarr[5]);
				pitch = Float.parseFloat(argarr[6]);
			}
			
		}
		catch(NumberFormatException e)
		{
			return null;
		}
		AldarLocation loc = null;
		loc = new AldarLocation(server, world, x, y, z, yaw, pitch);
		return loc;
	}
	
	public static void main(String[] args)
	{
		AldarLocation loc = new AldarLocation("server", "world", 1.20, 2.10, 3.30, 10F, 10.0F);
		AldarLocation c = new AldarLocation("server", "world", 1.20, 2.10, 3.30);
		String str = c.serialize();
		System.out.println(str);
		AldarLocation locds = AldarLocation.deserialize(str);
		System.out.println(locds);
		
		AldarLocation cmdString1 = AldarLocation.fromCommandString("server,world,3.3,100,20.5,0.1,0.3");
		System.out.println(cmdString1);
		AldarLocation cmdString2 = AldarLocation.fromCommandString("server,world,3.3,100,20.5");
		System.out.println(cmdString2);
	}
	
}
