package kr.kudong.entity.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import kr.kudong.entity.gui.GUI;

public class GUIEventListener implements Listener
{
	@EventHandler
	public void guiClicked(InventoryClickEvent e)
	{
		Player p = (Player)e.getWhoClicked();
		GUI gui = GUI.getGUI(p);
		if(gui == null) return;

		gui.clickEvent(e);
	}

	@EventHandler
	public void guiClose(InventoryCloseEvent e)
	{
		Player p = (Player)e.getPlayer();
		GUI gui = GUI.getGUI(p);
		if(gui == null) return;
		gui.closeEvent(e);
		gui.closeGUI();
	}
}
