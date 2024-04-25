package kr.kudong.book.bookInstance;

import java.util.List;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import kr.kudong.book.api.AldarBook;
import kr.kudong.book.playerdata.AldarBookPlayer;


public class Book extends AldarBook
{

	private final List<AldarBookPlayer> playerdataList;
 
	
	public Book(String id)
	{
		super(id);
		this.playerdataList = new CopyOnWriteArrayList<>();

	}

	public void removeAldarBookPlayer(AldarBookPlayer playerdata) {
		playerdataList.remove(playerdata);
	}
	
	public int getWatchingPlayers() {
		return playerdataList.size();
	}
	
	public synchronized void openBook(AldarBookPlayer playerdata) {
		
		
		if(!playerdataList.contains(playerdata))
			playerdataList.add(playerdata);
		
		playerdata.yaw = playerdata.player.getLocation().getYaw();
		playerdata.pitch = playerdata.player.getLocation().getPitch();
		playerdata.openedBook = this;
		
		if(book == null)
			this.repaint(0);
		else {
			Bukkit.getLogger().log(Level.INFO, "캐싱된 Book 데이터를 불러왔습니다. ID-<"+this.id+">");
			playerdata.player.openBook(book);
		}
		
	}
	
	@Override
	public synchronized void repaint(int page)
	{

		book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bookMeta = (BookMeta)book.getItemMeta();
		update(page ,book,bookMeta); 

		for(AldarBookPlayer data : playerdataList) {
				Player player = data.player;
				player.openBook(book);
		}

	}
	
	
//	public void addComponentRealTime(Component component)
//	{
//		int page = component.page;
//		
//		if(instance.containsKey(page)) { //페이지가 존재하면 
//			
//			instance.get(page).put(component.id , component);
//			
//		}
//		else {
//			instance.put(page, new HashMap<>());
//			instance.get(page).put(component.id , component);
//		}
//		book = null;
//		repaint(0); 
//	}
//	
//
//	public void removeComponentRealTime(int page, String id)
//	{
//		instance.get(page).remove(id);
//		book = null;
//		repaint(0); 
//	}
	
	public void closeAllPlayerBook() {
		
		for(AldarBookPlayer data : playerdataList) {
			
			if(data.openedBook == this)
				data.player.closeInventory();

		}

	}


}
