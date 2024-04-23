package kr.kudong.entity.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import kr.kudong.entity.gui.RidingGUI;

public class GUIEventListener implements Listener
{
	@EventHandler
	public void guiClicked(InventoryClickEvent e)
	{
		Player p = (Player)e.getWhoClicked();
		RidingGUI ridingGUI = RidingGUI.getGUI(p);
		if(ridingGUI == null) return;

		ridingGUI.clickEvent(e);
	}

	@EventHandler
	public void guiClose(InventoryCloseEvent e)
	{
		Player p = (Player)e.getPlayer();
		RidingGUI ridingGUI = RidingGUI.getGUI(p);
		if(ridingGUI == null) return;
		ridingGUI.closeEvent(e);
		ridingGUI.closeGUI();
	}
}
