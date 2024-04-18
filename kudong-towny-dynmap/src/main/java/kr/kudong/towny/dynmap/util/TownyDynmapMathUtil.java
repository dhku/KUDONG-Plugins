package kr.kudong.towny.dynmap.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.util.Vector;


public class TownyDynmapMathUtil
{
	public static Vector getCenterLocation(List<Vector> points) {
		
		float centroidX = 0, centroidY = 0;
		float det = 0, tempDet = 0;
		int j = 0;
		int nVertices = points.size();
		
		for (int i = 0; i < nVertices; i++)
		{
			// closed polygon
			if (i + 1 == nVertices)
				j = 0;
			else
				j = i + 1;

			// compute the determinant
			tempDet = points.get(i).getBlockX() * points.get(j).getBlockZ() - points.get(j).getBlockX() *points.get(i).getBlockZ();
			det += tempDet;

			centroidX += (points.get(i).getBlockX() + points.get(j).getBlockX())*tempDet;
			centroidY += (points.get(i).getBlockZ() + points.get(j).getBlockZ())*tempDet;
		}

		// divide by the total mass of the polygon
		centroidX /= 3*det;
		centroidY /= 3*det;
		
		return new Vector(centroidX,0,centroidY);
		
	}
	
	public static void sortPoints(List<Vector> points) {

		for (int point = 0; point < points.size(); point++)
		{
			
			int nextPoint = (point + 1) % points.size();
			int nextnextPoint = (point + 2) % points.size();
			
			Vector v1 = points.get(point);
			Vector v2 = points.get(nextPoint);
			Vector v3 = points.get(nextnextPoint);
			
			//오목 다각형인지 판별합니다. 
			Vector dir = v2.clone().subtract(v1);
			dir.normalize().multiply(2);
			if(wn_PnPoly(v2.clone().add(dir),points) != 0 ) {
				//Bukkit.getLogger().log(Level.INFO, "[DEBUG] 오목한 다각형이 감지되었습니다.");
				continue;
			}

			int result = ccw(v1.getBlockX(), v1.getBlockZ(),v2.getBlockX(), v2.getBlockZ(), v3.getBlockX(), v3.getBlockZ());
			
			if(result == 0) { //일직선이면
				//Bukkit.getLogger().log(Level.INFO, "[DEBUG] 일직선이 검출되었습니다.");
				continue;
			}else if(result > 0) { //시계방향이면 ok
				//Bukkit.getLogger().log(Level.INFO, "[DEBUG] 정상입니다.");
				return;
			}else { //반시계방향 
				//Bukkit.getLogger().log(Level.INFO, "[DEBUG] 반시계가 검출되었습니다.");
				Collections.reverse(points);
				return;
			}
		
		}

	}
	
	public static int ccw(int x1, int y1, int x2, int y2, int x3, int y3) {
		
	    int temp = x1*y2 + x2*y3 + x3*y1;
	    temp = temp - y1*x2 - y2*x3 - y3*x1;
	    if (temp > 0) {
	        return 1;
	    } else if (temp < 0) {
	        return -1;
	    } else {
	        return 0;
	    }
	}
	
	public static int wn_PnPoly(Vector P, List<Vector> points)
	{

		int wn = 0;

		for (int i = 0; i < points.size(); i++)
		{

			int j = (i + 1) % points.size();

			Vector vec1 = points.get(i);
			Vector vec2 = points.get(j);

			if (vec1.getZ() <= P.getZ())
			{
				if (vec2.getZ() > P.getZ())
					if (isLeft(vec1, vec2, P) > 0)
						++wn;
			} else
			{
				if (vec2.getZ() <= P.getZ())
					if (isLeft(vec1, vec2, P) < 0)
						--wn;
			}
		}

		return wn;
	}
	
	private static int isLeft(Vector P0, Vector P1, Vector P2)
	{
		return (int) ((P1.getX() - P0.getX()) * (P2.getZ() - P0.getZ())
				- (P2.getX() - P0.getX()) * (P1.getZ() - P0.getZ()));
	}
	
	public static final Vector rotateAroundAxisY(Vector v, double angle)
	{

		angle = Math.toRadians(angle);
		double x, z, cos, sin;
		cos = Math.cos(angle);
		sin = Math.sin(angle);
		x = v.getX() * cos + v.getZ() * sin;
		z = v.getX() * -sin + v.getZ() * cos;

		
		return v.setX(x).setZ(z);
	}
}
