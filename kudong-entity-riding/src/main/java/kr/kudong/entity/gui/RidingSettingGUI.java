package kr.kudong.entity.gui;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import kr.kudong.entity.data.RidingPlayerMap;
import kr.kudong.entity.data.RidingPlayerSetting;
import kr.kudong.entity.data.SteerableEntity;

public class RidingSettingGUI extends GUI
{
	private RidingPlayerMap map;
	
	public RidingSettingGUI(Player player)
	{
		super("§7탈것 설정", player, 27);
	}

	@Override
	void init()
	{
		this.map = this.manager.getRidingPlayerMap();
		RidingPlayerSetting ps = this.map.getPlayerSetting(uuid);
		
        setItem("§a§l탑승시 속도계 옆 좌표 표시", Arrays.asList("","§7-탈것 탑승시 표시되는 좌표를 §a활성화§7/§c비활성화 §7합니다.","§7-현재 상태 : "+(ps.isPositionVisible()?"§a활성화":"§c비활성화")), Material.COMPASS,(short)0,1,11);
        setItem("§a§l캐주얼 모드 변경", Arrays.asList("","§7-운전을 캐주얼 모드로 §a활성화§7/§c비활성화 §7합니다.","§7-현재 상태 : "+(ps.isCasualMode()?"§a활성화":"§c비활성화")), Material.OAK_BOAT,(short)0,1,13);
        setItem("§a§l메인 메뉴로 돌아가기", Arrays.asList("","§7-메인 메뉴로 돌아갑니다."), Material.CHEST,(short)0,1,15);
	}

	@Override
	public void clickEvent(InventoryClickEvent e)
	{
        e.setCancelled(true);
        ItemStack item = e.getCurrentItem();
        if(item == null) return;
        if(item.getType().equals(Material.AIR)) return;

        Player p = getPlayer();
        int slot = e.getRawSlot();
        RidingPlayerSetting ps = this.map.getPlayerSetting(uuid);
        SteerableEntity entity = this.map.getEntity(uuid);
        
        switch(slot) {

            case 11:
            	if(ps.isPositionVisible())
            	{
            		ps.setPositionVisible(false);
            		if(entity != null) entity.setPositionVisible(false);
            	}	
            	else
            	{
            		ps.setPositionVisible(true);
            		if(entity != null) entity.setPositionVisible(true);
            	}	
            	this.refresh();
                break;
            case 13:
            	if(ps.isCasualMode())
            	{
            		ps.setCasualMode(false);
            		if(entity != null) 
            		{
            			Vector v = entity.getEntity().getLocation().getDirection().clone();
            			entity.setInertiaDirection(v);
            			entity.setCasualMode(false);
            		}
            	}	
            	else
            	{
            		ps.setCasualMode(true);
            		if(entity != null) entity.setCasualMode(true);
            	}	
            	this.refresh();
                break;
            case 15:
            	this.closeGUI();
            	new RidingMainGUI(this.getPlayer()).openGUI();
                break;
        }

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
