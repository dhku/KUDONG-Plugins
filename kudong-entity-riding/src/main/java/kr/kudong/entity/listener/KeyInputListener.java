package kr.kudong.entity.listener;

import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import kr.kudong.entity.controller.RidingManager;
import kr.kudong.entity.data.KeyInputState;
import kr.kudong.entity.data.RidingPlayerMap;

public class KeyInputListener extends PacketAdapter
{
	private Logger logger;
	private JavaPlugin plugin;
	private RidingManager manager;
	private RidingPlayerMap map;
	
	public KeyInputListener(Logger logger,JavaPlugin plugin,RidingManager manager)
	{
		super(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.STEER_VEHICLE);
		this.logger = logger;
		this.plugin = plugin;
		this.manager = manager;
		this.map = manager.getRidingPlayerMap();
	}
	
	@Override
	public void onPacketReceiving(PacketEvent event)
	{
		PacketContainer container = event.getPacket();
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		float sidewalks = container.getFloat().read(0);
		float forward = container.getFloat().read(1); 
		boolean shift = container.getBooleans().read(1);
		
		if(shift == true)
			event.setCancelled(true);
		
//		System.out.println("forward: "+ forward);
//		System.out.println("sidewalks: "+ sidewalks);
//		System.out.println("shift: "+ shift);
//		System.out.println("=======================");
		
		if(this.map.containsPlayerInput(uuid))
		{
			KeyInputState state = this.map.getRidingPlayerInput(uuid);
			state.setState(forward, sidewalks, shift);
		}
	}
}
