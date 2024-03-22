package kr.kudong.entity.gui;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class RidingMainGUI extends GUI
{
	
	public RidingMainGUI(Player player)
	{
		super("§7탈것 메인", player, 36);
	}

	@Override
	void init()
	{
        
		setItem("§a§l도움말",Arrays.asList("","§7-명령어를 확인합니다."), Material.BOOK,(short)0,1,9);
		setItem("§6§l탈것 상점", Arrays.asList("","§7-탈것을 구매할수있는 상점으로 이동합니다."), Material.GOLD_INGOT,(short)0,1,11);
		setItem("§a§l운전", Arrays.asList("","§7-탈것을 이용하실 수 있습니다."), Material.MINECART,(short)0,1,13);
        setItem("§a§l탈것 정리", Arrays.asList("","§7-가지고있는 탈것을 판매하실 수 있습니다.."), Material.ANVIL,(short)0,1,15);
        setHead("§a§l탈것 설정",Arrays.asList("","§7-개인의 탈것 옵션을 변경하실 수 있습니다."), Bukkit.getOfflinePlayer(getPlayer().getUniqueId()),1,17);
        setItem("§a§l창 나가기", Arrays.asList("","§7-해당 창을 나갑니다."), Material.BARRIER,(short)0,1,35);
 
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
      
        switch(slot) {
        	case 9:
                p.sendMessage("§a§l탈것 §f시스템 명령어 도움말");
                p.sendMessage("========================================");
                p.sendMessage("§e/탈것 or /ride or /xkfrjt §6: 탈것 관리 메뉴를 엽니다.");
                p.sendMessage("§e/탈것 도움말 §6: 탈것 명령어를 확인합니다.");
                p.sendMessage("§e/탈것 정보 §6: 플러그인 정보를 확인합니다.");
                getPlayer().closeInventory();
        		break;
            case 11:
            	new RidingShopGUI(getPlayer()).openGUI();
                break;
            case 13:
                new RidingStorageGUI(getPlayer()).openGUI();
                break;
            case 15:
                new RidingManageGUI(getPlayer()).openGUI();
                break;
            case 17:
            	new RidingSettingGUI(getPlayer()).openGUI();
                break;
            case 35:
            	getPlayer().closeInventory();
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
