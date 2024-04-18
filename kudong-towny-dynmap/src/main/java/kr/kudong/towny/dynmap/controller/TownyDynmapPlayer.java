package kr.kudong.towny.dynmap.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import kr.kudong.towny.dynmap.marker.DivisionAreaMarker;

public class TownyDynmapPlayer
{
	private String name;
	private UUID uuid;
	private List<DivisionAreaMarker> marker;
	
	public TownyDynmapPlayer(Player player)
	{
		this.uuid = player.getUniqueId();
		this.name = player.getName();
		this.marker = new ArrayList<>();
	}
	
	public TownyDynmapPlayer(String name,UUID uuid)
	{
		this.uuid = uuid;
		this.name = name;
		this.marker = new ArrayList<>();
	}
	
	public void addMarker(DivisionAreaMarker m)
	{
		marker.add(m);
	}
	
	public void drawPaint()
	{
		for(DivisionAreaMarker m : marker)
		{
			m.drawPaint();
		}
	}
	
	public void clearMarker()
	{
		for(DivisionAreaMarker m : marker)
		{
			m.removePaint();
		}
		marker.clear();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public UUID getUuid()
	{
		return uuid;
	}

	public void setUuid(UUID uuid)
	{
		this.uuid = uuid;
	}

}
