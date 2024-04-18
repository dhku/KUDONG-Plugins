package kr.kudong.towny.dynmap.marker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.util.Vector;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import kr.kudong.towny.dynmap.util.TownyDynmapMathUtil;

public class DivisionAreaMarker implements IDivisionMarker
{
	private final MarkerSet mark;
	private final MarkerAPI markerapi;
	private final List<Marker> pinMarker;
	private Marker iconMarker;
	private AreaMarker areaMarker;
	private List<Vector> points;
	private String world;
	private String name;
	
	public DivisionAreaMarker(String name,List<Vector> points, String world, MarkerSet mark,MarkerAPI markerapi)
	{
		this.name = name;
		this.points = points;
		this.mark = mark;
		this.markerapi = markerapi;
		this.world = world;
		this.pinMarker = new ArrayList<>();
	}
	
	@Override
	public void drawPaint()
	{
		if(this.areaMarker != null) this.areaMarker.deleteMarker();
		if(this.iconMarker != null) this.iconMarker.deleteMarker();
		
		MarkerIcon icon1 = this.markerapi.getMarkerIcon("house");
		MarkerIcon icon2 = this.markerapi.getMarkerIcon("pin");
		
		Vector center = TownyDynmapMathUtil.getCenterLocation(points);

		double[] a = {0};
		double[] b = {0};
		
		this.areaMarker = this.mark.createAreaMarker("town_"+UUID.randomUUID(), "소유주\n["+name+"]" , false, world, a, b, true);

		if(this.areaMarker != null)
		{
			this.iconMarker = this.mark.createMarker("center_"+UUID.randomUUID(),  "소유주\n["+name+"]" , false, world, center.getX(), 0, center.getZ(),
					icon1, false);
			
			areaMarker.deleteCorner(0);

			int count = 0;
			
			for(Vector v : points)
			{
				areaMarker.setCornerLocation(count, v.getX(), v.getZ());
				this.pinMarker.add(this.mark.createMarker(v.getX()+"_"+v.getZ()
				, "좌표:"+(int)(v.getX()/16)+"_"+(int)(v.getZ()/16)
				, false
				, world 
				, v.getX()
				, 0
				, v.getZ()
				, icon2
				, false));
				count++;
			}
			
		}

	}
	
	public void updateSpawnIcon(double x, double z)
	{
		String areaname = "test";
		MarkerIcon icon1 = this.markerapi.getMarkerIcon("house");
		if(this.iconMarker != null) this.iconMarker.deleteMarker();
		this.iconMarker = this.mark.createMarker(areaname+"center",  areaname , false, world, x, 0, z,
				icon1, false);
	}

	@Override
	public void removePaint()
	{
		if(this.areaMarker != null) this.areaMarker.deleteMarker();
		if(this.iconMarker != null) this.iconMarker.deleteMarker();
		this.pinMarker.forEach(m -> {
			if(m != null)m.deleteMarker();
		});
		this.pinMarker.clear();
	}

}
