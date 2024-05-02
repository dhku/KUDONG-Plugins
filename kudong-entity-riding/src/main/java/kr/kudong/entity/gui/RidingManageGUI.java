package kr.kudong.entity.gui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import kr.kudong.entity.RidingCore;
import kr.kudong.entity.data.RidingPlayerMap;
import kr.kudong.entity.data.SteerableEntity;
import kr.kudong.entity.data.SteerablePreset;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class RidingManageGUI extends RidingGUI
{
	private RidingPlayerMap map;
	private List<SteerablePreset> list;
	
	public RidingManageGUI(Player player)
	{
		super("§7탈것 정리", player, 27);
	}

	@Override
	void init()
	{
		this.inv.clear();
		this.map = this.manager.getRidingPlayerMap();
		this.list = this.map.getPurchasedList(uuid);
		
		if(this.list == null) return;
		
		int count = 0;
		for(SteerablePreset p : list)
		{
			if(count == 26) break;
			
			int customID = p.getCustomModelDataID();
			String displayName = p.getDISPLAY_NAME();
			
			double price = p.getPRICE();
			
			ItemStack item = new ItemStack(Material.DIAMOND_HOE);
			ItemMeta meta = item.getItemMeta();
			meta.setCustomModelData(customID);
			item.setItemMeta(meta);
			
			List<String> l = new ArrayList<>();
			
			l.add("");
			l.add("§7-좌클릭시 해당 물건을 판매합니다.");
			l.add("§7-클릭시 반환되는 가격: §e"+RidingCore.formatter.format(price*0.8)+"원");
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
        Economy econ = this.manager.getEcon();
        
        if(map.isExistPreset(uuid, preset))
        {
        	
        	EconomyResponse r = econ.depositPlayer(player, preset.getPRICE() * 0.8);
        	
            if(r.transactionSuccess()) 
            {
             	RidingCore.getDbService().asyncDeleteRidingData(player, preset, (isSuccess)->
            	{
            		if(isSuccess)
            		{
            			Bukkit.getScheduler().runTask(plugin, ()->{
            				map.removePresetInPurchasedData(uuid, preset);
            				player.sendMessage(String.format("§7탈것 §e"+preset.getDISPLAY_NAME()+" §7판매에 §e%s원§7이 §a입금§7되었고 잔고 §e%s원§7이 되었습니다.", RidingCore.formatter.format(r.amount), RidingCore.formatter.format(r.balance)));
            				this.refresh();
            				
            			});
                      	
            		}
            		else
            		{
            			Bukkit.getScheduler().runTask(plugin, ()->{
            				
            				player.sendMessage("§7거래 오류가 발생하였습니다.");
            				econ.withdrawPlayer(player, preset.getPRICE() * 0.8);
            				
            			});
            			
            		}

            	});

            } else {
            	player.sendMessage(String.format("거래 오류가 발생하였습니다. 에러: %s", r.errorMessage));
            }
       
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
