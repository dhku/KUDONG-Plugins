package kr.kudong.book;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import kr.kudong.book.api.AldarBook;
import kr.kudong.book.api.AldarBookAPI;
import kr.kudong.book.bookInstance.Book;
import kr.kudong.book.bookInstance.components.SelectableComponents;
import kr.kudong.book.playerdata.AldarBookPlayer;


public class AldarBookManager implements AldarBookAPI
{
	private final Logger logger;
	private final Plugin plugin;
	private final Map<UUID,AldarBookPlayer> data;
	private final Map<String,Book> bookMap;
	public Map<UUID, SelectableComponents<?>> commandMap;
	private final static ExecutorService threadPool = Executors.newFixedThreadPool(2);
	
	public AldarBookManager(Logger logger,Plugin plugin) {
		this.logger = logger;
		this.plugin = plugin;
		this.bookMap = new HashMap<>();
		commandMap = new HashMap<>();
		data = new HashMap<>();
	}
	
	public void registerBook(String id,AldarBook book) {
		bookMap.put(id, (Book)book);
	}
	
	public void addPlayerBookData(Player player) {
		
		AldarBookPlayer d = new AldarBookPlayer(player);
		data.put(player.getUniqueId(),d);
	}
	
	public void addAllPlayerBookData() {
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.closeInventory();
			addPlayerBookData(player);
		}
		
	}
	
	public void clearAlldata() {
		threadPool.shutdown();
		data.clear();
		bookMap.clear();
		commandMap.clear();
	}
	
	public AldarBookPlayer getAldarBookPlayer(UUID uuid) {
		
		if(data.containsKey(uuid)) return data.get(uuid);
		else return null;
	}

	public void registerComponent(SelectableComponents<?> s)
	{
		commandMap.put(s.uuid, s);
		
	}
	
	public void closeAldarBook(String id) {
		
		synchronized(bookMap) {
			if(bookMap.containsKey(id)) {
				Book book = bookMap.get(id);
						
				for(UUID uuid :book.getButtonList()) {
					commandMap.remove(uuid);
				}
				book.getButtonList().clear();
				book.closeAllPlayerBook();
				bookMap.remove(id);
			}
			
		}
	}
	
	public boolean hasCachedBook(String id) {
		
		if(bookMap.containsKey(id))
			return true;
		else
			return false;
	
	}
	
	public void openBook(AldarBook book,UUID playerUUID) {
		
		AldarBookPlayer playerdata = this.getAldarBookPlayer(playerUUID);
		
		Runnable task = new Runnable() {
			@Override
			public void run()
			{
				synchronized(bookMap) {
					
					AldarBook cache = getAldarBook(book.id);
					if(cache != null)
						((Book)cache).openBook(playerdata);
					else {
						bookMap.put(book.id, (Book)book);
						((Book)book).openBook(playerdata);
					}
					
				}
			}
		};
		
		Future future = threadPool.submit(task);
		
		
	}

	@Override
	public AldarBook createAldarBook(String id)
	{
		return new Book(id);
	}
	
	public AldarBook getAldarBook(String id) {
		
		if(bookMap.containsKey(id))
			return bookMap.get(id);
		else
			return null;
		
	}
	

	public static void asyncPaint(AldarBook book , int page) {
		
		Runnable task = new Runnable() {

			@Override
			public void run()
			{
				book.repaint(page);
			}
			
		};
		
		Future future = threadPool.submit(task);
		
	}
	
}
