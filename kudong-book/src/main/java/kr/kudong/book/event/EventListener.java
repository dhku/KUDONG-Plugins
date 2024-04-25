package kr.kudong.book.event;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import kr.kudong.book.AldarBookManager;
import kr.kudong.book.api.AldarBook;
import kr.kudong.book.bookInstance.Book;
import kr.kudong.book.playerdata.AldarBookPlayer;
import net.md_5.bungee.api.chat.ClickEvent;

public class EventListener implements Listener
{
	private final Logger logger;
	private final AldarBookManager bookManager;
	
	
	public EventListener(Logger logger , AldarBookManager bookManager){
		this.bookManager = bookManager;
		this.logger = logger;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		
		AldarBookPlayer d = bookManager.getAldarBookPlayer(e.getPlayer().getUniqueId());
		
		if(d == null) {
			bookManager.addPlayerBookData(e.getPlayer());
		}
		else {
			d.player = e.getPlayer();
		}
		
	}

	@EventHandler 
	public void onPlayerLeave(PlayerQuitEvent e) {
		
//		AldarBookPlayer d = bookManager.getPlayerBookData(e.getPlayer().getUniqueId());
//		
//		if(d != null) {
//			d.bookList.clear();
//		}

	}

	
	@EventHandler 
	public void onBookClose(PlayerMoveEvent e) {
		
		AldarBookPlayer d = bookManager.getAldarBookPlayer(e.getPlayer().getUniqueId());

		if(d == null || d.openedBook == null)
			return;

		Location loc = e.getPlayer().getLocation();
		
		if(d.yaw != loc.getYaw() || d.pitch != loc.getPitch()) {
			
			Book book = (Book)(d.openedBook);
			d.openedBook = null;
			book.removeAldarBookPlayer(d);
			if(book.getWatchingPlayers() <= 0) {
				this.logger.log(Level.INFO, "사용되지않는 Book이 삭제되었습니다. ID-<"+book.id+">");
				bookManager.closeAldarBook(book.id);
			}

		}
		
	}
	
	
	
}
