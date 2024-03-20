package kr.kudong.entity.gui;

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
import kr.kudong.entity.controller.RidingManager;
import kr.kudong.entity.data.RidingPlayerMap;
import kr.kudong.entity.data.SteerablePreset;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class RidingShopGUI extends GUI
{
	private List<SteerablePreset> list;
	
	public RidingShopGUI(Player player)
	{
		super("§7탈것 상점", player, 27);
	}

	@Override
	void init()
	{
		this.list = this.manager.getPresetList();
		
		int count = 0;
		for(SteerablePreset p : list)
		{
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
			l.add("§7-해당 상품을 좌클릭시 구매가 가능합니다.");
			l.add("§7└가격: §e"+price+"$");
			l.add("§7└최고 스피드: §b"+FORWARD_DEFAULT_MAXSPEED*100+"km/h §7부스트 적용시(§b"+FORWARD_BOOST_MAXSPEED*100+"km/h§7)");
			l.add("§7└민감도: §b"+sensitive+"");
			l.add("§7└접지력: §b"+TRACTION+"");
			
			setItem("§6§l"+displayName+" §6구매",l, item,(short)0,1, count++);
		}
	}

	@Override
	public void clickEvent(InventoryClickEvent e)
	{
        e.setCancelled(true);
        ItemStack item = e.getCurrentItem();
        if(item == null) return;
        if(item.getType().equals(Material.AIR)) return;

        Economy econ = this.manager.getEcon();
        RidingPlayerMap map = this.manager.getRidingPlayerMap();
        
        Player player = getPlayer();
        int slot = e.getRawSlot();
        
        SteerablePreset preset = list.get(slot);
        
        player.closeInventory();
        double money = econ.getBalance(player);
        
        if(map.isExistPreset(uuid, preset))
        {
        	player.sendMessage("§7이미 존재하는 탈것입니다.");
        }
        else if(money < preset.getPRICE())
        {
        	player.sendMessage("§7돈이 부족 합니다. <잔고: §e"+money+"$"+"§7>");
        }
        else
        {
        	
        	EconomyResponse r = econ.withdrawPlayer(player, preset.getPRICE());
        	
            if(r.transactionSuccess()) 
            {
             	RidingCore.getDbService().asyncInsertRidingData(player, preset, (isSuccess)->
            	{
            		if(isSuccess)
            		{
            			map.AddPreset(uuid, preset);
            			
            			Bukkit.getScheduler().runTask(plugin, ()->{
            				
            				player.sendMessage(String.format("§7탈것 §e"+preset.getDISPLAY_NAME()+" §7구매에 §e%s§7가 지불되었고 잔고 §e%s§7가 남았습니다.", econ.format(r.amount), econ.format(r.balance)));
                        	player.sendMessage("§7구매하신 상품은 §b차고§7에서 이용 가능합니다.");
            				
            			});
                      	
            		}
            		else
            		{
            			Bukkit.getScheduler().runTask(plugin, ()->{
            				
            				player.sendMessage("§7거래 오류가 발생하였습니다.");
            				econ.depositPlayer(player, preset.getPRICE());
            				
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
