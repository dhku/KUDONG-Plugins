package kr.kudong.entity.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import kr.kudong.entity.data.RidingPlayerMap;
import kr.kudong.entity.data.RidingPlayerSetting;
import kr.kudong.entity.data.SteerableEntity;
import kr.kudong.entity.data.SteerablePreset;

public class RidingStorageGUI extends RidingGUI
{
	private RidingPlayerMap map;
	private List<SteerablePreset> list;
	
	public RidingStorageGUI(Player player)
	{
		super("§7운전하기", player, 27);
	}

	@Override
	void init()
	{
		this.map = this.manager.getRidingPlayerMap();
		this.list = this.map.getPurchasedList(uuid);
		
		if(this.list == null) return;
		
		int count = 0;
		for(SteerablePreset p : list)
		{
			if(count == 26) break;
			
			int customID = p.getCustomModelDataID();
			String displayName = p.getDISPLAY_NAME();
			
			double FORWARD_DEFAULT_MAXSPEED = p.getFORWARD_DEFAULT_MAXSPEED();
			double FORWARD_BOOST_MAXSPEED = p.getFORWARD_BOOST_MAXSPEED();
			double TRACTION = p.getTRACTION();
			double price = p.getPRICE();
			float sensitive = p.getSIDE_STEER_SENSIVITY();
			
			ItemStack item = new ItemStack(Material.DIAMOND_HOE);
			ItemMeta meta = item.getItemMeta();
			meta.setCustomModelData(customID);
			item.setItemMeta(meta);
			
			List<String> l = new ArrayList<>();
			
			l.add("");
			l.add("§7-해당 탈것을 좌클릭시 탑승을 시작합니다.");
			l.add("§7-최고 스피드: §b"+FORWARD_DEFAULT_MAXSPEED*100+"km/h §7부스트 적용시(§b"+FORWARD_BOOST_MAXSPEED*100+"km/h§7)");
			l.add("§7-민감도: §b"+sensitive+"");
			l.add("§7-접지력: §b"+TRACTION+"");
			l.add("§7-가격: §e"+price+"$");
			
			setItem("§6§l"+displayName+"§6",l, item,(short)0,1, count++);
		}
		
		setItem("§a§l메인 메뉴로 돌아가기", Arrays.asList("","§7-메인 메뉴로 돌아갑니다."), Material.CHEST,(short)0,1,26);
	}

	@Override
	public void clickEvent(InventoryClickEvent e)
	{
        e.setCancelled(true);
        ItemStack item = e.getCurrentItem();
        if(item == null) return;
        if(item.getType().equals(Material.AIR)) return;

        Player player = getPlayer();
        int slot = e.getRawSlot();
        
        if(slot == 26) 
        {
        	this.closeGUI();
        	new RidingMainGUI(player).openGUI();
        	return;
        }
        
        SteerablePreset preset = list.get(slot);
        
        if(player.isInsideVehicle())
        {
        	player.sendMessage("§7먼저 F키를 눌러 타고있는 것에서 하차해 주세요.");
        }
        else if (!this.map.containsEntity(uuid)) 
		{
			SteerableEntity entity = this.manager.getListener().createSteerableEntity(player,preset);
			RidingPlayerSetting ps = this.map.getPlayerSetting(uuid);
			
			entity.setCasualMode(ps.isCasualMode());
			entity.setPositionVisible(ps.isPositionVisible());
			
			this.map.registerEntity(uuid, entity);
			this.map.registerRidingPlayerInput(uuid);
			
			int TaskID = this.manager.getScheduler().startRidingScheduler(player);
			this.map.registerScheduler(uuid, TaskID); 
			
			player.sendMessage("§7성공적으로 "+preset.DISPLAY_NAME+"에 탑승하였습니다.");
			player.sendMessage("§7F키를 눌러 타고있는 것에서 나올 수 있습니다.");
		}
        else
        {
        	player.sendMessage("§7에러! 다시한번 재시도 해주세요.");
        	this.manager.getListener().removeSteerableEntity(player);
        }
        
        player.closeInventory();
    
	}

	@Override
	public void dragEvent(InventoryDragEvent e)
	{

		
	}

	@Override
	public void closeEvent(InventoryCloseEvent e)
	{
		closeGUI();
	}

}