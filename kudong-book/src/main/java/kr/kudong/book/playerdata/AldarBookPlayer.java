package kr.kudong.book.playerdata;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import kr.kudong.book.api.AldarBook;

public class AldarBookPlayer
{
	
	public Player player;
	public final UUID uuid;
	public AldarBook openedBook;
	public float pitch;
	public float yaw;
	
	public AldarBookPlayer(Player player) {
		
		this.player = player;
		this.uuid = player.getUniqueId();
		this.openedBook = null;
		
	}

	
	public AldarBook getOpenedBook()
	{
		return openedBook;
	}

	public void setOpenedBook(AldarBook openedBook)
	{
		this.openedBook = openedBook;
	}


}
